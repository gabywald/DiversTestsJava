package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;

/**
 * Commande 'run' : exécute un script (fichier contenant des commandes).
 * Supporte les redirections et pipes.
 * @author Gabriel Chandesris (2026)
 */
public class RunCommand implements Command {
    private FileSystem fileSystem;
    private CommandParser commandParser;

    public RunCommand(FileSystem fileSystem, CommandParser commandParser) {
        this.fileSystem = fileSystem;
        this.commandParser = commandParser;
    }

    @Override
    public String execute(String[] args, String stdin) {
        if (args.length == 0) { return "Usage: run <filename>"; }

        String fileName = args[0];
        if (!this.fileSystem.exists(fileName)) { return "Fichier introuvable: " + fileName; }

        String scriptContent = this.fileSystem.cat(fileName);
        String[] lines = scriptContent.split("\n");
        StringBuilder output = new StringBuilder();

        String originalDir = this.fileSystem.getCurrentDirectory();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) { continue; }
            String result = this.commandParser.execute(line, this.fileSystem.getCurrentDirectory());
            if (!result.isEmpty() && !result.startsWith("MODE_EDIT:")) 
            	{ output.append(result).append("\n"); }
        }

        this.fileSystem.setCurrentDirectory(originalDir);

        return output.toString().isEmpty() ?
               "Script exécuté avec succès." :
               output.toString();
    }
}
