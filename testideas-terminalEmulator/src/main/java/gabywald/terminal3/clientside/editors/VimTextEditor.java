package gabywald.terminal3.clientside.editors;

import java.util.ArrayList;
import java.util.List;

import gabywald.terminal3.clientside.TerminalClient;
import gabywald.terminal3.clientside.gui.TerminalFrame;

/**
 * Éditeur vim adapté à Swing (simplifié).
 * Supporte le mode commande et le mode insertion.
 * @author Gabriel Chandesris (2026)
 */
public class VimTextEditor implements ITextEditor {
	private String fileName;
	private List<String> lines;
	private int currentLineIndex;
	private boolean insertMode = false;

	public VimTextEditor(String fileName) {
		this.fileName = fileName;
		this.lines = new ArrayList<>();
		this.currentLineIndex = 0;

		String content = TerminalClient.getInstance().callCommandServer("cat " + fileName).getFirst();
		if (content != null) {
			for (String line : content.split("\n")) 
				{ this.lines.add(line); }
		}
	}

	@Override
	public void start() {
		TerminalFrame.getInstance().appendOutput("--- Vim: Édition de '" + fileName + "' ---\n");
		TerminalFrame.getInstance().appendOutput("Mode: COMMANDE | Type 'i' to be in INSERT mode. \n");
		TerminalFrame.getInstance().appendOutput("Commands: :wq (record and quit), :q! (quit), :esc (commande mode)\n");
		this.displayCurrentLine();
	}

	/**
	 * Affiche la ligne courante.
	 */
	private void displayCurrentLine() {
		TerminalFrame.getInstance().appendOutput("vim [" + (this.insertMode ? "INSERT" : "COMMAND") + "] > " + this.lines.get(currentLineIndex) + "\n");
	}

	@Override
	public String handleInput(String input) {
		if (this.insertMode) {
			if (input.equals(":esc")) {
				this.insertMode = false;
				TerminalFrame.getInstance().appendOutput("Mode: COMMANDE\n");
				this.displayCurrentLine();
				return null;
			} else {
				this.lines.set(currentLineIndex, this.lines.get(currentLineIndex) + input);
				this.displayCurrentLine();
				return null;
			}
		} else {
			if (input.equals("i")) {
				this.insertMode = true;
				TerminalFrame.getInstance().appendOutput("Mode: INSERT\n");
				this.displayCurrentLine();
				return null;
			} else if (input.equals(":wq")) {
				return "SAVE:" + String.join("\n", this.lines);
			} else if (input.equals(":q!")) {
				return "CANCEL";
			} else if (input.equals(":esc")) {
				this.displayCurrentLine();
				return null;
			} else {
				TerminalFrame.getInstance().appendOutput("Unknown command. Type 'i' to edit. \n");
				this.displayCurrentLine();
				return null;
			}
		}
	}

	@Override
	public String getName() { return "vim"; }
}
