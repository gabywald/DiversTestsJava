package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.clientside.gui.TerminalState;
import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalFile;
import gabywald.terminal3.serverside.filesystem.TerminalNode;
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
            TerminalNode existing = resolveFile(state, fileName);
            if (existing != null && existing.isDirectory()) {
                output.append("touch: cannot touch '").append(fileName).append("': Is a directory\n");
                continue;
            }
            
            TerminalDirectory parentDir = getParentDirectory(state, fileName);
            if (parentDir == null) {
                output.append("touch: cannot touch '").append(fileName).append("': No such file or directory\n");
                continue;
            }
            
            String simpleName = getSimpleName(fileName);
            if (existing != null && existing.isFile()) 
            	{ ((TerminalFile) existing).setContent(((TerminalFile)existing).getContent()); }
            else { parentDir.createFile(simpleName); }
        }
        return output.toString();
    }
    
    private TerminalNode resolveFile(TerminalState state, String fileName) {
        TerminalDirectory current = state.getCurrentDirectory();
        if (fileName.startsWith("/")) { return CommandHelper.resolveAbsolutePath(state.getRootDirectory(), fileName); }
        return CommandHelper.resolveRelativePath(current, fileName);
    }
    
    private TerminalDirectory getParentDirectory(TerminalState state, String path) {
        if (path.startsWith("/")) { return CommandHelper.getParentFromAbsolutePath(state.getRootDirectory(), path); }
        return CommandHelper.getParentFromRelativePath(state.getCurrentDirectory(), path);
    }
    
    private String getSimpleName(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }
    
    @Override
    public String getName() { return "touch"; }
    @Override
    public String getDescription() { return "Create empty files or update timestamp"; }
    @Override
    public String getUsage() { return "touch FILE..."; }
}
