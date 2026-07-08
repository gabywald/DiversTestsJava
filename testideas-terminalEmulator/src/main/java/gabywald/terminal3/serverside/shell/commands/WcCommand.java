package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandFactory;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * Commande 'wc' : count lines, words and characters
 * Usage: wc [file] or wc < stdin
 * @author Gabriel Chandesris (2026)
 */
public class WcCommand implements ICommand {

	@Override
	public String execute(TerminalState state, String[] args) 
		{ return this.execute(state, args, null); }

	@Override
	public String execute(TerminalState state, String[] args, String stdin) {
		String content;

		if (args.length > 0) {
			String fileName = args[0];
			TerminalNode targetFile = CommandHelper.resolveFile(state, fileName);
			if (targetFile == null) { return "Unknown File: " + fileName; }
			content = CommandFactory.getCommand("cat").execute(state, new String[] { fileName });
		} 
		else if (stdin != null) { content = stdin; } 
		else { return "Usage: wc [file] or use a pipe"; }

		int lines = content.split("\n").length;
		int words = content.split("\\s+").length;
		int chars = content.length();

		return String.format("%d %d %d", lines, words, chars);
	}
	
	@Override
	public String getName() { return "wc"; }
	@Override
	public String getDescription() { return "Count lines, words and characters"; }
	@Override
	public String getUsage() { return "Usage: wc [FILE] or use a pipe"; }
	
}
