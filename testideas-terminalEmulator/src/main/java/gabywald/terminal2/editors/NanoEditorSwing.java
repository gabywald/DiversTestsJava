package gabywald.terminal2.editors;

import gabywald.terminal2.FileSystem;
import gabywald.terminal2.TerminalEmulator;
import java.util.ArrayList;
import java.util.List;

/**
 * Éditeur nano adapté à Swing.
 * Permet d'éditer un fichier ligne par ligne.
 * @author Gabriel Chandesris (2026)
 */
public class NanoEditorSwing implements TextEditorSwing {
    private TerminalEmulator terminal;
    private FileSystem fileSystem;
    private String fileName;
    private List<String> lines;

    /**
     * Constructeur.
     * @param terminal TerminalEmulator pour afficher les messages.
     * @param fileSystem Système de fichiers.
     * @param fileName Nom du fichier à éditer.
     */
    public NanoEditorSwing(TerminalEmulator terminal, FileSystem fileSystem, String fileName) {
        this.terminal = terminal;
        this.fileSystem = fileSystem;
        this.fileName = fileName;
        this.lines = new ArrayList<>();

        if (fileSystem.exists(fileName)) {
            String content = fileSystem.cat(fileName);
            for (String line : content.split("\n")) {
            	this.lines.add(line);
            }
        }
    }

    @Override
    public void start() {
    	this.terminal.appendToOutput("--- Nano: Édition de '" + this.fileName + "' ---\n");
    	this.terminal.appendToOutput("Commandes: :wq (sauvegarder et quitter), :q (quitter)\n");
        if (!lines.isEmpty()) {
        	this.terminal.appendToOutput("Contenu actuel:\n");
            for (String line : this.lines) {
            	this.terminal.appendToOutput(line + "\n");
            }
        }
        this.terminal.appendToOutput("nano> ");
    }

    @Override
    public String handleInput(String input) {
        if (input.equals(":wq")) {
            return "SAVE:" + String.join("\n", this.lines);
        } else if (input.equals(":q")) {
            return "CANCEL";
        } else {
        	this.lines.add(input);
            return null;
        }
    }

    @Override
    public String getName() {
        return "nano";
    }
}
