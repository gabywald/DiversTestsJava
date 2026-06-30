package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.Directory;
import gabywald.terminal.filesystem.FileNode;

/**
 * mkdir command - Create directories
 */
public class MkdirCommand implements Command {
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) return "mkdir: missing operand";
        
        StringBuilder output = new StringBuilder();
        for (String dirName : args) {
            Directory parentDir = getParentDirectory(state, dirName);
            if (parentDir == null) {
                output.append("mkdir: cannot create directory '").append(dirName).append("': No such file or directory\n");
                continue;
            }
            
            String simpleName = getSimpleName(dirName);
            if (parentDir.hasChild(simpleName)) {
                output.append("mkdir: cannot create directory '").append(dirName).append("': File exists\n");
                continue;
            }
            parentDir.createDirectory(simpleName);
        }
        return output.toString();
    }
    
    private Directory getParentDirectory(TerminalState state, String path) {
        if (path.startsWith("/")) return getParentFromAbsolutePath(state.getRootDirectory(), path);
        return getParentFromRelativePath(state.getCurrentDirectory(), path);
    }
    
    private String getSimpleName(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }
    
    private Directory getParentFromAbsolutePath(Directory root, String path) {
        String[] parts = path.split("/");
        Directory current = root;
        for (int i = 1; i < parts.length - 1; i++) {
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
    
    private Directory getParentFromRelativePath(Directory current, String path) {
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
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
    
    @Override
    public String getName() { return "mkdir"; }
    @Override
    public String getDescription() { return "Create directories"; }
    @Override
    public String getUsage() { return "mkdir DIRECTORY..."; }
}
