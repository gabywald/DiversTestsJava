package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalFile;
import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * touch command - Create empty files or update timestamp
 * @author Gabriel Chandesris (2026)
 */
public class TouchCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) 
		{ return this.execute(state, args); }
	
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
    public String getUsage() { return "Usage: touch [FILE]..."; }
}
