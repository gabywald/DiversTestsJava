package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.TerminalDirectory;
import gabywald.terminal.filesystem.TerminalNode;
import gabywald.terminal.filesystem.TerminalFile;

/**
 * cp command - Copy filesabriel Chandesris (2026)
 * @author Gabriel Chandesris (2026)
 */
public class CpCommand implements Command {
	
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length < 2) {
            return "cp: missing destination file operand after '" + 
                   (args.length > 0 ? args[args.length - 1] : "") + "'\nTry 'cp --help' for more information.";
        }
        
        if (args.length == 2) { return copyFile(state, args[0], args[1]); }
        
        String destination = args[args.length - 1];
        TerminalDirectory destDir = CommandHelper.resolveDirectory(state, destination);
        if (destDir == null || !destDir.isDirectory()) { return "cp: target '" + destination + "' is not a directory"; }
        
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < args.length - 1; i++) {
            String result = copyFile(state, args[i], destination + "/" + CommandHelper.getSimpleName(args[i]));
            if (!result.isEmpty()) { output.append(result).append("\n"); }
        }
        return output.toString();
    }
    
    private String copyFile(TerminalState state, String sourcePath, String destPath) {
        TerminalNode source = CommandHelper.resolveFile(state, sourcePath);
        if (source == null) return "cp: cannot stat '" + sourcePath + "': No such file or directory";
        if (source.isDirectory()) return "cp: -r not specified; omitting directory '" + sourcePath + "'";
        
        TerminalDirectory destParent = CommandHelper.getParentDirectory(state, destPath);
        if (destParent == null) return "cp: cannot create '" + destPath + "': No such file or directory";
        
        String destName = CommandHelper.getSimpleName(destPath);
        if (destParent.hasChild(destName)) {
            TerminalNode existing = destParent.getChild(destName);
            if (existing.isDirectory()) { return "cp: cannot overwrite directory '" + destPath + "' with '" + sourcePath + "'"; }
            ((TerminalFile) existing).setContent(((TerminalFile) source).getContent());
        } else {
            TerminalFile sourceFile = (TerminalFile) source;
            TerminalFile newFile = new TerminalFile(destName, destParent, sourceFile.getContent());
            destParent.addChild(newFile);
        }
        return "";
    }
    
    @Override
    public String getName() { return "cp"; }
    @Override
    public String getDescription() { return "Copy files and directories"; }
    @Override
    public String getUsage() { return "cp [OPTION] SOURCE DEST\n       cp [OPTION] SOURCE... DIRECTORY"; }
    
}