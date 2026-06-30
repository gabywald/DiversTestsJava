package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.Directory;
import gabywald.terminal.filesystem.FileNode;
import gabywald.terminal.filesystem.TerminalFile;

/**
 * cp command - Copy files
 */
public class CpCommand implements Command {
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length < 2) {
            return "cp: missing destination file operand after '" + 
                   (args.length > 0 ? args[args.length - 1] : "") + "'\nTry 'cp --help' for more information.";
        }
        
        if (args.length == 2) {
            return copyFile(state, args[0], args[1]);
        }
        
        String destination = args[args.length - 1];
        Directory destDir = resolveDirectory(state, destination);
        if (destDir == null || !destDir.isDirectory()) {
            return "cp: target '" + destination + "' is not a directory";
        }
        
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < args.length - 1; i++) {
            String result = copyFile(state, args[i], destination + "/" + getSimpleName(args[i]));
            if (!result.isEmpty()) output.append(result).append("\n");
        }
        return output.toString();
    }
    
    private String copyFile(TerminalState state, String sourcePath, String destPath) {
        FileNode source = resolveFile(state, sourcePath);
        if (source == null) return "cp: cannot stat '" + sourcePath + "': No such file or directory";
        if (source.isDirectory()) return "cp: -r not specified; omitting directory '" + sourcePath + "'";
        
        Directory destParent = getParentDirectory(state, destPath);
        if (destParent == null) return "cp: cannot create '" + destPath + "': No such file or directory";
        
        String destName = getSimpleName(destPath);
        if (destParent.hasChild(destName)) {
            FileNode existing = destParent.getChild(destName);
            if (existing.isDirectory()) return "cp: cannot overwrite directory '" + destPath + "' with '" + sourcePath + "'";
            ((TerminalFile) existing).setContent(((TerminalFile) source).getContent());
        } else {
            TerminalFile sourceFile = (TerminalFile) source;
            TerminalFile newFile = new TerminalFile(destName, destParent, sourceFile.getContent());
            destParent.addChild(newFile);
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
    public String getName() { return "cp"; }
    @Override
    public String getDescription() { return "Copy files and directories"; }
    @Override
    public String getUsage() { return "cp [OPTION] SOURCE DEST\n       cp [OPTION] SOURCE... DIRECTORY"; }
}