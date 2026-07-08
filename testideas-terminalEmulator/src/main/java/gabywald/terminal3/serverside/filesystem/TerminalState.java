package gabywald.terminal3.serverside.filesystem;

/**
 * Global state of the terminal including current directory, history, etc.
 * @author Gabriel Chandesris (2026)
 */
public class TerminalState {
	private static final String ROOT = "/";
	
	private TerminalDirectory currentDirectory;
	private TerminalDirectory rootDirectory;
	private String prompt;
	
	public TerminalState() {
		this.rootDirectory = new TerminalDirectory(TerminalState.ROOT, null);
		this.currentDirectory = this.rootDirectory;
		this.prompt = "user@terminal:~$";
	}
	
	TerminalState(TerminalDirectory root) {
		this.rootDirectory = root;
		this.currentDirectory = root;
		this.prompt = "user@terminal:~$";

	}
	
	public TerminalDirectory getCurrentDirectory() { return currentDirectory; }
	
	public void setCurrentDirectory(TerminalDirectory currentDirectory) {
		this.currentDirectory = currentDirectory;
		this.updatePrompt();
	}
	
	public TerminalDirectory getRootDirectory()		{ return this.rootDirectory; }
	
	public String getPrompt()	{ return this.prompt; }
	
	private void updatePrompt() {
		String path = this.currentDirectory.getPath();
		if (TerminalState.ROOT.equals(path)) { this.prompt = "user@terminal:~$"; }
		else { this.prompt = "user@terminal:" + path + "$"; }
	}
	
}
