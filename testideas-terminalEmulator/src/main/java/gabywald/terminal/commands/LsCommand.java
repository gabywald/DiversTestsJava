package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.Directory;
import gabywald.terminal.filesystem.FileNode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ls command - List directory contents
 * @author Gabriel Chandesris (2026)
 */
public class LsCommand implements Command {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @Override
    public String execute(TerminalState state, String[] args) {
        boolean longFormat = false;
        boolean showHidden = false;
        boolean reverseOrder = false;
        String targetPath = ".";
        
        List<String> paths = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith("-")) {
                if (arg.contains("l")) longFormat = true;
                if (arg.contains("a")) showHidden = true;
                if (arg.contains("r")) reverseOrder = true;
            } else if (!arg.isEmpty()) {
                paths.add(arg);
            }
        }
        
        if (!paths.isEmpty()) targetPath = paths.get(0);
        
        Directory targetDir = resolveDirectory(state, targetPath);
        if (targetDir == null) return "ls: cannot access '" + targetPath + "': No such file or directory";
        
        List<FileNode> children = new ArrayList<>(targetDir.getChildren());
        if (!showHidden) children.removeIf(node -> node.getName().startsWith("."));
        
        final boolean reverseO = reverseOrder;
        Collections.sort(children, (n1, n2) -> {
            int result = n1.getName().compareTo(n2.getName());
            return (reverseO ? -result : result);
        });
        
        StringBuilder output = new StringBuilder();
        if (longFormat) {
            output.append("total ").append(children.size()).append("\n");
            for (FileNode node : children) output.append(formatLong(node));
        } else {
            for (int i = 0; i < children.size(); i++) {
                if (i > 0) output.append("  ");
                output.append(children.get(i).getName());
            }
            if (!children.isEmpty()) output.append("\n");
        }
        return output.toString();
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
    
    private String formatLong(FileNode node) {
        String type = node.isDirectory() ? "d" : "-";
        String permissions = node.isDirectory() ? "rwxr-xr-x" : "rw-r--r--";
        String size = node.isDirectory() ? "4096" : String.valueOf(node.getSize());
        String date = node.getModifiedAt().format(DATE_FORMATTER);
        return String.format("%s%s 1 user group %8s %s %s\n", type, permissions, size, date, node.getName());
    }
    
    @Override
    public String getName() { return "ls"; }
    @Override
    public String getDescription() { return "List directory contents"; }
    @Override
    public String getUsage() { return "ls [OPTION]... [FILE]..."; }
}
