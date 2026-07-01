package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;

/**
 * clear command - Clear the terminal screen
 * @author Gabriel Chandesris (2026)
 */
public class ClearCommand implements Command {
    @Override
    public String execute(TerminalState state, String[] args) {
        return "\033[H\033[2J";
    }
    
    @Override
    public String getName() { return "clear"; }
    @Override
    public String getDescription() { return "Clear the terminal screen"; }
    @Override
    public String getUsage() { return "clear"; }
}
