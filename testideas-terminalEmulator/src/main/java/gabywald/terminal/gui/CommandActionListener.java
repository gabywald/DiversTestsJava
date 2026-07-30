package gabywald.terminal.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gabywald.terminal.commands.Command;
import gabywald.terminal.commands.CommandFactory;
import gabywald.terminal.commands.CommandParser;

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
        
        Command cmd = CommandFactory.getCommand(commandName);
        if (cmd == null) {
        	this.localtf.appendOutput(commandName + ": command not found\n");
        } else {
            String result = cmd.execute(this.localtf.getTerminalState(), cmdArgs);
            if ("EXIT".equals(result)) {
                System.exit(0);
            } else if (result.contains("\033[H\033[2J")) {
            	this.localtf.clearScreen();
            	this.localtf.printWelcomeMessage();
            } else {
            	this.localtf.appendOutput(result + "\n");
            }
        }
        
        this.localtf.updatePrompt();
        this.localtf.clearInputLine();
    }
}
