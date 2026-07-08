package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * echo command - Display a line of text
 * @author Gabriel Chandesris (2026)
 */
public class EchoCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) {
		// TODO implement with stdin NOT NULL !
		return (stdin == null)? this.execute(state, args) : null ;
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
    public String getUsage() { return "echo [STRING]..."; }
}
