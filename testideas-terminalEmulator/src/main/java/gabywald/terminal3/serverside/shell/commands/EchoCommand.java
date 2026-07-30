package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalFile;
import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * echo command - Display a line of text
 * @author Gabriel Chandesris (2026)
 */
public class EchoCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) {
		if (stdin != null) {
			// In that case, this is an "outputfile" for echo
			TerminalNode outputFile = CommandHelper.resolveFile(state, stdin);
			StringBuilder output = new StringBuilder();
			// NOTE newline in echo transmission !
			for (String arg : args) 
				{ output.append( (arg.contains("/n") ? arg.replaceAll("/n", "\n") : arg) ).append(" "); }
			if ( (outputFile != null) && (outputFile.isFile()) ) 
				{ ((TerminalFile)outputFile).setContent(output.subSequence(0, output.length() - 1).toString()); }
			else {
				if ( outputFile == null ) { return "File: '" + stdin + "' not found !"; }
				if ( ! outputFile.isFile()) { return "'" + stdin + "': not a file !"; }
			}
		} else { return this.execute(state, args); }
		return "";
	}
	
	@Override
	public String execute(TerminalState state, String[] args) {
		if (args.length == 0) { return ""; }
		StringBuilder output = new StringBuilder();
		for (String arg : args) { output.append(arg).append(" "); }
		return output.subSequence(0, output.length() - 1).toString();
	}
	
	@Override
	public String getName() { return "echo"; }
	@Override
	public String getDescription() { return "Display a line of text"; }
	@Override
	public String getUsage() { return "Usage: echo [STRING]..."; }
	
	@Override
	public boolean isVisible() { return true; }
	
}
