package gabywald.terminal.filesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Directory class representing a folder in the file system.
 */
public class Directory extends FileNode {
    private List<FileNode> children;
    
    public Directory(String name, Directory parent) {
        super(name, parent);
        this.children = new ArrayList<>();
    }
    
    public List<FileNode> getChildren() { return Collections.unmodifiableList(children); }
    
    public boolean addChild(FileNode child) {
        if (child == null) return false;
        for (FileNode existing : children) {
            if (existing.getName().equals(child.getName())) return false;
        }
        child.setParent(this);
        children.add(child);
        this.modifiedAt = java.time.LocalDateTime.now();
        return true;
    }
    
    public boolean removeChild(FileNode child) {
        if (child == null) return false;
        boolean removed = children.remove(child);
        if (removed) {
            child.setParent(null);
            this.modifiedAt = java.time.LocalDateTime.now();
        }
        return removed;
    }
    
    public boolean removeChildByName(String name) {
        FileNode toRemove = getChild(name);
        if (toRemove != null) return removeChild(toRemove);
        return false;
    }
    
    public FileNode getChild(String name) {
        for (FileNode child : children) {
            if (child.getName().equals(name)) return child;
        }
        return null;
    }
    
    public boolean hasChild(String name) { return getChild(name) != null; }
    
    public List<Directory> getSubdirectories() {
        return children.stream()
                .filter(FileNode::isDirectory)
                .map(node -> (Directory) node)
                .collect(Collectors.toList());
    }
    
    public List<TerminalFile> getFiles() {
        return children.stream()
                .filter(FileNode::isFile)
                .map(node -> (TerminalFile) node)
                .collect(Collectors.toList());
    }
    
    @Override
    public String getPath() {
        if (parent == null) return "/";
        String parentPath = parent.getPath();
        if ("/".equals(parentPath)) return "/" + name;
        return parentPath + "/" + name;
    }
    
    @Override
    public boolean isDirectory() { return true; }
    @Override
    public boolean isFile() { return false; }
    @Override
    public long getSize() { return 0; }
    
    @Override
    public boolean delete() {
        if (parent != null) return parent.removeChild(this);
        return false;
    }
    
    public void clear() { children.clear(); this.modifiedAt = java.time.LocalDateTime.now(); }
    public int getChildCount() { return children.size(); }
    public boolean isEmpty() { return children.isEmpty(); }
    
    public Directory createDirectory(String name) {
        if (hasChild(name)) return null;
        Directory dir = new Directory(name, this);
        addChild(dir);
        return dir;
    }
    
    public TerminalFile createFile(String name) {
        if (hasChild(name)) return null;
        TerminalFile file = new TerminalFile(name, this);
        addChild(file);
        return file;
    }
    
    @Override
    public String toString() { return "[DIR] " + name + " (" + children.size() + " items)"; }
}
