package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.TerminalDirectory;
import gabywald.terminal.filesystem.TerminalNode;
import gabywald.terminal.filesystem.TerminalFile;

/**
 * touch command - Create empty files or update timestamp
 * @author Gabriel Chandesris (2026)
 */
public class TouchCommand implements Command {

    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) { return "touch: missing file operand"; }
        
        StringBuilder output = new StringBuilder();
        for (String fileName : args) {
            TerminalNode existing = CommandHelper.resolveFile(state, fileName);
            if (existing != null && existing.isDirectory()) {
                output.append("touch: cannot touch '").append(fileName).append("': Is a directory\n");
                continue;
            }
            
            TerminalDirectory parentDir = CommandHelper.getParentDirectory(state, fileName);
            if (parentDir == null) {
                output.append("touch: cannot touch '").append(fileName).append("': No such file or directory\n");
                continue;
            }
            
            String simpleName = CommandHelper.getSimpleName(fileName);
            if (existing != null && existing.isFile()) 
            	{ ((TerminalFile) existing).setContent(((TerminalFile)existing).getContent()); }
            else { parentDir.createFile(simpleName); }
        }
        return output.toString();
    }
    
    @Override
    public String getName() { return "touch"; }
    @Override
    public String getDescription() { return "Create empty files or update timestamp"; }
    @Override
    public String getUsage() { return "touch FILE..."; }
}
