package gabywald.terminal.commands;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating and registering available commands.
 */
public class CommandFactory {
    private static final Map<String, Command> commands = new HashMap<>();
    
    static {
        registerCommand(new LsCommand());
        registerCommand(new CdCommand());
        registerCommand(new PwdCommand());
        registerCommand(new CatCommand());
        registerCommand(new EchoCommand());
        registerCommand(new TouchCommand());
        registerCommand(new MkdirCommand());
        registerCommand(new RmCommand());
        registerCommand(new RmdirCommand());
        registerCommand(new CpCommand());
        registerCommand(new MvCommand());
        registerCommand(new ClearCommand());
        registerCommand(new HelpCommand());
        registerCommand(new ExitCommand());
    }
    
    public static void registerCommand(Command command) {
        if (command != null) commands.put(command.getName().toLowerCase(), command);
    }
    
    public static Command getCommand(String name) {
        if (name == null || name.isEmpty()) return null;
        return commands.get(name.toLowerCase());
    }
    
    public static boolean hasCommand(String name) { return getCommand(name) != null; }
    public static Map<String, Command> getAllCommands() { return new HashMap<>(commands); }
    public static String[] getCommandNames() { return commands.keySet().toArray(new String[0]); }
}
