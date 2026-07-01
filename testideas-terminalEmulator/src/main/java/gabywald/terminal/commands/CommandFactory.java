package gabywald.terminal.commands;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating and registering available commands.
 * @author Gabriel Chandesris (2026)
 */
public class CommandFactory {
	
    private static final Map<String, Command> commands = new HashMap<String, Command>();
    
    static {
    	CommandFactory.registerCommand(new LsCommand());
    	CommandFactory.registerCommand(new CdCommand());
    	CommandFactory.registerCommand(new PwdCommand());
    	CommandFactory.registerCommand(new CatCommand());
    	CommandFactory.registerCommand(new EchoCommand());
    	CommandFactory.registerCommand(new TouchCommand());
    	CommandFactory.registerCommand(new MkdirCommand());
    	CommandFactory.registerCommand(new RmCommand());
    	CommandFactory.registerCommand(new RmdirCommand());
    	CommandFactory.registerCommand(new CpCommand());
    	CommandFactory.registerCommand(new MvCommand());
    	CommandFactory.registerCommand(new ClearCommand());
    	CommandFactory.registerCommand(new HelpCommand());
    	CommandFactory.registerCommand(new ExitCommand());
    }
    
    public static void registerCommand(Command command) {
        if (command != null) { CommandFactory.commands.put(command.getName().toLowerCase(), command); }
    }
    
    public static Command getCommand(String name) {
        if (name == null || name.isEmpty()) { return null; }
        return CommandFactory.commands.get(name.toLowerCase());
    }
    
    public static boolean hasCommand(String name)		{ return CommandFactory.getCommand(name) != null; }
    public static Map<String, Command> getAllCommands()	{ return new HashMap<>(CommandFactory.commands); }
    public static String[] getCommandNames()			{ return CommandFactory.commands.keySet().toArray(new String[0]); }
    
}
