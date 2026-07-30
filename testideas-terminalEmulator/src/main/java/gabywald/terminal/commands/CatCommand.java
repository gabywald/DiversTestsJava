package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;
import gabywald.terminal.filesystem.TerminalNode;
import gabywald.terminal.filesystem.TerminalFile;

/**
 * cat command - Concatenate and print files
 * @author Gabriel Chandesris (2026)
 */
public class CatCommand implements Command {
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) return "cat: missing operand";
        
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
