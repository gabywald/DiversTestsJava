package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;

/**
 * Commande 'touch' : crée un nouveau fichier.
 * @author Gabriel Chandesris (2026)
 */
public class TouchCommand implements Command {
    private FileSystem fileSystem;

    public TouchCommand(FileSystem fileSystem) { this.fileSystem = fileSystem; }

    @Override
    public String execute(String[] args, String stdin) {
        if (args.length == 0) { return "Usage: touch <filename>"; }
        if (this.fileSystem.touch(args[0])) { return ""; }
        return "Erreur: Fichier déjà existant ou nom invalide.";
    }
}
