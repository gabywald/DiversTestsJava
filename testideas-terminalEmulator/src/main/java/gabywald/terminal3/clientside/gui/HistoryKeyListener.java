package gabywald.terminal3.clientside.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import gabywald.terminal3.serverside.shell.CommandFactory;
import gabywald.terminal3.serverside.shell.CommandParser;

/**
 * 
 * @author Gabriel Chandesris (2026)
 */
public class HistoryKeyListener extends KeyAdapter {

	private TerminalFrame localtf = null; 
	
	HistoryKeyListener(TerminalFrame tf) 
    	{ this.localtf = tf; }

	@Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            String prevCommand = this.localtf.getTerminalState().getPreviousCommand();
            if (prevCommand != null) { this.localtf.getInputField().setText(prevCommand); }
            e.consume();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            String nextCommand = this.localtf.getTerminalState().getNextCommand();
            if (nextCommand != null) { this.localtf.getInputField().setText(nextCommand); }
            e.consume();
        } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
            handleTabCompletion();
            e.consume();
        }
    }
    
    private void handleTabCompletion() {
        String text = this.localtf.getInputField().getText();
        String[] parts = CommandParser.parse(text);
        if (parts.length == 0) { return; }
        
        if (parts.length == 1) {
            String prefix = parts[0];
            String[] commandNames = CommandFactory.getCommandNames();
            for (String cmdName : commandNames) {
                if (cmdName.startsWith(prefix)) {
                	this.localtf.getInputField().setText(cmdName + " ");
                    return;
                }
            }
        }
    }
}
