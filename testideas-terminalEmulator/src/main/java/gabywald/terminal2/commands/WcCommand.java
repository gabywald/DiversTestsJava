package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;

/**
 * Commande 'wc' : compte les lignes, mots et caractères.
 * Usage: wc [fichier] ou wc < stdin
 */
public class WcCommand implements Command {
    private FileSystem fileSystem;

    public WcCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String execute(String[] args, String stdin) {
        String content;

        if (args.length > 0) {
            String fileName = args[0];
            if (!fileSystem.exists(fileName)) {
                return "Fichier introuvable: " + fileName;
            }
            content = fileSystem.cat(fileName);
        } else if (stdin != null) {
            content = stdin;
        } else {
            return "Usage: wc [file] ou utiliser un pipe";
        }

        int lines = content.split("\n").length;
        int words = content.split("\\s+").length;
        int chars = content.length();

        return String.format("%d %d %d", lines, words, chars);
    }
}
