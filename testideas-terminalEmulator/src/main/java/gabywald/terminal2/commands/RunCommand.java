package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;

/**
 * Commande 'run' : exécute un script (fichier contenant des commandes).
 * Supporte les redirections et pipes.
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
        if (args.length == 0) {
            return "Usage: run <filename>";
        }

        String fileName = args[0];
        if (!fileSystem.exists(fileName)) {
            return "Fichier introuvable: " + fileName;
        }

        String scriptContent = fileSystem.cat(fileName);
        String[] lines = scriptContent.split("\n");
        StringBuilder output = new StringBuilder();

        String originalDir = fileSystem.getCurrentDirectory();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String result = commandParser.execute(line, fileSystem.getCurrentDirectory());
            if (!result.isEmpty() && !result.startsWith("MODE_EDIT:")) {
                output.append(result).append("\n");
            }
        }

        fileSystem.setCurrentDirectory(originalDir);

        return output.toString().isEmpty() ?
               "Script exécuté avec succès." :
               output.toString();
    }
}
