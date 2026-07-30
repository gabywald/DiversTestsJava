package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalFile;
import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * cat command - Concatenate and print files
 * @author Gabriel Chandesris (2026)
 */
public class CatCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) {
		if (stdin == null) { return this.execute(state, args); }
		else {
			if (args.length == 0) { return stdin; }

			StringBuilder output = new StringBuilder();
			for (String fileName : args) {
				TerminalNode targetFile = CommandHelper.resolveFile(state, fileName);
				if (targetFile == null) { return "Unknown File: " + fileName; }
				else { output.append(((TerminalFile)targetFile).getContent()); }
			}
			return output.toString();
		}
	}
	
	@Override
	public String execute(TerminalState state, String[] args) {
		if (args.length == 0) { return "cat: missing operand"; }
		
		StringBuilder output = new StringBuilder();
		for (String fileName : args) {
			TerminalNode node = CommandHelper.resolveFile(state, fileName);
			if (node == null) {
				output.append("cat: No such file: ").append(fileName);
				continue;
			}
			if (!node.isFile()) {
				output.append("cat: ").append(fileName).append(": Is a directory\n");
				continue;
			}
			TerminalFile file = (TerminalFile) node;
			output.append(file.getContent());
		}
		return output.toString();
	}
	
	@Override
	public String getName() { return "cat"; }
	@Override
	public String getDescription() { return "Concatenate and print files"; }
	@Override
	public String getUsage() { return "Usage: cat [FILE]..."; }
	
	@Override
	public boolean isVisible() { return true; }

}
