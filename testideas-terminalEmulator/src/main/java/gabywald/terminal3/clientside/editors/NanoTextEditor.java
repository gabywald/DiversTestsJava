package gabywald.terminal3.clientside.editors;

import java.util.ArrayList;
import java.util.List;

import gabywald.terminal3.clientside.TerminalClient;
import gabywald.terminal3.clientside.gui.TerminalFrame;
import gabywald.terminal3.serverside.shell.CommandFactory;

/**
 * Éditeur nano adapté à Swing.
 * Permet d'éditer un fichier ligne par ligne.
 * @author Gabriel Chandesris (2026)
 */
public class NanoTextEditor implements ITextEditor {
	private String fileName;
	private List<String> lines;

	public NanoTextEditor(String fileName) {
		this.fileName = fileName;
		this.lines = new ArrayList<>();

		String content = TerminalClient.getInstance().callCommandServer("cat " + fileName).getFirst();
		if (content != null) {
			for (String line : content.split("\n")) 
				{ this.lines.add(line); }
		}
	}

	@Override
	public void start() {
		TerminalFrame.getInstance().appendOutput("--- Nano: Edit '" + this.fileName + "' ---\n");
		TerminalFrame.getInstance().appendOutput("Commands: :wq (record and quit), :q (quit)\n");
		if (!this.lines.isEmpty()) {
			TerminalFrame.getInstance().appendOutput("Actual Content:\n");
			for (String line : this.lines) {
				TerminalFrame.getInstance().appendOutput(line + "\n");
			}
		}
		TerminalFrame.getInstance().appendOutput(this.getName() + "> ");
	}

	@Override
	public String handleInput(String input) {
		if (input.equals(":wq")) {
			return "SAVE:" + String.join("\n", lines);
		} else if (input.equals(":q")) {
			return "CANCEL";
		} else {
			this.lines.add(input);
			return null;
		}
	}

	@Override
	public String getName() { return "nano"; }
}
