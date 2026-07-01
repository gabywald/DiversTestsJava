package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.TerminalDirectory;

/**
 * mkdir command - Create directories
 * @author Gabriel Chandesris (2026)
 */
public class MkdirCommand implements Command {
	
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) return "mkdir: missing operand";
        
        StringBuilder output = new StringBuilder();
        for (String dirName : args) {
            TerminalDirectory parentDir = CommandHelper.getParentDirectory(state, dirName);
            if (parentDir == null) {
                output.append("mkdir: cannot create directory '").append(dirName).append("': No such file or directory\n");
                continue;
            }
            
            String simpleName = CommandHelper.getSimpleName(dirName);
            if (parentDir.hasChild(simpleName)) {
                output.append("mkdir: cannot create directory '").append(dirName).append("': File exists\n");
                continue;
            }
            parentDir.createDirectory(simpleName);
        }
        return output.toString();
    }
    
    @Override
    public String getName() { return "mkdir"; }
    @Override
    public String getDescription() { return "Create directories"; }
    @Override
    public String getUsage() { return "mkdir DIRECTORY..."; }
    
}
