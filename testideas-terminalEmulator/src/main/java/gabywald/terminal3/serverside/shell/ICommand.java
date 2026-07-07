package gabywald.terminal3.serverside.shell;

import gabywald.terminal3.clientside.gui.TerminalState;

/**
 * Command interface for all executable commands.
 * @author Gabriel Chandesris (2026)
 */
public interface ICommand {
	String execute(TerminalState state, String[] args);
	String execute(TerminalState state, String[] args, String stdin);
	String getName();
	String getDescription();
    String getUsage();
}
