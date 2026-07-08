package gabywald.terminal3.serverside.shell.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gabywald.terminal3.serverside.filesystem.TerminalDirectory;
import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandHelper;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * ls command - List directory contents
 * @author Gabriel Chandesris (2026)
 */
public class LsCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) 
		{ return this.execute(state, args); }
    
    @Override
    public String execute(TerminalState state, String[] args) {
        boolean longFormat = false;
        boolean showHidden = false;
        boolean reverseOrder = false;
        String targetPath = ".";
        
        List<String> paths = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith("-")) {
                if (arg.contains("l")) { longFormat = true; }
                if (arg.contains("a")) { showHidden = true; }
                if (arg.contains("r")) { reverseOrder = true; }
            } else if (!arg.isEmpty()) { paths.add(arg); }
        }
        
        if (!paths.isEmpty()) { targetPath = paths.get(0); }
        
        TerminalDirectory targetDir = CommandHelper.resolveDirectory(state, targetPath);
        if (targetDir == null) { return "ls: cannot access '" + targetPath + "': No such file or directory"; }
        
        List<TerminalNode> children = new ArrayList<>(targetDir.getChildren());
        if (!showHidden) { children.removeIf(node -> node.getName().startsWith(".")); }
        
        final boolean reverseO = reverseOrder;
        Collections.sort(children, (n1, n2) -> {
            int result = n1.getName().compareTo(n2.getName());
            return (reverseO ? -result : result);
        });
        
        StringBuilder output = new StringBuilder();
        if (longFormat) {
            output.append("total ").append(children.size()).append("\n");
            for (TerminalNode node : children) output.append(CommandHelper.formatLong(node));
        } else {
            for (int i = 0; i < children.size(); i++) {
                if (i > 0) { output.append("  "); }
                output.append(children.get(i).getName());
            }
            if (!children.isEmpty()) { output.append("\n"); }
        }
        return output.toString();
    }
    
    @Override
    public String getName() { return "ls"; }
    @Override
    public String getDescription() { return "List directory contents"; }
    @Override
    public String getUsage() { return "ls [OPTION]... [FILE]..."; }
    
}
