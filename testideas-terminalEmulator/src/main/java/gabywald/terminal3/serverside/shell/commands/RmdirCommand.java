package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * rmdir command - Remove empty directories
 * @author Gabriel Chandesris (2026)
 * TODO to make fuzion with rm command (and add some options)
 */
public class RmdirCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) 
		{ return this.execute(state, args); }
	
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) { return "rmdir: missing operand"; }
        
        StringBuilder output = new StringBuilder();
        for (String dirName : args) {
            TerminalNode node = CommandHelper.resolveDirectory(state, dirName);
            if (node == null) {
                output.append("rmdir: failed to remove '").append(dirName).append("': No such file or directory\n");
                continue;
            }
            if (!node.isDirectory()) {
                output.append("rmdir: failed to remove '").append(dirName).append("': Not a directory\n");
                continue;
            }
            TerminalDirectory dir = (TerminalDirectory) node;
            if (!dir.isEmpty()) {
                output.append("rmdir: failed to remove '").append(dirName).append("': Directory not empty\n");
                continue;
            }
            if (!dir.delete()) {
                output.append("rmdir: failed to remove '").append(dirName).append("': Operation not permitted\n");
            }
        }
        return output.toString();
    }
    
    @Override
    public String getName() { return "rmdir"; }
    @Override
    public String getDescription() { return "Remove empty directories"; }
    @Override
    public String getUsage() { return "Usage: rmdir [DIRECTORY]..."; }
}
