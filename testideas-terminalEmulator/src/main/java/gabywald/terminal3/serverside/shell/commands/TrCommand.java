package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandFactory;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * Command 'tr' : replace or remove characters
 * Usage: tr <set1> <set2> [file] or tr <set1> <set2> < stdin
 * @author Gabriel Chandesris (2026)
 */
public class TrCommand implements ICommand {

	@Override
	public String execute(TerminalState state, String[] args) 
		{ return this.execute(state, args, null); }

	@Override
	public String execute(TerminalState state, String[] args, String stdin) {
		if (args.length < 2) { return "Usage: tr <set1> <set2> [file]"; }

		String set1 = args[0];
		String set2 = args[1];
		String content;

		if (args.length > 2) {
			String fileName = args[2];
			TerminalNode targetFile = CommandHelper.resolveFile(state, fileName);
			if (targetFile == null) { return "Unknown File: " + fileName; }
			content = CommandFactory.getCommand("cat").execute(state, new String[] { fileName });
		}
		else if (stdin != null) { content = stdin; } 
		else { return "Usage: tr <set1> <set2> [file] or use pipe"; }

		StringBuilder output = new StringBuilder();
		for (char c : content.toCharArray()) {
			int index = set1.indexOf(c);
			if (index != -1 && index < set2.length()) 
				{ output.append(set2.charAt(index)); } 
			else 
				{ output.append(c); }
		}

		return output.toString();
	}
	
	@Override
	public String getName() { return "tr"; }
	@Override
	public String getDescription() { return "Replace or remove characters"; }
	@Override
	public String getUsage() { return "Usage: tr <set1> <set2> [FILE]"; }
	
}
