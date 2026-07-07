package gabywald.terminal3.serverside.filesystem;

import java.time.LocalDateTime;

/**
 * Abstract base class for file system nodes (files and directories).
 * @author Gabriel Chandesris (2026)
 */
public abstract class TerminalNode implements Comparable<TerminalNode> {
	
    protected String name;
    protected TerminalDirectory parent;
    protected LocalDateTime createdAt;
    protected LocalDateTime modifiedAt;
    
    public TerminalNode(String name, TerminalDirectory parent) {
        this.name = name;
        this.parent = parent;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }
    
    public String getName()					{ return name; }
    public void setName(String name)		{ this.name = name; this.modifiedAt = LocalDateTime.now(); }
    public TerminalDirectory getParent()	{ return parent; }
    public void setParent(TerminalDirectory parent)	{ this.parent = parent; this.modifiedAt = LocalDateTime.now(); }
    public LocalDateTime getCreatedAt()		{ return createdAt; }
    public LocalDateTime getModifiedAt()	{ return modifiedAt; }
    
    public abstract String getPath();
    public abstract boolean isDirectory();
    public abstract boolean isFile();
    public abstract long getSize();
    public abstract boolean delete();
    
    @Override
    public String toString() { return name; }
    
    @Override
    public int compareTo(TerminalNode other) { return this.name.compareTo(other.name); }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null || this.getClass() != obj.getClass()) { return false;}
        TerminalNode fileNode = (TerminalNode) obj;
        return name.equals(fileNode.name) && 
               ( (parent == null) ? fileNode.parent == null : parent.equals(fileNode.parent));
    }
    
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + ( (parent != null) ? parent.hashCode() : 0);
        return result;
    }
    
}
