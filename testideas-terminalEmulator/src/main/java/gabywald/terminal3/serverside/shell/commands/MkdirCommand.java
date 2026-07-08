package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * mkdir command - Create directories
 * @author Gabriel Chandesris (2026)
 */
public class MkdirCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) 
		{ return this.execute(state, args); }
	
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) return "mkdir: missing operand";
        
        StringBuilder output = new StringBuilder();
        for (String dirName : args) {
            TerminalDirectory parentDir = CommandHelper.getParentDirectory(state, dirName);
            if (parentDir == null) {
                output.append("mkdir: cannot create directory '").append(dirName).append("': No such file or directory\n");
                continue;
            }
            
            String simpleName = CommandHelper.getSimpleName(dirName);
            if (parentDir.hasChild(simpleName)) {
                output.append("mkdir: cannot create directory '").append(dirName).append("': File exists\n");
                continue;
            }
            parentDir.createDirectory(simpleName);
        }
        return output.toString();
    }
    
    @Override
    public String getName() { return "mkdir"; }
    @Override
    public String getDescription() { return "Create directories"; }
    @Override
    public String getUsage() { return "Usage: mkdir DIRECTORY..."; }
    
}
