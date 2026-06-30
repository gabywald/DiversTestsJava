package gabywald.terminal;

import java.util.ArrayList;
import java.util.List;

import gabywald.terminal.filesystem.Directory;

/**
 * Global state of the terminal including current directory, history, etc.
 */
public class TerminalState {
    private Directory currentDirectory;
    private Directory rootDirectory;
    private List<String> commandHistory;
    private int historyIndex;
    private String prompt;
    
    public TerminalState() {
        this.rootDirectory = new Directory("/", null);
        this.currentDirectory = rootDirectory;
        this.commandHistory = new ArrayList<>();
        this.historyIndex = -1;
        this.prompt = "user@terminal:~$";
    }
    
    public TerminalState(Directory root) {
        this.rootDirectory = root;
        this.currentDirectory = root;
        this.commandHistory = new ArrayList<>();
        this.historyIndex = -1;
        this.prompt = "user@terminal:~$";
    }
    
    public Directory getCurrentDirectory() { return currentDirectory; }
    public void setCurrentDirectory(Directory currentDirectory) {
        this.currentDirectory = currentDirectory;
        updatePrompt();
    }
    public Directory getRootDirectory() { return rootDirectory; }
    public List<String> getCommandHistory() { return new ArrayList<>(commandHistory); }
    
    public void addToHistory(String command) {
        if (command != null && !command.trim().isEmpty()) {
            commandHistory.add(command);
            historyIndex = commandHistory.size();
        }
    }
    
    public int getHistoryIndex() { return historyIndex; }
    public void setHistoryIndex(int historyIndex) { this.historyIndex = historyIndex; }
    public String getPrompt() { return prompt; }
    
    private void updatePrompt() {
        String path = currentDirectory.getPath();
        if ("/".equals(path)) {
            prompt = "user@terminal:~$";
        } else {
            prompt = "user@terminal:" + path + "$";
        }
    }
    
    public String getPreviousCommand() {
        if (commandHistory.isEmpty()) return null;
        if (historyIndex > 0) {
            historyIndex--;
            return commandHistory.get(historyIndex);
        } else if (historyIndex == 0) {
            return commandHistory.get(0);
        }
        return null;
    }
    
    public String getNextCommand() {
        if (commandHistory.isEmpty()) return null;
        if (historyIndex < commandHistory.size() - 1) {
            historyIndex++;
            return commandHistory.get(historyIndex);
        } else if (historyIndex == commandHistory.size() - 1) {
            historyIndex = commandHistory.size();
            return "";
        }
        return null;
    }
    
    public void resetHistoryIndex() { historyIndex = commandHistory.size(); }
}
