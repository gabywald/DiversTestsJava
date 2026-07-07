package gabywald.terminal3.serverside.shell.commands;

import gabywald.terminal3.clientside.gui.TerminalState;
import gabywald.terminal3.serverside.filesystem.TerminalFile;
import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * cat command - Concatenate and print files
 * @author Gabriel Chandesris (2026)
 */
public class CatCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) {
		// TODO implement with stdin NOT NULL !
		return (stdin == null)? this.execute(state, args) : null ;
	}
	
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) { return "cat: missing operand"; }
        
        StringBuilder output = new StringBuilder();
        for (String fileName : args) {
            TerminalNode node = CommandHelper.resolveFile(state, fileName);
            if (node == null) {
                output.append("cat: ").append(fileName).append(": No such file or directory\n");
                continue;
            }
            if (!node.isFile()) {
                output.append("cat: ").append(fileName).append(": Is a directory\n");
                continue;
            }
            TerminalFile file = (TerminalFile) node;
            output.append(file.getContent());
        }
        return output.toString();
    }
    
    @Override
    public String getName() { return "cat"; }
    @Override
    public String getDescription() { return "Concatenate and print files"; }
    @Override
    public String getUsage() { return "cat [FILE]..."; }

}
