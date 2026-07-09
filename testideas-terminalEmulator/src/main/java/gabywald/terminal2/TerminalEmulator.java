package gabywald.terminal2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import gabywald.terminal2.commands.CommandParser;
import gabywald.terminal2.editors.NanoEditorSwing;
import gabywald.terminal2.editors.TextEditorSwing;
import gabywald.terminal2.editors.VimEditorSwing;

/**
 * Classe principale pour l'interface graphique de l'émulateur de terminal.
 * Gère l'affichage, la saisie des commandes, et les modes d'édition.
 * @author Gabriel Chandesris (2026)
 */
public class TerminalEmulator {
    private JFrame frame;
    private JTextPane outputArea;
    private JTextField inputField;
    private FileSystem fileSystem;
    private CommandParser commandParser;
    private String currentDirectory;

    // For Edition Mode
    private TextEditorSwing currentEditor;
    private String currentEditFile;
    private boolean isEditing = false;

    /**
     * Constructor : initialize graphical Interface and File System
     */
    public TerminalEmulator() {
    	this.fileSystem = new FileSystem();
    	this.commandParser = new CommandParser(this.fileSystem);
    	this.currentDirectory = this.fileSystem.getRoot();
        this.initializeUI();
    }

    /**
     * Initialise User Interface (UI).
     */
    private void initializeUI() {
    	this.frame = new JFrame("Terminal Emulator");
    	this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.frame.setSize(800, 600);
    	this.frame.setLayout(new BorderLayout());

        // Not editable output Zone
    	this.outputArea = new JTextPane();
    	this.outputArea.setEditable(false);
    	this.outputArea.setBackground(Color.BLACK);
    	this.outputArea.setForeground(Color.GREEN);
    	this.outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(this.outputArea);
        this.frame.add(scrollPane, BorderLayout.CENTER);

        // Input Field
        this.inputField = new JTextField();
        this.inputField.setBackground(Color.BLACK);
        this.inputField.setForeground(Color.GREEN);
        this.inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        this.inputField.addActionListener(e -> executeCommand());
        this.frame.add(this.inputField, BorderLayout.SOUTH);

        // Show Welcome Message
        this.printWelcomeMessage();
        // Show Initial Prompt
        this.printPrompt();

        this.frame.setVisible(true);
    }
    
   private void printWelcomeMessage() {
        this.appendToOutput("===============================================\n");
        this.appendToOutput("   TERMINAL EMULATOR (V2) - Java 8 / Swing\n");
        this.appendToOutput("   Type 'help' for a list of available commands\n");
        // this.appendToOutput("   Type 'exit' to quit\n");
        this.appendToOutput("===============================================\n\n");
    }

    /**
     * Show Prompt in Output Zone
     */
    private void printPrompt() {
        try {
            StyledDocument doc = this.outputArea.getStyledDocument();
            doc.insertString(doc.getLength(), currentDirectory + " > ", getPromptStyle());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Return Prompt Stule
     */
    private Style getPromptStyle() {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        Style style = sc.addStyle("PromptStyle", null);
        StyleConstants.setForeground(style, Color.GREEN);
        StyleConstants.setBold(style, true);
        return style;
    }

    /**
     * Execute command given by User
     */
    private void executeCommand() {
        String command = this.inputField.getText().trim();
        this.inputField.setText("");

        if (command.isEmpty()) {
        	this.printPrompt();
            return;
        }

        this.appendToOutput(command + "\n");

        if (this.isEditing) {
        	this.handleEditorInput(command);
            return;
        }

        if (command.startsWith("edit ")) {
        	this.startEditMode(command.substring(5).trim());
            return;
        }

        String output = this.commandParser.execute(command, this.currentDirectory);
        if (output.startsWith("MODE_EDIT:")) 
        	{ this.startEditMode(output.substring(10)); }
        else 
        	{ this.appendToOutput(output + "\n"); }

        if (command.startsWith("cd ")) 
        	{ this.currentDirectory = this.fileSystem.getCurrentDirectory(); }

        this.printPrompt();
    }

    /**
     * Start Editing mode (nano or vim)
     */
    private void startEditMode(String args) {
    	this.isEditing = true;
        String[] editArgs = args.split("\\s+");
        String editorType = "nano";
        String fileName;

        if (editArgs.length > 0 && (editArgs[0].equals("--nano") || editArgs[0].equals("--vim"))) {
            editorType = editArgs[0].substring(2);
            if (editArgs.length < 2) {
            	this.appendToOutput("Usage: edit [--nano|--vim] <filename>\n");
                this.isEditing = false;
                this.printPrompt();
                return;
            }
            fileName = editArgs[1];
        } else { fileName = editArgs[0]; }

        this.currentEditFile = fileName;

        if (editorType.equals("nano")) 
        	{ this.currentEditor = new NanoEditorSwing(this, this.fileSystem, fileName); } 
        else 
        	{ this.currentEditor = new VimEditorSwing(this, this.fileSystem, fileName); }

        this.appendToOutput("--- Edition with '" + editorType + "' ---\n");
        this.currentEditor.start();
    }

    /**
     * Gère les entrées en mode édition.
     */
    private void handleEditorInput(String input) {
        String result = this.currentEditor.handleInput(input);
        if (result != null) {
            if (result.startsWith("SAVE:")) {
            	this.fileSystem.echo(this.currentEditFile, result.substring(5));
                this.appendToOutput("File '" + this.currentEditFile + "' recorded.\n");
            } else if (result.equals("CANCEL")) {
            	this.appendToOutput("Edition Canceled.\n");
            }
            this.endEditMode();
        } else {
        	this.appendToOutput(input + "\n");
        }
    }

    /**
     * End Edtion Mode 
     */
    private void endEditMode() {
    	this.isEditing			= false;
    	this.currentEditor		= null;
    	this.currentEditFile	= null;
    	this.printPrompt();
    }

    /**
     * Add Text to Output Zone
     */
    public void appendToOutput(String text) {
        try {
            StyledDocument doc = this.outputArea.getStyledDocument();
            doc.insertString(doc.getLength(), text, null);
            this.outputArea.setCaretPosition(doc.getLength());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
}
