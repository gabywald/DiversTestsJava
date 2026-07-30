package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;

/**
 * Commande 'echo' : affiche du texte.
 * @author Gabriel Chandesris (2026)
 */
public class EchoCommand implements Command {
    private FileSystem fileSystem;

    public EchoCommand(FileSystem fileSystem) { this.fileSystem = fileSystem; }

    @Override
    public String execute(String[] args, String stdin) {
        if (args.length == 0) { return "Usage: echo <text>"; }

        StringBuilder text = new StringBuilder();
        for (String arg : args) { text.append(arg).append(" "); } // .replaceAll("\"", "")
        return text.toString().trim();
    }
}
