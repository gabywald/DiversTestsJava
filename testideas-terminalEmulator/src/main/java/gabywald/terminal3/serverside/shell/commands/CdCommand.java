package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * cd command - Change directory
 * @author Gabriel Chandesris (2026)
 */
public class CdCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) {
		// TODO implement with stdin NOT NULL !
		return (stdin == null)? this.execute(state, args) : null ;
	}
	
	@Override
	public String execute(TerminalState state, String[] args) {
		if (args.length == 0) {
			state.setCurrentDirectory(state.getRootDirectory());
			return "";
		}
		
		String path = args[0];
		TerminalDirectory newDir = CommandHelper.resolveDirectory(state, path);
		if (newDir == null) { return "cd: no such directory: " + path; }
		
		state.setCurrentDirectory(newDir);
		return "";
	}
	
	@Override
	public String getName() { return "cd"; }
	@Override
	public String getDescription() { return "Change the current directory"; }
	@Override
	public String getUsage() { return "Usage: cd [DIR]"; }

	@Override
	public boolean isVisible() { return true; }
	
}