package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.Directory;
import gabywald.terminal.filesystem.FileNode;
import gabywald.terminal.filesystem.TerminalFile;

/**
 * mv command - Move or rename files
 */
public class MvCommand implements Command {
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
        Directory destDir = resolveDirectory(state, destination);
        if (destDir == null || !destDir.isDirectory()) {
            return "mv: target '" + destination + "' is not a directory";
        }
        
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < args.length - 1; i++) {
            String result = moveFile(state, args[i], destination + "/" + getSimpleName(args[i]));
            if (!result.isEmpty()) output.append(result).append("\n");
        }
        return output.toString();
    }
    
    private String moveFile(TerminalState state, String sourcePath, String destPath) {
        FileNode source = resolveFile(state, sourcePath);
        if (source == null) return "mv: cannot stat '" + sourcePath + "': No such file or directory";
        
        Directory destParent = getParentDirectory(state, destPath);
        if (destParent == null) return "mv: cannot move '" + sourcePath + "' to '" + destPath + "': No such file or directory";
        
        String destName = getSimpleName(destPath);
        if (destParent.hasChild(destName)) {
            FileNode existing = destParent.getChild(destName);
            if (existing.isDirectory()) return "mv: cannot overwrite directory '" + destPath + "' with '" + sourcePath + "'";
            if (source.isFile()) {
                ((TerminalFile) existing).setContent(((TerminalFile) source).getContent());
            }
            source.delete();
        } else {
            source.setName(destName);
            source.getParent().removeChild(source);
            destParent.addChild(source);
        }
        return "";
    }
    
    private FileNode resolveFile(TerminalState state, String fileName) {
        Directory current = state.getCurrentDirectory();
        if (fileName.startsWith("/")) return resolveAbsolutePath(state.getRootDirectory(), fileName);
        return resolveRelativePath(current, fileName);
    }
    
    private Directory resolveDirectory(TerminalState state, String dirName) {
        Directory current = state.getCurrentDirectory();
        if (dirName.startsWith("/")) return resolveAbsoluteDirectory(state.getRootDirectory(), dirName);
        return resolveRelativeDirectory(current, dirName);
    }
    
    private Directory getParentDirectory(TerminalState state, String path) {
        if (path.startsWith("/")) return getParentFromAbsolutePath(state.getRootDirectory(), path);
        return getParentFromRelativePath(state.getCurrentDirectory(), path);
    }
    
    private String getSimpleName(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
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
    public String getName() { return "mv"; }
    @Override
    public String getDescription() { return "Move or rename files"; }
    @Override
    public String getUsage() { return "mv [OPTION] SOURCE DEST\n       mv [OPTION] SOURCE... DIRECTORY"; }
}
