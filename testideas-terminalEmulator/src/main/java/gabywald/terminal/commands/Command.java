package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;

/**
 * Command interface for all executable commands.
 */
public interface Command {
    String execute(TerminalState state, String[] args);
    String getName();
    String getDescription();
    String getUsage();
}
