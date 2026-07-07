package gabywald.terminal3.clientside.gui;

import java.util.ArrayList;
import java.util.List;

import gabywald.terminal3.serverside.filesystem.TerminalDirectory;

/**
 * Global state of the terminal including current directory, history, etc.
 * @author Gabriel Chandesris (2026)
 */
public class TerminalState {
    private TerminalDirectory currentDirectory;
    private TerminalDirectory rootDirectory;
    private List<String> commandHistory;
    private int historyIndex;
    private String prompt;
    
    public TerminalState() {
        this.rootDirectory = new TerminalDirectory("/", null);
        this.currentDirectory = this.rootDirectory;
        this.commandHistory = new ArrayList<>();
        this.historyIndex = -1;
        this.prompt = "user@terminal:~$";
    }
    
    public TerminalState(TerminalDirectory root) {
        this.rootDirectory = root;
        this.currentDirectory = root;
        this.commandHistory = new ArrayList<>();
        this.historyIndex = -1;
        this.prompt = "user@terminal:~$";
    }
    
    public TerminalDirectory getCurrentDirectory() { return currentDirectory; }
    public void setCurrentDirectory(TerminalDirectory currentDirectory) {
        this.currentDirectory = currentDirectory;
        updatePrompt();
    }
    public TerminalDirectory getRootDirectory()		{ return this.rootDirectory; }
    public List<String> getCommandHistory()	{ return new ArrayList<String>(commandHistory); }
    
    public void addToHistory(String command) {
        if (command != null && !command.trim().isEmpty()) {
        	this.commandHistory.add(command);
        	this.historyIndex = this.commandHistory.size();
        }
    }
    
    public int getHistoryIndex()	{ return historyIndex; }
    public void setHistoryIndex(int historyIndex)	{ this.historyIndex = historyIndex; }
    public String getPrompt()	{ return this.prompt; }
    
    private void updatePrompt() {
        String path = this.currentDirectory.getPath();
        if ("/".equals(path)) { this.prompt = "user@terminal:~$"; }
        else { this.prompt = "user@terminal:" + path + "$"; }
    }
    
    public String getPreviousCommand() {
        if (this.commandHistory.isEmpty()) return null;
        if (this.historyIndex > 0) {
        	this.historyIndex--;
            return this.commandHistory.get(this.historyIndex);
        } else if (this.historyIndex == 0) {
            return this.commandHistory.get(0);
        }
        return null;
    }
    
    public String getNextCommand() {
        if (this.commandHistory.isEmpty()) return null;
        if (this.historyIndex < commandHistory.size() - 1) {
        	this.historyIndex++;
            return this.commandHistory.get(this.historyIndex);
        } else if (this.historyIndex == this.commandHistory.size() - 1) {
        	this.historyIndex = this.commandHistory.size();
            return "";
        }
        return null;
    }
    
    public void resetHistoryIndex() { this.historyIndex = this.commandHistory.size(); }
}
