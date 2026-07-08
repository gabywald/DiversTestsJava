package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * exit command - Exit the terminal
 * @author Gabriel Chandesris (2026)
 */
public class ExitCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) 
		{ return this.execute(state, args); }
	
    @Override
    public String execute(TerminalState state, String[] args) 
    	{ return "EXIT"; }
    
    @Override
    public String getName() { return "exit"; }
    @Override
    public String getDescription() { return "Exit the terminal"; }
    @Override
    public String getUsage() { return "exit"; }
    
}
