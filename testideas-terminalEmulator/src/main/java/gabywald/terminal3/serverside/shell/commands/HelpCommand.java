package gabywald.terminal3.serverside.shell.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandFactory;
import gabywald.terminal3.serverside.shell.ICommand;

/**
 * help command - Display help information
 * @author Gabriel Chandesris (2026)
 */
public class HelpCommand implements ICommand {
	
	@Override
	public String execute(TerminalState state, String[] args, String stdin) 
		{ return this.execute(state, args); }
	
    @Override
    public String execute(TerminalState state, String[] args) {
        if (args.length == 0) 
        	{ return this.getGeneralHelp(); } 
        else { return this.getCommandHelp(args[0]); }
    }
    
    private String getGeneralHelp() {
        StringBuilder help = new StringBuilder();
        help.append("Available commands:\n\n");
        Map<String, ICommand> commands = CommandFactory.getAllCommands();
        List<String> keys = commands.keySet().stream().sorted().collect(Collectors.toList());
        keys.stream().forEach(key -> {
        	ICommand cmd = commands.get(key);
        	help.append(String.format("  %-15s %s\n", cmd.getName(), cmd.getDescription()));
        });
        help.append("\nType 'help <command>' for more information about a specific command.\n");
        return help.toString();
    }
    
    private String getCommandHelp(String commandName) {
    	ICommand cmd = CommandFactory.getCommand(commandName);
        if (cmd == null) 
        	{ return "help: no help topics match '" + commandName + "'.\nTry 'help' for a list of available commands."; }
        return String.format("Usage: %s\n\n%s\n", cmd.getUsage(), cmd.getDescription());
    }
    
    @Override
    public String getName() { return "help"; }
    @Override
    public String getDescription() { return "Display help information"; }
    @Override
    public String getUsage() { return "Usage: help [COMMAND]"; }
    
}
 