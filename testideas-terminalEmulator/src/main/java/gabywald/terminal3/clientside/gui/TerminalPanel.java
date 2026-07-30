package gabywald.terminal3.clientside.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Terminal panel with output display
 * @author Gabriel Chandesris (2026)
 */
public class TerminalPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextArea outputArea;
    private JScrollPane scrollPane;
    
    public TerminalPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        
        this.outputArea = new JTextArea();
        this.outputArea.setEditable(false);
        this.outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        this.outputArea.setBackground(Color.BLACK);
        this.outputArea.setForeground(Color.WHITE); // WHITE
        this.outputArea.setCaretColor(Color.WHITE); // WHITE
        
        this.scrollPane = new JScrollPane(this.outputArea);
        this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        this.add(this.scrollPane, BorderLayout.CENTER);
    }
    
    public void appendOutput(String text) {
    	this.outputArea.append(text);
    	this.outputArea.setCaretPosition(this.outputArea.getDocument().getLength());
    }
    
    public void clearScreen() 				{ this.outputArea.setText(""); }
    public void setOutputText(String text)	{ this.outputArea.setText(text); }
    public String getOutputText()			{ return this.outputArea.getText(); }
    public JTextArea getOutputArea()		{ return this.outputArea; }
    
}

