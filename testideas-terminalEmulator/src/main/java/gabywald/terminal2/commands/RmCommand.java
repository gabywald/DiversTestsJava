package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;

/**
 * Commande 'rm' : supprime un fichier ou un répertoire.
 */
public class RmCommand implements Command {
    private FileSystem fileSystem;

    public RmCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String execute(String[] args, String stdin) {
        if (args.length == 0) {
            return "Usage: rm <filename|directory>";
        }
        if (fileSystem.rm(args[0])) {
            return "";
        }
        return "Erreur: Fichier ou répertoire introuvable ou non vide.";
    }
}
