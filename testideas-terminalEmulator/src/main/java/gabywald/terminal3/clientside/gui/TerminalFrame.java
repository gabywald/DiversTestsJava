package gabywald.terminal3.clientside.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import gabywald.global.structures.PairSimple;
import gabywald.terminal3.clientside.TerminalClient;
import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalFile;
import gabywald.terminal3.serverside.filesystem.TerminalNode;

// import gabywald.terminal3.serverside.filesystem.TerminalNode;

/**
 * Main terminal window with Swing GUI
 * @author Gabriel Chandesris (2026)
 */
public class TerminalFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	// KLEIN BLUE : #21177D ; 
	public static Color BLUE_KLEIN = new Color(	Integer.parseInt("21", 16), 
												Integer.parseInt("17", 16), 
												Integer.parseInt("7D", 16) );
	// 3Dnull : #FF06B5 ; 
	public static Color NEON_MAGENTA = new Color(	Integer.parseInt("FF", 16), 
													Integer.parseInt("06", 16), 
													Integer.parseInt("B5", 16) );
	
	private static TerminalFrame instance = null;
	
	public static TerminalFrame getInstance() {
		if (TerminalFrame.instance == null) 
			{ TerminalFrame.instance = new TerminalFrame(); }
		return TerminalFrame.instance;
	}
	
	private TerminalHistory history = new TerminalHistory(); 
	private String currentPrompt = ""; // NOTE starting prompt
	private JTextArea outputArea;
	private JTextField inputField;
	private JScrollPane scrollPane;
	private JLabel promptLabel;
	
	private TerminalFrame() { this("---@---$"); }
	
	public TerminalFrame(String prompt) {
		// this.state = state;
		this.currentPrompt = prompt;
		this.initializeUI();
	}
	
	private void initializeUI() {
		this.setTitle("Terminal Emulator");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		this.outputArea = new JTextArea();
		this.outputArea.setEditable(false);
		this.outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		this.outputArea.setBackground(Color.BLACK);
		this.outputArea.setForeground(Color.WHITE);
		this.outputArea.setCaretColor(Color.WHITE);
		
		this.scrollPane = new JScrollPane(this.outputArea);
		this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		boolean hasDesktop = true;
		if (hasDesktop) {
			JComponent splitPane = this.initializeDesktopPane();
			mainPanel.add(splitPane, BorderLayout.CENTER);
		} else { mainPanel.add(this.scrollPane, BorderLayout.CENTER); }
		
		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		
		this.promptLabel = new JLabel(this.currentPrompt + " ", SwingConstants.RIGHT);
		this.promptLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
		this.promptLabel.setForeground(Color.WHITE);
		this.promptLabel.setBackground(Color.BLACK);
		this.promptLabel.setOpaque(true);
		
		this.inputField = new JTextField();
		this.inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
		this.inputField.setBackground(Color.BLACK);
		this.inputField.setForeground(Color.WHITE);
		this.inputField.setCaretColor(Color.WHITE);
		this.inputField.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		
		this.inputField.addActionListener(new CommandActionListener(this));
		this.inputField.addKeyListener(new HistoryKeyListener(this));
		
		inputPanel.add(this.promptLabel, BorderLayout.WEST);
		inputPanel.add(this.inputField, BorderLayout.CENTER);
		
		mainPanel.add(inputPanel, BorderLayout.SOUTH);
		
		this.add(mainPanel);
		
		// this.printWelcomeMessage();
		this.inputField.requestFocusInWindow();
	}
	
	private JComponent initializeDesktopPane() {
		JDesktopPane desktopPane = new JDesktopPane();
		
		desktopPane.setBackground( TerminalFrame.BLUE_KLEIN );
		
		JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Windows");
        JMenuItem newWindowItem = new JMenuItem("New Window");
        JMenuItem newExplorItem = new JMenuItem("New Explorer");
        
        newWindowItem.addActionListener(new ActionListener() {
            private int windowCount = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new Internal Window
                JInternalFrame internalFrame = new JInternalFrame("Window " + (++windowCount), true, true, true, true);
                internalFrame.setBackground(TerminalFrame.NEON_MAGENTA);
                internalFrame.setVisible(true);
                internalFrame.setSize(300, 200);
                internalFrame.setLocation(50 * windowCount, 50 * windowCount);
                // Add it into Desktop
                desktopPane.add(internalFrame);
            }
        });
        
        newExplorItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	PairSimple<String, String> pwdResult = TerminalClient.getInstance().callCommandServer("pwd");
            	PairSimple<String, String> lslResult = TerminalClient.getInstance().callCommandServer("ls -l");
            	
            	TerminalNode tn = new TerminalDirectory(pwdResult.first, null);
            	Arrays.asList(lslResult.first.split("\n")).stream().forEach( line -> {
            		char firstChar = line.charAt(0);
            		int  lastSpace = line.lastIndexOf(" ");
            		if (firstChar == 'd') {
            			((TerminalDirectory)tn).addChild(new TerminalDirectory(line.substring(lastSpace), null));
            		}
            		if (firstChar == '-') {
            			((TerminalDirectory)tn).addChild(new TerminalFile(line.substring(lastSpace), null));
            		}
            	});
            	
            	JInternalFrame internalFrame = new TerminalNodeFileExplorerFrame(tn, desktopPane);
                internalFrame.setBackground(TerminalFrame.NEON_MAGENTA);
                internalFrame.setTitle("Explorer");
                internalFrame.setVisible(true);
                internalFrame.setSize(300, 200);
                internalFrame.setLocation(10, 10);
            	desktopPane.add(internalFrame);
            }
        });

        menu.add(newWindowItem);
        menu.add(newExplorItem);
        menuBar.add(menu);
        this.setJMenuBar(menuBar);
        
		JSplitPane splitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT,
				desktopPane,
				this.scrollPane
			);
		
		splitPane.setDividerLocation(0); // (this.getWidth() / 2);
		return splitPane;
	}
	
	public void appendOutput(String text) {
		this.outputArea.append(text);
		this.outputArea.setCaretPosition(outputArea.getDocument().getLength());
	}
	
	void clearScreen()		{ this.outputArea.setText(""); }
	
	void clearInputLine()	{ this.inputField.setText(""); }
	
	public void updatePrompt()		{ this.promptLabel.setText(this.currentPrompt + " "); }
	public String getPrompt()		{ return this.currentPrompt; }

	JTextField getInputField()		{ return this.inputField; }
	
	public TerminalHistory getTerminalHistory()	{ return this.history; }

	public void setPrompt(String prompt) { this.currentPrompt = prompt; }

}
