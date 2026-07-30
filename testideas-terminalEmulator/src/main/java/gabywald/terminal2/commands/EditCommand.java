package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;

/**
 * Commande 'edit' : lance un éditeur de texte (nano ou vim).
 * Usage:
 *   edit <filename>          # Utilise nano par défaut
 *   edit --nano <filename>   # Force nano
 *   edit --vim <filename>    # Force vim
 * @author Gabriel Chandesris (2026)
 */
public class EditCommand implements Command {
    private FileSystem fileSystem;
    private CommandParser commandParser;

    public EditCommand(FileSystem fileSystem, CommandParser commandParser) {
        this.fileSystem = fileSystem;
        this.commandParser = commandParser;
    }

    @Override
    public String execute(String[] args, String stdin) {
        if (args.length == 0) { return "Usage: edit [--nano|--vim] <filename>"; }
        return "MODE_EDIT:" + String.join(" ", args);
    }
}
