package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * Commande 'edit' : text editor (nano or vim).
 * Usage:
 *   edit <filename>		  # Use nano by default
 *   edit --nano <filename>   # nano
 *   edit --vim <filename>	# vim
 * @author Gabriel Chandesris (2026)
 */
public class EditCommand implements ICommand {

	@Override
	public String execute(TerminalState state, String[] args, String stdin) 
		{ return this.execute(state, args); }
	
	@Override
	public String execute(TerminalState state, String[] args) {
		if (args.length == 0) { return "Usage: edit [--nano|--vim] <filename>"; }
		return "MODE_EDIT:" + String.join(" ", args);
	}
	
	@Override
	public String getName() { return "edit"; }
	@Override
	public String getDescription() { return "Edit a text file !"; }
	@Override
	public String getUsage() { return "Usage: edit [--nano|--vim] <filename>"; }
	
	@Override
	public boolean isVisible() { return true; }
	
}
