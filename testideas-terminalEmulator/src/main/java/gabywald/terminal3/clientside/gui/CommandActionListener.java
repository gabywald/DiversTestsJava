package gabywald.terminal3.clientside.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gabywald.terminal3.clientside.TerminalClient;
import gabywald.terminal3.serverside.shell.CommandFactory;
import gabywald.terminal3.serverside.shell.CommandParser;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * 
 * @author Gabriel Chandesris (2026)
 */
public class CommandActionListener implements ActionListener {
	private TerminalFrame localtf = null; 
	
    CommandActionListener(TerminalFrame tf) 
    	{ this.localtf = tf; }

	@Override
    public void actionPerformed(ActionEvent e) {
        String command = this.localtf.getInputField().getText().trim();
        if (command.isEmpty()) {
        	this.localtf.clearInputLine();
            return;
        }
        
        this.localtf.getTerminalState().addToHistory(command);
        this.localtf.getTerminalState().resetHistoryIndex();
        this.localtf.appendOutput(this.localtf.getTerminalState().getPrompt() + " " + command + "\n");
        
        String[] parts = CommandParser.parse(command);
        if (parts.length == 0) {
        	this.localtf.clearInputLine();
            return;
        }
        
        String commandName = parts[0];
        String[] cmdArgs = new String[parts.length - 1];
        System.arraycopy(parts, 1, cmdArgs, 0, cmdArgs.length);
        
        ICommand cmd = CommandFactory.getCommand(commandName);
        if (cmd == null) {
        	this.localtf.appendOutput(commandName + ": command not found\n");
        } else {
        	// TODO here call to distant / local server !! (or via the cmd
        	// String result = // cmd.execute(this.localtf.getTerminalState(), cmdArgs);
            String result = TerminalClient.getInstance().callCommandServer(command); 
            if ("EXIT".equals(result)) // exit 
            	{ System.exit(0); } 
            else if (result.contains("\033[H\033[2J")) // clear
            	{ this.localtf.clearScreen(); } // this.localtf.printWelcomeMessage();
            else { this.localtf.appendOutput(result + "\n"); }
        }
        
        this.localtf.updatePrompt();
        this.localtf.clearInputLine();
    }
}
