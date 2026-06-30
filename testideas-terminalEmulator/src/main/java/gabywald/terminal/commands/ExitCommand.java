package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;

/**
 * exit command - Exit the terminal
 */
public class ExitCommand implements Command {
    @Override
    public String execute(TerminalState state, String[] args) {
        return "EXIT";
    }
    
    @Override
    public String getName() { return "exit"; }
    @Override
    public String getDescription() { return "Exit the terminal"; }
    @Override
    public String getUsage() { return "exit"; }
}
