package gabywald.terminal2.editors;

import gabywald.terminal2.FileSystem;
import gabywald.terminal2.TerminalEmulator;
import java.util.ArrayList;
import java.util.List;

/**
 * Éditeur vim adapté à Swing (simplifié).
 * Supporte le mode commande et le mode insertion.
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
            for (String line : content.split("\n")) {
                lines.add(line);
            }
        } else {
            lines.add("");
        }
    }

    @Override
    public void start() {
        terminal.appendToOutput("--- Vim: Édition de '" + fileName + "' ---\n");
        terminal.appendToOutput("Mode: COMMANDE | Tapez 'i' pour passer en mode insertion\n");
        terminal.appendToOutput("Commandes: :wq (sauvegarder), :q! (quitter), :esc (mode commande)\n");
        displayCurrentLine();
    }

    /**
     * Affiche la ligne courante.
     */
    private void displayCurrentLine() {
        terminal.appendToOutput("vim [" + (insertMode ? "INSERT" : "COMMAND") + "] > " + lines.get(currentLineIndex) + "\n");
    }

    @Override
    public String handleInput(String input) {
        if (insertMode) {
            if (input.equals(":esc")) {
                insertMode = false;
                terminal.appendToOutput("Mode: COMMANDE\n");
                displayCurrentLine();
                return null;
            } else {
                lines.set(currentLineIndex, lines.get(currentLineIndex) + input);
                displayCurrentLine();
                return null;
            }
        } else {
            if (input.equals("i")) {
                insertMode = true;
                terminal.appendToOutput("Mode: INSERT\n");
                displayCurrentLine();
                return null;
            } else if (input.equals(":wq")) {
                return "SAVE:" + String.join("\n", lines);
            } else if (input.equals(":q!")) {
                return "CANCEL";
            } else if (input.equals(":esc")) {
                displayCurrentLine();
                return null;
            } else {
                terminal.appendToOutput("Commande inconnue. Tapez 'i' pour éditer.\n");
                displayCurrentLine();
                return null;
            }
        }
    }

    @Override
    public String getName() {
        return "vim";
    }
}
