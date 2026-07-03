package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;

/**
 * Commande 'grep' : filtre les lignes contenant un motif.
 * Usage: grep <motif> [fichier] ou grep <motif> < stdin
 * @author Gabriel Chandesris (2026)
 */
public class GrepCommand implements Command {
    private FileSystem fileSystem;

    public GrepCommand(FileSystem fileSystem) { this.fileSystem = fileSystem; }

    @Override
    public String execute(String[] args, String stdin) {
        if (args.length == 0) { return "Usage: grep <pattern> [file]"; }

        String pattern = args[0];
        String content;

        if (args.length > 1) {
            String fileName = args[1];
            if (!this.fileSystem.exists(fileName)) 
            	{ return "Fichier introuvable: " + fileName; }
            content = this.fileSystem.cat(fileName);
        } else if (stdin != null) {
            content = stdin;
        } else {
            return "Usage: grep <pattern> [file] or use pipe";
        }

        StringBuilder output = new StringBuilder();
        for (String line : content.split("\n")) {
            if (line.contains(pattern)) { output.append(line).append("\n"); }
        }

        return output.toString();
    }
}
