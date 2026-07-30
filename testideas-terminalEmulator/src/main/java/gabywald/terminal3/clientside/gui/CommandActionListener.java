package gabywald.terminal3.clientside.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gabywald.global.structures.Pair;
import gabywald.terminal3.clientside.TerminalClient;
import gabywald.terminal3.clientside.editors.ITextEditor;
import gabywald.terminal3.clientside.editors.NanoTextEditor;
import gabywald.terminal3.clientside.editors.VimTextEditor;
import gabywald.terminal3.serverside.shell.CommandFactory;
import gabywald.terminal3.serverside.shell.CommandParser;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * 
 * @author Gabriel Chandesris (2026)
 */
public class CommandActionListener implements ActionListener {
	private TerminalFrame localtf = null;
	
	// For Edition Mode
	private boolean isEditing = false;
	private String currentEditFile = null;
	private ITextEditor currentEditor = null;
	private String previousPrompt = null;
	
	CommandActionListener(TerminalFrame tf) 
		{ this.localtf = tf; }

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = this.localtf.getInputField().getText().trim();
		if (command.isEmpty()) {
			this.localtf.clearInputLine();
			return;
		}
		
		this.localtf.getTerminalHistory().addToHistory(command);
		this.localtf.getTerminalHistory().resetHistoryIndex();
		this.localtf.appendOutput(this.localtf.getPrompt() + " " + command + "\n");
		
		String[] parts = CommandParser.parse(command);
		if (parts.length == 0) {
			this.localtf.clearInputLine();
			return;
		}
		
		if (this.isEditing) {
			this.handleEditorInput(command);
			return;
		}

		if (command.startsWith("edit ")) {
			this.startEditMode(command.substring(5).trim());
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
			// String result = // cmd.execute(this.localtf.getTerminalState(), cmdArgs);
			Pair<String, String> result = TerminalClient.getInstance().callCommandServer(command); 
			TerminalFrame.getInstance().setPrompt(result.getSecond());
			if ("EXIT".equals(result.getFirst())) // exit 
				{ System.exit(0); } 
			else if (result.getFirst().contains("\033[H\033[2J")) // clear
				{ this.localtf.clearScreen(); } // this.localtf.printWelcomeMessage();
			else { this.localtf.appendOutput(result.getFirst() + "\n"); }
		}
		
		this.localtf.updatePrompt();
		this.localtf.clearInputLine();
	}
	
	
	/**
	 * Start Editing mode (nano or vim)
	 */
	private void startEditMode(String args) {
		this.isEditing = true;
		String[] editArgs = args.split("\\s+");
		String editorType = "nano";
		String fileName;

		if (editArgs.length > 0 && (editArgs[0].equals("--nano") || editArgs[0].equals("--vim"))) {
			editorType = editArgs[0].substring(2);
			if (editArgs.length < 2) {
				this.localtf.appendOutput("Usage: edit [--nano|--vim] <filename>\n");
				this.isEditing = false;
				this.localtf.updatePrompt();
				return;
			}
			fileName = editArgs[1];
		} else { fileName = editArgs[0]; }

		this.currentEditFile  = fileName;
		
		if (editorType.equals("nano")) 
			{ this.currentEditor = new NanoTextEditor(fileName); } 
		else 
			{ this.currentEditor = new VimTextEditor(fileName); }

		this.localtf.appendOutput("--- Edition with '" + editorType + "' ---\n");
		this.localtf.setPrompt(this.currentEditor.getName() + ">");
		this.localtf.updatePrompt();
		this.currentEditor.start();
	}

	private void handleEditorInput(String input) {
		String result = this.currentEditor.handleInput(input);
		if (result != null) {
			if (result.startsWith("SAVE:")) {
				Pair<String, String> resultEcho = TerminalClient.getInstance() // .replaceAll("\n", "\\n")
						.callCommandServer("echo '" + result.substring(5) + "' >> " + this.currentEditFile);
				// CommandFactory.getCommand("echo").execute(state, new String[] { result.substring(5) }, this.currentEditFile);
				this.localtf.appendOutput("File '" + this.currentEditFile + "' recorded.\n");
				this.localtf.setPrompt(resultEcho.getSecond());
				this.localtf.updatePrompt();
				this.localtf.clearInputLine();
			} else if (result.equals("CANCEL")) 
				{ this.localtf.appendOutput("Edition Canceled.\n"); }
			this.endEditMode();
		} else { 
			this.localtf.appendOutput(input + "\n");
			this.localtf.clearInputLine();
		}
	}

	private void endEditMode() {
		this.isEditing			= false;
		this.currentEditor		= null;
		this.currentEditFile	= null;
		this.localtf.updatePrompt();
	}
}
