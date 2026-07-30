package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;

/**
 * Commande 'cat' : affiche le contenu d'un fichier ou de stdin.
 * @author Gabriel Chandesris (2026)
 */
public class CatCommand implements Command {
    private FileSystem fileSystem;

    public CatCommand(FileSystem fileSystem) { this.fileSystem = fileSystem; }

    @Override
    public String execute(String[] args, String stdin) {
        if (args.length == 0) {
            if (stdin != null) { return stdin; }
            return "Usage: cat <filename> ou utiliser une redirection (<)";
        }

        StringBuilder output = new StringBuilder();
        for (String fileName : args) {
            if (this.fileSystem.exists(fileName)) {
                output.append(this.fileSystem.cat(fileName));
            } else {
                output.append("Fichier introuvable: ").append(fileName);
            }
        }
        return output.toString();
    }
}
