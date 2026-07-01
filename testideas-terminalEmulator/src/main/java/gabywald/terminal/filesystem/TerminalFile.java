package gabywald.terminal.filesystem;

/**
 * TerminalFile class representing a text file in the file system.
 * @author Gabriel Chandesris (2026)
 */
public class TerminalFile extends FileNode {
    private String content;
    
    public TerminalFile(String name, TerminalDirectory parent) {
        super(name, parent);
        this.content = "";
    }
    
    public TerminalFile(String name, TerminalDirectory parent, String content) {
        super(name, parent);
        this.content = ((content != null) ? content : "");
    }
    
    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content != null ? content : "";
        this.modifiedAt = java.time.LocalDateTime.now();
    }
    
    public void appendContent(String text) {
        if (text != null) {
            this.content += text;
            this.modifiedAt = java.time.LocalDateTime.now();
        }
    }
    
    @Override
    public String getPath() {
        if (parent == null) { return "/" + name; }
        String parentPath = parent.getPath();
        if ("/".equals(parentPath)) { return "/" + name; }
        return parentPath + "/" + name;
    }
    
    @Override
    public boolean isDirectory() { return false; }
    @Override
    public boolean isFile() { return true; }
    @Override
    public long getSize() { return content.getBytes().length; }
    
    @Override
    public boolean delete() {
        if (parent != null) { return parent.removeChild(this); }
        return false;
    }
    
    public void clear() { this.content = ""; this.modifiedAt = java.time.LocalDateTime.now(); }
    public boolean isEmpty() { return content.isEmpty(); }
    
    @Override
    public String toString() { return "[FILE] " + name + " (" + getSize() + " bytes)"; }
}
