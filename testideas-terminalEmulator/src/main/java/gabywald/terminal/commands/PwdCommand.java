package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;

/**
 * pwd command - Print working directory
 * @author Gabriel Chandesris (2026)
 */
public class PwdCommand implements Command {
    @Override
    public String execute(TerminalState state, String[] args) {
        return state.getCurrentDirectory().getPath();
    }
    
    @Override
    public String getName() { return "pwd"; }
    @Override
    public String getDescription() { return "Print the current working directory"; }
    @Override
    public String getUsage() { return "pwd"; }
}
