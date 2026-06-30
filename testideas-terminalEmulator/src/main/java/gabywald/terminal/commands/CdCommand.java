package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.Directory;
import gabywald.terminal.filesystem.FileNode;

/**
 * cd command - Change directory
 */
public class CdCommand implements Command {
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) {
            state.setCurrentDirectory(state.getRootDirectory());
            return "";
        }
        
        String path = args[0];
        Directory newDir = resolveDirectory(state, path);
        if (newDir == null) return "cd: no such file or directory: " + path;
        
        state.setCurrentDirectory(newDir);
        return "";
    }
    
    private Directory resolveDirectory(TerminalState state, String path) {
        if (".".equals(path) || "".equals(path)) return state.getCurrentDirectory();
        if ("/".equals(path)) return state.getRootDirectory();
        if (path.startsWith("/")) return resolveAbsolutePath(state.getRootDirectory(), path);
        return resolveRelativePath(state.getCurrentDirectory(), path);
    }
    
    private Directory resolveAbsolutePath(Directory root, String path) {
        String[] parts = path.split("/");
        Directory current = root;
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isEmpty() || ".".equals(parts[i])) continue;
            if ("..".equals(parts[i])) {
                if (current.getParent() != null) current = current.getParent();
            } else {
                FileNode node = current.getChild(parts[i]);
                if (node == null || !node.isDirectory()) return null;
                current = (Directory) node;
            }
        }
        return current;
    }
    
    private Directory resolveRelativePath(Directory current, String path) {
        String[] parts = path.split("/");
        for (String part : parts) {
            if (part.isEmpty() || ".".equals(part)) continue;
            if ("..".equals(part)) {
                if (current.getParent() != null) current = current.getParent();
            } else {
                FileNode node = current.getChild(part);
                if (node == null || !node.isDirectory()) return null;
                current = (Directory) node;
            }
        }
        return current;
    }
    
    @Override
    public String getName() { return "cd"; }
    @Override
    public String getDescription() { return "Change the current directory"; }
    @Override
    public String getUsage() { return "cd [DIR]"; }
}