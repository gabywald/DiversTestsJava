package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * clear command - Clear the terminal screen
 * @author Gabriel Chandesris (2026)
 */
public class ClearCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) 
		{ return this.execute(state, args); }
	
	@Override
	public String execute(TerminalState state, String[] args) 
		{ return "\033[H\033[2J"; }
	
	@Override
	public String getName() { return "clear"; }
	@Override
	public String getDescription() { return "Clear the terminal screen"; }
	@Override
	public String getUsage() { return "Usage: clear"; }
	
	@Override
	public boolean isVisible() { return true; }
	
}
