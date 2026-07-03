package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;

/**
 * Commande 'pwd' : affiche le répertoire courant.
 * @author Gabriel Chandesris (2026)
 */
public class PwdCommand implements Command {
    private FileSystem fileSystem;

    public PwdCommand(FileSystem fileSystem) { this.fileSystem = fileSystem; }

    @Override
    public String execute(String[] args, String stdin) 
    	{ return this.fileSystem.getCurrentDirectory(); }
}
