package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.TerminalDirectory;

/**
 * cd command - Change directory
 * @author Gabriel Chandesris (2026)
 */
public class CdCommand implements Command {
	
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) {
            state.setCurrentDirectory(state.getRootDirectory());
            return "";
        }
        
        String path = args[0];
        TerminalDirectory newDir = CommandHelper.resolveDirectory(state, path);
        if (newDir == null) { return "cd: no such file or directory: " + path; }
        
        state.setCurrentDirectory(newDir);
        return "";
    }
    
    @Override
    public String getName() { return "cd"; }
    @Override
    public String getDescription() { return "Change the current directory"; }
    @Override
    public String getUsage() { return "cd [DIR]"; }
    
}