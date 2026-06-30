package gabywald.terminal.commands;

import gabywald.terminal.TerminalState;

/**
 * echo command - Display a line of text
 */
public class EchoCommand implements Command {
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) return "";
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) output.append(" ");
            output.append(args[i]);
        }
        return output.toString();
    }
    
    @Override
    public String getName() { return "echo"; }
    @Override
    public String getDescription() { return "Display a line of text"; }
    @Override
    public String getUsage() { return "echo [STRING]..."; }
}
