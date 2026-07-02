package gabywald.terminal2;

import gabywald.terminal2.commands.CommandParser;
import gabywald.terminal2.editors.NanoEditorSwing;
import gabywald.terminal2.editors.TextEditorSwing;
import gabywald.terminal2.editors.VimEditorSwing;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Classe principale pour l'interface graphique de l'émulateur de terminal.
 * Gère l'affichage, la saisie des commandes, et les modes d'édition.
 */
public class TerminalEmulator {
    private JFrame frame;
    private JTextPane outputArea;
    private JTextField inputField;
    private FileSystem fileSystem;
    private CommandParser commandParser;
    private String currentDirectory;

    // Pour le mode édition
    private TextEditorSwing currentEditor;
    private String currentEditFile;
    private boolean isEditing = false;

    /**
     * Constructeur : initialise l'interface graphique et le système de fichiers.
     */
    public TerminalEmulator() {
        fileSystem = new FileSystem();
        commandParser = new CommandParser(fileSystem);
        currentDirectory = fileSystem.getRoot();
        initializeUI();
    }

    /**
     * Initialise l'interface utilisateur (UI).
     */
    private void initializeUI() {
        frame = new JFrame("Terminal Emulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Zone de sortie (non éditable)
        outputArea = new JTextPane();
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.GREEN);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Champ de saisie
        inputField = new JTextField();
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.GREEN);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputField.addActionListener(e -> executeCommand());
        frame.add(inputField, BorderLayout.SOUTH);

        // Afficher le prompt initial
        printPrompt();

        frame.setVisible(true);
    }

    /**
     * Affiche le prompt dans la zone de sortie.
     */
    private void printPrompt() {
        try {
            StyledDocument doc = outputArea.getStyledDocument();
            doc.insertString(doc.getLength(), currentDirectory + " > ", getPromptStyle());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retourne le style pour le prompt.
     */
    private Style getPromptStyle() {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        Style style = sc.addStyle("PromptStyle", null);
        StyleConstants.setForeground(style, Color.GREEN);
        StyleConstants.setBold(style, true);
        return style;
    }

    /**
     * Exécute la commande saisie par l'utilisateur.
     */
    private void executeCommand() {
        String command = inputField.getText().trim();
        inputField.setText("");

        if (command.isEmpty()) {
            printPrompt();
            return;
        }

        appendToOutput(command + "\n");

        if (isEditing) {
            handleEditorInput(command);
            return;
        }

        if (command.startsWith("edit ")) {
            startEditMode(command.substring(5).trim());
            return;
        }

        String output = commandParser.execute(command, currentDirectory);
        if (output.startsWith("MODE_EDIT:")) {
            startEditMode(output.substring(10));
        } else {
            appendToOutput(output + "\n");
        }

        if (command.startsWith("cd ")) {
            currentDirectory = fileSystem.getCurrentDirectory();
        }

        printPrompt();
    }

    /**
     * Démarre le mode édition (nano ou vim).
     */
    private void startEditMode(String args) {
        isEditing = true;
        String[] editArgs = args.split("\\s+");
        String editorType = "nano";
        String fileName;

        if (editArgs.length > 0 && (editArgs[0].equals("--nano") || editArgs[0].equals("--vim"))) {
            editorType = editArgs[0].substring(2);
            if (editArgs.length < 2) {
                appendToOutput("Usage: edit [--nano|--vim] <filename>\n");
                isEditing = false;
                printPrompt();
                return;
            }
            fileName = editArgs[1];
        } else {
            fileName = editArgs[0];
        }

        currentEditFile = fileName;

        if (editorType.equals("nano")) {
            currentEditor = new NanoEditorSwing(this, fileSystem, fileName);
        } else {
            currentEditor = new VimEditorSwing(this, fileSystem, fileName);
        }

        appendToOutput("--- Édition avec " + editorType + " ---\n");
        currentEditor.start();
    }

    /**
     * Gère les entrées en mode édition.
     */
    private void handleEditorInput(String input) {
        String result = currentEditor.handleInput(input);
        if (result != null) {
            if (result.startsWith("SAVE:")) {
                fileSystem.echo(currentEditFile, result.substring(5));
                appendToOutput("Fichier '" + currentEditFile + "' sauvegardé.\n");
            } else if (result.equals("CANCEL")) {
                appendToOutput("Édition annulée.\n");
            }
            endEditMode();
        } else {
            appendToOutput(input + "\n");
        }
    }

    /**
     * Termine le mode édition.
     */
    private void endEditMode() {
        isEditing = false;
        currentEditor = null;
        currentEditFile = null;
        printPrompt();
    }

    /**
     * Ajoute du texte à la zone de sortie.
     */
    public void appendToOutput(String text) {
        try {
            StyledDocument doc = outputArea.getStyledDocument();
            doc.insertString(doc.getLength(), text, null);
            outputArea.setCaretPosition(doc.getLength());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
}
