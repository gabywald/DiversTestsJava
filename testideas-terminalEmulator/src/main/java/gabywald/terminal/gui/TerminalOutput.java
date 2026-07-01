package gabywald.terminal.gui;

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
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textPane.setBackground(Color.BLACK);
        textPane.setCaretColor(Color.WHITE);
        
        styleContext = new StyleContext();
        
        defaultStyle = styleContext.addStyle("default", null);
        StyleConstants.setForeground(defaultStyle, Color.WHITE);
        
        errorStyle = styleContext.addStyle("error", null);
        StyleConstants.setForeground(errorStyle, Color.RED);
        
        successStyle = styleContext.addStyle("success", null);
        StyleConstants.setForeground(successStyle, Color.GREEN);
        
        promptStyle = styleContext.addStyle("prompt", null);
        StyleConstants.setForeground(promptStyle, Color.CYAN);
        
        commandStyle = styleContext.addStyle("command", null);
        StyleConstants.setForeground(commandStyle, Color.YELLOW);
        
        textPane.setDocument(new DefaultStyledDocument(styleContext));
    }
    
    public void append(String text) { append(text, defaultStyle); }
    
    public void append(String text, Style style) {
        try {
            Document doc = textPane.getDocument();
            doc.insertString(doc.getLength(), text, style);
            textPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) { e.printStackTrace(); }
    }
    
    public void appendLine(String text)					{ append(text + "\n"); }
    public void appendLine(String text, Style style)	{ append(text + "\n", style); }
    public void appendPrompt(String prompt)				{ append(prompt + " ", promptStyle); }
    public void appendCommand(String command)			{ append(command, commandStyle); }
    public void appendError(String error)				{ appendLine(error, errorStyle); }
    public void appendSuccess(String message)			{ appendLine(message, successStyle); }
    public void clearScreen()							{ textPane.setText(""); }
    
    public JTextPane getTextPane()	{ return textPane; }
    public Style getDefaultStyle()	{ return defaultStyle; }
    public Style getErrorStyle()	{ return errorStyle; }
    public Style getSuccessStyle()	{ return successStyle; }
    public Style getPromptStyle()	{ return promptStyle; }
    public Style getCommandStyle()	{ return commandStyle; }
    
}