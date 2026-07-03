package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;

/**
 * Commande 'tr' : remplace ou supprime des caractères.
 * Usage: tr <set1> <set2> [fichier] ou tr <set1> <set2> < stdin
 * @author Gabriel Chandesris (2026)
 */
public class TrCommand implements Command {
    private FileSystem fileSystem;

    public TrCommand(FileSystem fileSystem) { this.fileSystem = fileSystem; }

    @Override
    public String execute(String[] args, String stdin) {
        if (args.length < 2) { return "Usage: tr <set1> <set2> [file]"; }

        String set1 = args[0];
        String set2 = args[1];
        String content;

        if (args.length > 2) {
            String fileName = args[2];
            if (!this.fileSystem.exists(fileName)) 
            	{ return "Fichier introuvable: " + fileName; }
            content = this.fileSystem.cat(fileName);
        } else if (stdin != null) {
            content = stdin;
        } else {
            return "Usage: tr <set1> <set2> [file] ou utiliser un pipe";
        }

        StringBuilder output = new StringBuilder();
        for (char c : content.toCharArray()) {
            int index = set1.indexOf(c);
            if (index != -1 && index < set2.length()) {
                output.append(set2.charAt(index));
            } else {
                output.append(c);
            }
        }

        return output.toString();
    }
}
