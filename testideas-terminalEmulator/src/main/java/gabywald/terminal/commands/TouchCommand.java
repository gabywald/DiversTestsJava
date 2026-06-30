package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.Directory;
import gabywald.terminal.filesystem.FileNode;
import gabywald.terminal.filesystem.TerminalFile;

/**
 * touch command - Create empty files or update timestamp
 */
public class TouchCommand implements Command {
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) return "touch: missing file operand";
        
        StringBuilder output = new StringBuilder();
        for (String fileName : args) {
            FileNode existing = resolveFile(state, fileName);
            if (existing != null && existing.isDirectory()) {
                output.append("touch: cannot touch '").append(fileName).append("': Is a directory\n");
                continue;
            }
            
            Directory parentDir = getParentDirectory(state, fileName);
            if (parentDir == null) {
                output.append("touch: cannot touch '").append(fileName).append("': No such file or directory\n");
                continue;
            }
            
            String simpleName = getSimpleName(fileName);
            if (existing != null && existing.isFile()) {
                ((TerminalFile) existing).setContent(((TerminalFile)existing).getContent());
            } else {
                parentDir.createFile(simpleName);
            }
        }
        return output.toString();
    }
    
    private FileNode resolveFile(TerminalState state, String fileName) {
        Directory current = state.getCurrentDirectory();
        if (fileName.startsWith("/")) return resolveAbsolutePath(state.getRootDirectory(), fileName);
        return resolveRelativePath(current, fileName);
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
    public String getName() { return "touch"; }
    @Override
    public String getDescription() { return "Create empty files or update timestamp"; }
    @Override
    public String getUsage() { return "touch FILE..."; }
}
