package gabywald.terminal3.clientside.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gabriel Chandesris (2026)
 */
public class TerminalHistory {
    private List<String> commandHistory;
    private int historyIndex;
    
    public TerminalHistory() {
        this.commandHistory = new ArrayList<String>();
        this.historyIndex = -1;
    }
    
    public List<String> getCommandHistory()	{ return new ArrayList<String>(commandHistory); }
    
    public void addToHistory(String command) {
        if (command != null && !command.trim().isEmpty()) {
        	this.commandHistory.add(command);
        	this.historyIndex = this.commandHistory.size();
        }
    }
    
    public int getHistoryIndex()
    	{ return historyIndex; }
    
    public void setHistoryIndex(int historyIndex)
    	{ this.historyIndex = historyIndex; }
    
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
