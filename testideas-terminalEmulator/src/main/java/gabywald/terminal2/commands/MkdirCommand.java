package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;

/**
 * Commande 'mkdir' : crée un nouveau répertoire.
 */
public class MkdirCommand implements Command {
    private FileSystem fileSystem;

    public MkdirCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String execute(String[] args, String stdin) {
        if (args.length == 0) {
            return "Usage: mkdir <directory>";
        }
        if (fileSystem.mkdir(args[0])) {
            return "";
        }
        return "Erreur: Répertoire déjà existant ou nom invalide.";
    }
}
