package gabywald.terminal2.editors;

import gabywald.terminal2.FileSystem;
import gabywald.terminal2.TerminalEmulator;
import java.util.ArrayList;
import java.util.List;

/**
 * Éditeur vim adapté à Swing (simplifié).
 * Supporte le mode commande et le mode insertion.
 * @author Gabriel Chandesris (2026)
 */
public class VimEditorSwing implements TextEditorSwing {
    private TerminalEmulator terminal;
    private FileSystem fileSystem;
    private String fileName;
    private List<String> lines;
    private int currentLineIndex;
    private boolean insertMode = false;

    /**
     * Constructeur.
     * @param terminal TerminalEmulator pour afficher les messages.
     * @param fileSystem Système de fichiers.
     * @param fileName Nom du fichier à éditer.
     */
    public VimEditorSwing(TerminalEmulator terminal, FileSystem fileSystem, String fileName) {
        this.terminal = terminal;
        this.fileSystem = fileSystem;
        this.fileName = fileName;
        this.lines = new ArrayList<>();
        this.currentLineIndex = 0;

        if (fileSystem.exists(fileName)) {
            String content = fileSystem.cat(fileName);
            for (String line : content.split("\n")) { lines.add(line); }
        } else { lines.add(""); }
    }

    @Override
    public void start() {
        this.terminal.appendToOutput("--- Vim: Édition de '" + fileName + "' ---\n");
        this.terminal.appendToOutput("Mode: COMMANDE | Tapez 'i' pour passer en mode insertion\n");
        this.terminal.appendToOutput("Commandes: :wq (sauvegarder), :q! (quitter), :esc (mode commande)\n");
        displayCurrentLine();
    }

    /**
     * Affiche la ligne courante.
     */
    private void displayCurrentLine() {
    	this.terminal.appendToOutput("vim [" + (this.insertMode ? "INSERT" : "COMMAND") + "] > " + this.lines.get(currentLineIndex) + "\n");
    }

    @Override
    public String handleInput(String input) {
        if (this.insertMode) {
            if (input.equals(":esc")) {
            	this.insertMode = false;
                this.terminal.appendToOutput("Mode: COMMANDE\n");
                displayCurrentLine();
                return null;
            } else {
                lines.set(currentLineIndex, lines.get(currentLineIndex) + input);
                displayCurrentLine();
                return null;
            }
        } else {
            if (input.equals("i")) {
            	this.insertMode = true;
                this.terminal.appendToOutput("Mode: INSERT\n");
                this.displayCurrentLine();
                return null;
            } else if (input.equals(":wq")) {
                return "SAVE:" + String.join("\n", lines);
            } else if (input.equals(":q!")) {
                return "CANCEL";
            } else if (input.equals(":esc")) {
            	this.displayCurrentLine();
                return null;
            } else {
            	this.terminal.appendToOutput("Commande inconnue. Tapez 'i' pour éditer.\n");
            	this.displayCurrentLine();
                return null;
            }
        }
    }

    @Override
    public String getName() { return "vim"; }
}
