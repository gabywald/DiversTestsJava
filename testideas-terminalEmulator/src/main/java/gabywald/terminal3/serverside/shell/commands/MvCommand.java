package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalFile;
import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * mv command - Move or rename files
 * @author Gabriel Chandesris (2026)
 */
public class MvCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) 
		{ return this.execute(state, args); }
	
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length < 2) {
            return "mv: missing destination file operand after '" + 
                   (args.length > 0 ? args[args.length - 1] : "") + "'\nTry 'mv --help' for more information.";
        }
        
        if (args.length == 2) {
            return moveFile(state, args[0], args[1]);
        }
        
        String destination = args[args.length - 1];
        TerminalDirectory destDir = CommandHelper.resolveDirectory(state, destination);
        if (destDir == null || !destDir.isDirectory()) {
            return "mv: target '" + destination + "' is not a directory";
        }
        
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < args.length - 1; i++) {
            String result = moveFile(state, args[i], destination + "/" + CommandHelper.getSimpleName(args[i]));
            if (!result.isEmpty()) { output.append(result).append("\n"); }
        }
        return output.toString();
    }
    
    private String moveFile(TerminalState state, String sourcePath, String destPath) {
        TerminalNode source = CommandHelper.resolveFile(state, sourcePath);
        if (source == null) { return "mv: cannot stat '" + sourcePath + "': No such file or directory"; }
        
        TerminalDirectory destParent = CommandHelper.getParentDirectory(state, destPath);
        if (destParent == null) { return "mv: cannot move '" + sourcePath + "' to '" + destPath + "': No such file or directory"; }
        
        String destName = CommandHelper.getSimpleName(destPath);
        if (destParent.hasChild(destName)) {
            TerminalNode existing = destParent.getChild(destName);
            if (existing.isDirectory()) { return "mv: cannot overwrite directory '" + destPath + "' with '" + sourcePath + "'"; }
            if (source.isFile()) { ((TerminalFile) existing).setContent(((TerminalFile) source).getContent()); }
            source.delete();
        } else {
            source.setName(destName);
            source.getParent().removeChild(source);
            destParent.addChild(source);
        }
        return "";
    }
    
    @Override
    public String getName() { return "mv"; }
    @Override
    public String getDescription() { return "Move or rename files"; }
    @Override
    
    public String getUsage() { return "mv [OPTION] SOURCE DEST\n       mv [OPTION] SOURCE... DIRECTORY"; }
}
