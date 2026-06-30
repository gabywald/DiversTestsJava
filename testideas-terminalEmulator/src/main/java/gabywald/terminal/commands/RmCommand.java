package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.Directory;
import gabywald.terminal.filesystem.FileNode;

/**
 * rm command - Remove files
 */
public class RmCommand implements Command {
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) return "rm: missing operand";
        
        StringBuilder output = new StringBuilder();
        for (String fileName : args) {
            FileNode node = resolveFile(state, fileName);
            if (node == null) {
                output.append("rm: cannot remove '").append(fileName).append("': No such file or directory\n");
                continue;
            }
            if (node.isDirectory()) {
                output.append("rm: cannot remove '").append(fileName).append("': Is a directory\n");
                continue;
            }
            if (!node.delete()) {
                output.append("rm: cannot remove '").append(fileName).append("': Operation not permitted\n");
            }
        }
        return output.toString();
    }
    
    private FileNode resolveFile(TerminalState state, String fileName) {
        Directory current = state.getCurrentDirectory();
        if (fileName.startsWith("/")) return resolveAbsolutePath(state.getRootDirectory(), fileName);
        return resolveRelativePath(current, fileName);
    }
    
    private FileNode resolveAbsolutePath(Directory root, String path) {
        String[] parts = path.split("/");
        Directory current = root;
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isEmpty() || ".".equals(parts[i])) continue;
            if ("..".equals(parts[i])) {
                if (current.getParent() != null) current = current.getParent();
            } else {
                FileNode node = current.getChild(parts[i]);
                if (node == null) return null;
                if (i == parts.length - 1) return node;
                if (!node.isDirectory()) return null;
                current = (Directory) node;
            }
        }
        return current;
    }
    
    private FileNode resolveRelativePath(Directory current, String path) {
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty() || ".".equals(parts[i])) continue;
            if ("..".equals(parts[i])) {
                if (current.getParent() != null) current = current.getParent();
            } else {
                FileNode node = current.getChild(parts[i]);
                if (node == null) return null;
                if (i == parts.length - 1) return node;
                if (!node.isDirectory()) return null;
                current = (Directory) node;
            }
        }
        return current;
    }
    
    @Override
    public String getName() { return "rm"; }
    @Override
    public String getDescription() { return "Remove files"; }
    @Override
    public String getUsage() { return "rm FILE..."; }
}
