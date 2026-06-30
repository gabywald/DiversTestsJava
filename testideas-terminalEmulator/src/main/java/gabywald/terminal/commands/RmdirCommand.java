package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.Directory;
import gabywald.terminal.filesystem.FileNode;

/**
 * rmdir command - Remove empty directories
 */
public class RmdirCommand implements Command {
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) return "rmdir: missing operand";
        
        StringBuilder output = new StringBuilder();
        for (String dirName : args) {
            FileNode node = resolveDirectory(state, dirName);
            if (node == null) {
                output.append("rmdir: failed to remove '").append(dirName).append("': No such file or directory\n");
                continue;
            }
            if (!node.isDirectory()) {
                output.append("rmdir: failed to remove '").append(dirName).append("': Not a directory\n");
                continue;
            }
            Directory dir = (Directory) node;
            if (!dir.isEmpty()) {
                output.append("rmdir: failed to remove '").append(dirName).append("': Directory not empty\n");
                continue;
            }
            if (!dir.delete()) {
                output.append("rmdir: failed to remove '").append(dirName).append("': Operation not permitted\n");
            }
        }
        return output.toString();
    }
    
    private Directory resolveDirectory(TerminalState state, String dirName) {
        Directory current = state.getCurrentDirectory();
        if (dirName.startsWith("/")) return resolveAbsoluteDirectory(state.getRootDirectory(), dirName);
        return resolveRelativeDirectory(current, dirName);
    }
    
    private Directory resolveAbsoluteDirectory(Directory root, String path) {
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
    
    private Directory resolveRelativeDirectory(Directory current, String path) {
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
    public String getName() { return "rmdir"; }
    @Override
    public String getDescription() { return "Remove empty directories"; }
    @Override
    public String getUsage() { return "rmdir DIRECTORY..."; }
}
