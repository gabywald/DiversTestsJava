package gabywald.terminal3.clientside.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * Terminal output with color support
 * @author Gabriel Chandesris (2026)
 */
public class TerminalOutput {
	
    private JTextPane textPane;
    private StyleContext styleContext;
    private Style defaultStyle;
    private Style errorStyle;
    private Style successStyle;
    private Style promptStyle;
    private Style commandStyle;
    
    public TerminalOutput() {
        this.textPane = new JTextPane();
        this.textPane.setEditable(false);
        this.textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        this.textPane.setBackground(Color.BLACK);
        this.textPane.setCaretColor(Color.WHITE); // WHITE
        
        this.styleContext = new StyleContext();
        
        this.defaultStyle = this.styleContext.addStyle("default", null);
        StyleConstants.setForeground(this.defaultStyle, Color.WHITE); // WHITE
        
        this.errorStyle = this.styleContext.addStyle("error", null);
        StyleConstants.setForeground(this.errorStyle, Color.RED);
        
        this.successStyle = this.styleContext.addStyle("success", null);
        StyleConstants.setForeground(this.successStyle, Color.GREEN);
        
        this.promptStyle = this.styleContext.addStyle("prompt", null);
        StyleConstants.setForeground(this.promptStyle, Color.CYAN);
        
        this.commandStyle = this.styleContext.addStyle("command", null);
        StyleConstants.setForeground(this.commandStyle, Color.YELLOW);
        
        this.textPane.setDocument(new DefaultStyledDocument(styleContext));
    }
    
    public void append(String text) { this.append(text, this.defaultStyle); }
    
    public void append(String text, Style style) {
        try {
            Document doc = this.textPane.getDocument();
            doc.insertString(doc.getLength(), text, style);
            this.textPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) { e.printStackTrace(); }
    }
    
    public void appendLine(String text)					{ this.append(text + "\n"); }
    public void appendLine(String text, Style style)	{ this.append(text + "\n", style); }
    public void appendPrompt(String prompt)				{ this.append(prompt + " ", this.promptStyle); }
    public void appendCommand(String command)			{ this.append(command, this.commandStyle); }
    public void appendError(String error)				{ this.appendLine(error, this.errorStyle); }
    public void appendSuccess(String message)			{ this.appendLine(message, this.successStyle); }
    public void clearScreen()							{ this.textPane.setText(""); }
    
    public JTextPane getTextPane()	{ return this.textPane; }
    public Style getDefaultStyle()	{ return this.defaultStyle; }
    public Style getErrorStyle()	{ return this.errorStyle; }
    public Style getSuccessStyle()	{ return this.successStyle; }
    public Style getPromptStyle()	{ return this.promptStyle; }
    public Style getCommandStyle()	{ return this.commandStyle; }
    
}