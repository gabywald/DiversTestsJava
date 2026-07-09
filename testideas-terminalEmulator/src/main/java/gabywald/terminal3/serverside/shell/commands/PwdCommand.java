package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * pwd command - Print working directory
 * @author Gabriel Chandesris (2026)
 */
public class PwdCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) 
		{ return this.execute(state, args); }
	
	@Override
	public String execute(TerminalState state, String[] args) 
		{ return state.getCurrentDirectory().getPath(); }
	
	@Override
	public String getName() { return "pwd"; }
	@Override
	public String getDescription() { return "Print the current working directory"; }
	@Override
	public String getUsage() { return "Usage: pwd"; }
	
	@Override
	public boolean isVisible() { return true; }
	
}
