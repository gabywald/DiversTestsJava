package gabywald.terminal.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import gabywald.terminal.TerminalState;
import gabywald.terminal.commands.Command;
import gabywald.terminal.commands.CommandFactory;
import gabywald.terminal.commands.CommandParser;

/**
 * Main terminal window with Swing GUI
 * @author Gabriel Chandesris (2026)
 */
public class TerminalFrame extends JFrame {
	
    private static final long serialVersionUID = 1L;
    
    private TerminalState state;
    private JTextArea outputArea;
    private JTextField inputField;
    private JScrollPane scrollPane;
    private JLabel promptLabel;
    
    public TerminalFrame() {
        this(new TerminalState());
    }
    
    public TerminalFrame(TerminalState state) {
        this.state = state;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Terminal Emulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.WHITE);
        outputArea.setCaretColor(Color.WHITE);
        
        scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        promptLabel = new JLabel(state.getPrompt() + " ", SwingConstants.RIGHT);
        promptLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        promptLabel.setForeground(Color.WHITE);
        promptLabel.setBackground(Color.BLACK);
        promptLabel.setOpaque(true);
        
        inputField = new JTextField();
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        inputField.addActionListener(new CommandActionListener());
        inputField.addKeyListener(new HistoryKeyListener());
        
        inputPanel.add(promptLabel, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        printWelcomeMessage();
        inputField.requestFocusInWindow();
    }
    
    private void printWelcomeMessage() {
        appendOutput("=============================================\n");
        appendOutput("   TERMINAL EMULATOR - Java 8 / Swing\n");
        appendOutput("   Type 'help' for a list of available commands\n");
        appendOutput("   Type 'exit' to quit\n");
        appendOutput("=============================================\n\n");
    }
    
    public void appendOutput(String text) {
        outputArea.append(text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    public void clearScreen() {
        outputArea.setText("");
    }
    
    private void clearInputLine() {
        inputField.setText("");
    }
    
    private void updatePrompt() {
        promptLabel.setText(state.getPrompt() + " ");
    }
    
//    public TerminalState getState() { return this.state; }
//    public void setState(TerminalState state) { this.state = state; updatePrompt(); }
    
    private class CommandActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = inputField.getText().trim();
            if (command.isEmpty()) {
                clearInputLine();
                return;
            }
            
            state.addToHistory(command);
            state.resetHistoryIndex();
            appendOutput(state.getPrompt() + " " + command + "\n");
            
            String[] parts = CommandParser.parse(command);
            if (parts.length == 0) {
                clearInputLine();
                return;
            }
            
            String commandName = parts[0];
            String[] cmdArgs = new String[parts.length - 1];
            System.arraycopy(parts, 1, cmdArgs, 0, cmdArgs.length);
            
            Command cmd = CommandFactory.getCommand(commandName);
            if (cmd == null) {
                appendOutput(commandName + ": command not found\n");
            } else {
                String result = cmd.execute(state, cmdArgs);
                if ("EXIT".equals(result)) {
                    System.exit(0);
                } else if (result.contains("\033[H\033[2J")) {
                    clearScreen();
                    printWelcomeMessage();
                } else {
                    appendOutput(result + "\n");
                }
            }
            
            updatePrompt();
            clearInputLine();
        }
    }
    
    private class HistoryKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                String prevCommand = state.getPreviousCommand();
                if (prevCommand != null) inputField.setText(prevCommand);
                e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                String nextCommand = state.getNextCommand();
                if (nextCommand != null) inputField.setText(nextCommand);
                e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                handleTabCompletion();
                e.consume();
            }
        }
        
        private void handleTabCompletion() {
            String text = inputField.getText();
            String[] parts = CommandParser.parse(text);
            if (parts.length == 0) return;
            
            if (parts.length == 1) {
                String prefix = parts[0];
                String[] commandNames = CommandFactory.getCommandNames();
                for (String cmdName : commandNames) {
                    if (cmdName.startsWith(prefix)) {
                        inputField.setText(cmdName + " ");
                        return;
                    }
                }
            }
        }
    }
    
}
