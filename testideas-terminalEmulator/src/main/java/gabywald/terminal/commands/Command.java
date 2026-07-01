package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;

/**
 * Command interface for all executable commands.
 * @author Gabriel Chandesris (2026)
 */
public interface Command {
    String execute(TerminalState state, String[] args);
    String getName();
    String getDescription();
    String getUsage();
}
