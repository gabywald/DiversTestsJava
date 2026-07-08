package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandFactory;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * Command 'grep' : filter lines with pattern
 * Usage: grep <motif> [file] or grep <motif> < stdin
 * @author Gabriel Chandesris (2026)
 */
public class GrepCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args) 
		{ return this.execute(state, args, null); }

	@Override
	public String execute(TerminalState state, String[] args, String stdin) {
		if (args.length == 0) { return "Usage: grep <pattern> [file]"; }

		String pattern = args[0];
		String content = null;

		if (args.length > 1) {
			String fileName = args[1];
			TerminalNode targetFile = CommandHelper.resolveFile(state, fileName);
			if (targetFile == null) { return "Unknown File: " + fileName; }
			content = CommandFactory.getCommand("cat").execute(state, new String[] { fileName });
		} 
		else if (stdin != null) { content = stdin; }
		else { return "Usage: grep <pattern> [file] or use pipe"; }

		StringBuilder output = new StringBuilder();
		for (String line : content.split("\n")) {
			if (line.contains(pattern)) { output.append(line).append("\n"); }
		}

		return output.toString();	
	}

	@Override
	public String getName() { return "grep"; }
	@Override
	public String getDescription() { return "Filter lines with pattern"; }
	@Override
	public String getUsage() { return "Usage: grep <pattern> [file]"; }
}
