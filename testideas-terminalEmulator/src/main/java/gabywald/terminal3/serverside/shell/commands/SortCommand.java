package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandFactory;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.ICommand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Command 'sort' : sort lines of a file or stdin
 * Usage: sort [fichier] or sort < stdin
 * @author Gabriel Chandesris (2026)
 */
public class SortCommand implements ICommand {
	
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
		else { return "Usage: sort [file] or use pipe pipe"; }

		List<String> lines = Arrays.asList(content.split("\n"));
		Collections.sort(lines);
		return String.join("\n", lines);
	}

	@Override
	public String getName() { return "sort"; }
	@Override
	public String getDescription() { return "Sort lines of a file"; }
	@Override
	public String getUsage() { return "Usage: sort [FILE]"; }
	
	@Override
	public boolean isVisible() { return true; }
	
}
