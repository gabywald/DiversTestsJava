package gabywald.terminal3.serverside.filesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Directory class representing a folder in the file system.
 * @author Gabriel Chandesris (2026)
 */
public class TerminalDirectory extends TerminalNode {
    private List<TerminalNode> children;
    
    public TerminalDirectory(String name, TerminalDirectory parent) {
        super(name, parent);
        this.children = new ArrayList<TerminalNode>();
    }
    
    public List<TerminalNode> getChildren() { return Collections.unmodifiableList(children); }
    
    public boolean addChild(TerminalNode child) {
        if (child == null) { return false; }
        for (TerminalNode existing : children) {
            if (existing.getName().equals(child.getName())) 
            	{ return false; }
        }
        child.setParent(this);
        this.children.add(child);
        this.modifiedAt = java.time.LocalDateTime.now();
        return true;
    }
    
    public boolean removeChild(TerminalNode child) {
        if (child == null) { return false; }
        boolean removed = this.children.remove(child);
        if (removed) {
            child.setParent(null);
            this.modifiedAt = java.time.LocalDateTime.now();
        }
        return removed;
    }
    
    public boolean removeChildByName(String name) {
        TerminalNode toRemove = getChild(name);
        if (toRemove != null) { return removeChild(toRemove); }
        return false;
    }
    
    public TerminalNode getChild(String name) {
        for (TerminalNode child : children) {
            if (child.getName().equals(name)) 
            	{ return child; }
        }
        return null;
    }
    
    public boolean hasChild(String name) { return getChild(name) != null; }
    
    public List<TerminalDirectory> getSubdirectories() {
        return this.children.stream()
                .filter(TerminalNode::isDirectory)
                .map(node -> (TerminalDirectory) node)
                .collect(Collectors.toList());
    }
    
    public List<TerminalFile> getFiles() {
        return this.children.stream()
                .filter(TerminalNode::isFile)
                .map(node -> (TerminalFile) node)
                .collect(Collectors.toList());
    }
    
    @Override
    public String getPath() {
        if (this.parent == null) { return "/"; }
        String parentPath = parent.getPath();
        if ("/".equals(parentPath)) return "/" + this.name;
        return parentPath + "/" + this.name;
    }
    
    @Override
    public boolean isDirectory() { return true; }
    @Override
    public boolean isFile() { return false; }
    @Override
    public long getSize() { return 0; }
    
    @Override
    public boolean delete() {
        if (this.parent != null) { return this.parent.removeChild(this); }
        return false;
    }
    
    public void clear()			{ this.children.clear(); this.modifiedAt = java.time.LocalDateTime.now(); }
    public int getChildCount()	{ return this.children.size(); }
    public boolean isEmpty()	{ return this.children.isEmpty(); }
    
    public TerminalDirectory createDirectory(String name) {
        if (this.hasChild(name)) { return null; }
        TerminalDirectory dir = new TerminalDirectory(name, this);
        this.addChild(dir);
        return dir;
    }
    
    public TerminalFile createFile(String name) {
        if (this.hasChild(name)) { return null; }
        TerminalFile file = new TerminalFile(name, this);
        this.addChild(file);
        return file;
    }
    
    @Override
    public String toString() { return "[DIR] " + this.name + " (" + this.children.size() + " items)"; }
}
