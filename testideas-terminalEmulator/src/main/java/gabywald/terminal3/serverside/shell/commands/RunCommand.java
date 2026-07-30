package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandFactory;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.CommandParser;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * Commande 'run' : running a script (file filled with commands).
 * With redirections et pipes.
 * @author Gabriel Chandesris (2026)
 */
public class RunCommand implements ICommand {

	@Override
	public String execute(TerminalState state, String[] args, String stdin) 
		{ return this.execute(state, args); }
	
	@Override
	public String execute(TerminalState state, String[] args) {
		if (args.length == 0) { return "Usage: run <filename>"; }

		String fileName = args[0];
		TerminalNode targetFile = CommandHelper.resolveFile(state, fileName);
		if (targetFile == null) { return "Unknown File: " + fileName; }

		String scriptContent = CommandFactory.getCommand("cat").execute(state, new String[] { fileName });
		String[] lines = scriptContent.split("\n");
		StringBuilder output = new StringBuilder();

		TerminalDirectory originalDir = state.getCurrentDirectory();

		for (String line : lines) {
			line = line.trim();
			if (line.isEmpty() || line.startsWith("#")) { continue; }
			String result = CommandParser.execute(state, line); // TODO include here the script engine !
			if (!result.isEmpty() && !result.startsWith("MODE_EDIT:")) 
				{ output.append(result).append("\n"); }
		}

		state.setCurrentDirectory(originalDir);

		return output.toString().isEmpty() ? "Successful script execution !" : output.toString();
	}
	
	@Override
	public String getName() { return "run"; }
	@Override
	public String getDescription() { return "Run a Script"; }
	@Override
	public String getUsage() { return "Usage: run [FILE]"; }
	
	@Override
	public boolean isVisible() { return true; }
	
}
