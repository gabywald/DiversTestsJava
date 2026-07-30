package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;

/**
 * Commande 'cd' : change le répertoire courant.
 * @author Gabriel Chandesris (2026)
 */
public class CdCommand implements Command {
    private FileSystem fileSystem;

    public CdCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String execute(String[] args, String stdin) {
        if (args.length == 0) { return "Usage: cd <directory>"; }

        String targetDir = args[0];
        if (targetDir.equals("..")) {
            String parentDir = fileSystem.getParentDirectory(fileSystem.getCurrentDirectory());
            this.fileSystem.setCurrentDirectory(parentDir);
            return "";
        } else if (targetDir.equals("/")) {
            fileSystem.setCurrentDirectory(fileSystem.getRoot());
            return "";
        } else {
            String currentDir = fileSystem.getCurrentDirectory();
            String fullPath = currentDir.equals("/") ? "/" + targetDir : currentDir + "/" + targetDir;
            if (fileSystem.exists(targetDir)) {
                fileSystem.setCurrentDirectory(fullPath);
                return "";
            }
            return "Répertoire introuvable: " + targetDir;
        }
    }
}
