package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;
import java.util.List;

/**
 * Commande 'ls' : liste le contenu du répertoire courant.
 */
public class LsCommand implements Command {
    private FileSystem fileSystem;

    public LsCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String execute(String[] args, String stdin) {
        List<String> contents = fileSystem.ls();
        if (contents.isEmpty()) {
            return "";
        }
        return String.join(" ", contents);
    }
}