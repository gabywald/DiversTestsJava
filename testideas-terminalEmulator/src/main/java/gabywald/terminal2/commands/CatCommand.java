package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;

/**
 * Commande 'cat' : affiche le contenu d'un fichier ou de stdin.
 */
public class CatCommand implements Command {
    private FileSystem fileSystem;

    public CatCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String execute(String[] args, String stdin) {
        if (args.length == 0) {
            if (stdin != null) {
                return stdin;
            }
            return "Usage: cat <filename> ou utiliser une redirection (<)";
        }

        StringBuilder output = new StringBuilder();
        for (String fileName : args) {
            if (fileSystem.exists(fileName)) {
                output.append(fileSystem.cat(fileName)).append("\n");
            } else {
                output.append("Fichier introuvable: ").append(fileName).append("\n");
            }
        }
        return output.toString();
    }
}
