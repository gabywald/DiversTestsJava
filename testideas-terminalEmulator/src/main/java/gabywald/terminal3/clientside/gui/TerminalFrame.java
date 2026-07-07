package gabywald.terminal3.clientside.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Main terminal window with Swing GUI
 * @author Gabriel Chandesris (2026)
 */
public class TerminalFrame extends JFrame {
	
    private static final long serialVersionUID = 1L;
    
    private static TerminalFrame instance = null;
    
    public static TerminalFrame getInstance() {
    	if (TerminalFrame.instance == null) 
    		{ TerminalFrame.instance = new TerminalFrame(); }
    	return TerminalFrame.instance;
    }
    
    private TerminalState state;
    private JTextArea outputArea;
    private JTextField inputField;
    private JScrollPane scrollPane;
    private JLabel promptLabel;
    
    private TerminalFrame() { this(new TerminalState()); }
    
    public TerminalFrame(TerminalState state) {
        this.state = state;
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
        
        mainPanel.add(this.scrollPane, BorderLayout.CENTER);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        this.promptLabel = new JLabel(this.state.getPrompt() + " ", SwingConstants.RIGHT);
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
    
    public void appendOutput(String text) {
    	this.outputArea.append(text);
    	this.outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    void clearScreen()		{ this.outputArea.setText(""); }
    
    void clearInputLine()	{ this.inputField.setText(""); }
    
    void updatePrompt()		{ this.promptLabel.setText(this.state.getPrompt() + " "); }

	JTextField getInputField()		{ return this.inputField; }
	
    public TerminalState getTerminalState()	{ return this.state; }
    public void setState(TerminalState state) { this.state = state;this.updatePrompt(); }

}
