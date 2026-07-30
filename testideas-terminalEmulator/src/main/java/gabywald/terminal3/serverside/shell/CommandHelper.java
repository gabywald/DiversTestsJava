package gabywald.terminal3.serverside.shell;

import java.time.format.DateTimeFormatter;

import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;

/**
 * 
 * @author Gabriel Chandesris (2026)
 */
public abstract class CommandHelper {

    public static TerminalDirectory getParentFromAbsolutePath(TerminalDirectory root, String path) {
        String[] parts = path.split("/");
        TerminalDirectory current = root;
        for (int i = 1; i < parts.length - 1; i++) {
            if (parts[i].isEmpty() || ".".equals(parts[i])) { continue; }
            if ("..".equals(parts[i])) {
                if (current.getParent() != null) { current = current.getParent(); }
            } else {
                TerminalNode node = current.getChild(parts[i]);
                if (node == null || !node.isDirectory()) { return null; }
                current = (TerminalDirectory) node;
            }
        }
        return current;
    }
    
    public static TerminalDirectory getParentFromRelativePath(TerminalDirectory current, String path) {
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if (parts[i].isEmpty() || ".".equals(parts[i])) { continue; }
            if ("..".equals(parts[i])) {
                if (current.getParent() != null) current = current.getParent();
            } else {
                TerminalNode node = current.getChild(parts[i]);
                if (node == null || !node.isDirectory()) { return null; }
                current = (TerminalDirectory) node;
            }
        }
        return current;
    }
    
    public static TerminalNode resolveFile(TerminalState state, String fileName) {
        TerminalDirectory current = state.getCurrentDirectory();
        if (fileName.startsWith("/")) { return CommandHelper.resolveAbsolutePath(state.getRootDirectory(), fileName); }
        return CommandHelper.resolveRelativePath(current, fileName);
    }
    
    public static TerminalNode resolveAbsolutePath(TerminalDirectory root, String path) {
        String[] parts = path.split("/");
        TerminalDirectory current = root;
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isEmpty() || ".".equals(parts[i])) { continue; }
            if ("..".equals(parts[i])) {
                if (current.getParent() != null) { current = current.getParent(); }
            } else {
                TerminalNode node = current.getChild(parts[i]);
                if (node == null)			{ return null; }
                if (i == parts.length - 1)	{ return node; }
                if (!node.isDirectory())	{ return null; }
                current = (TerminalDirectory) node;
            }
        }
        return current;
    }
    
    public static TerminalNode resolveRelativePath(TerminalDirectory current, String path) {
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty() || ".".equals(parts[i])) { continue; }
            if ("..".equals(parts[i])) {
                if (current.getParent() != null) { current = current.getParent(); }
            } else {
                TerminalNode node = current.getChild(parts[i]);
                if (node == null)			{ return null; }
                if (i == parts.length - 1)	{ return node; }
                if (!node.isDirectory())	{ return null; }
                current = (TerminalDirectory) node;
            }
        }
        return current;
    }
    
   public static TerminalDirectory resolveDirectory(TerminalState state, String dirName) {
        TerminalDirectory current = state.getCurrentDirectory();
        if (dirName.startsWith("/")) { return CommandHelper.resolveAbsoluteDirectory(state.getRootDirectory(), dirName); }
        return CommandHelper.resolveRelativeDirectory(current, dirName);
    }
    
    static TerminalDirectory resolveAbsoluteDirectory(TerminalDirectory root, String path) {
        String[] parts = path.split("/");
        TerminalDirectory current = root;
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isEmpty() || ".".equals(parts[i])) { continue; }
            if ("..".equals(parts[i])) {
                if (current.getParent() != null) { current = current.getParent(); }
            } else {
                TerminalNode node = current.getChild(parts[i]);
                if (node == null || !node.isDirectory()) { return null; }
                current = (TerminalDirectory) node;
            }
        }
        return current;
    }
    
    static TerminalDirectory resolveRelativeDirectory(TerminalDirectory current, String path) {
        String[] parts = path.split("/");
        for (String part : parts) {
            if (part.isEmpty() || ".".equals(part)) { continue; }
            if ("..".equals(part)) {
                if (current.getParent() != null) { current = current.getParent(); }
            } else {
                TerminalNode node = current.getChild(part);
                if (node == null || !node.isDirectory()) { return null; }
                current = (TerminalDirectory) node;
            }
        }
        return current;
    }
    
    public static TerminalDirectory getParentDirectory(TerminalState state, String path) {
        if (path.startsWith("/")) { return CommandHelper.getParentFromAbsolutePath(state.getRootDirectory(), path); }
        return CommandHelper.getParentFromRelativePath(state.getCurrentDirectory(), path);
    }
    
    public static String getSimpleName(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public static String formatLong(TerminalNode node) {
        String type = node.isDirectory() ? "d" : "-";
        String permissions = node.isDirectory() ? "rwxr-xr-x" : "rw-r--r--";
        String size = node.isDirectory() ? "4096" : String.valueOf(node.getSize());
        String date = node.getModifiedAt().format(DATE_FORMATTER);
        return String.format("%s%s 1 user group %8s %s %s\n", type, permissions, size, date, node.getName());
    }
    
}
