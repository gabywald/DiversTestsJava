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
			// in that case, this is an "outputfile" for echo
			TerminalNode outputFile = CommandHelper.resolveFile(state, stdin);
			if ( (outputFile != null) && (outputFile.isFile()) ) 
				{ ((TerminalFile)outputFile).setContent(args[0]); }
		} else { return this.execute(state, args); }
		return "";
	}
	
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) return "";
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) { output.append(" "); }
            output.append(args[i]);
        }
        return output.toString();
    }
    
    @Override
    public String getName() { return "echo"; }
    @Override
    public String getDescription() { return "Display a line of text"; }
    @Override
    public String getUsage() { return "Usage: echo [STRING]..."; }
}
