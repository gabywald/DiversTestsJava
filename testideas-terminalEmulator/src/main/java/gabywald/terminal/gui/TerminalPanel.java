package gabywald.terminal.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Terminal panel with output display
 */
public class TerminalPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextArea outputArea;
    private JScrollPane scrollPane;
    
    public TerminalPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.WHITE);
        outputArea.setCaretColor(Color.WHITE);
        
        scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void appendOutput(String text) {
        outputArea.append(text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    public void clearScreen() { outputArea.setText(""); }
    public void setOutputText(String text) { outputArea.setText(text); }
    public String getOutputText() { return outputArea.getText(); }
    public JTextArea getOutputArea() { return outputArea; }
}

