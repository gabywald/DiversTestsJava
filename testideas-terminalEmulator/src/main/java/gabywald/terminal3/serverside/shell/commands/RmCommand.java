package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * rm command - Remove files
 * @author Gabriel Chandesris (2026)
 * TODO to make fuzion with rmdir command (and add some options : -d, -f...)
 */
public class RmCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) 
		{ return this.execute(state, args); }
	
	@Override
	public String execute(TerminalState state, String[] args) {
		if (args.length == 0) { return "rm: missing operand"; }
		
		StringBuilder output = new StringBuilder();
		for (String fileName : args) {
			TerminalNode node = CommandHelper.resolveFile(state, fileName);
			if (node == null) {
				output.append("rm: cannot remove '").append(fileName).append("': No such file or directory");
				continue;
			}
			if (node.isDirectory()) {
				// output.append("rm: cannot remove '").append(fileName).append("': Is a directory");
				TerminalDirectory dir = (TerminalDirectory) node;
				if (!dir.isEmpty()) {
					output.append("rm: failed to remove '").append(fileName).append("': Directory not empty");
					continue;
				}
				// continue;
			}
			if (!node.delete()) {
				output.append("rm: cannot remove '").append(fileName).append("': Operation not permitted");
			}
		}
		return output.toString();
	}
	
	@Override
	public String getName() { return "rm"; }
	@Override
	public String getDescription() { return "Remove files"; }
	@Override
	public String getUsage() { return "Usage: rm [FILE|DIRECTORY] ..."; }
	
	@Override
	public boolean isVisible() { return true; }
	
}
