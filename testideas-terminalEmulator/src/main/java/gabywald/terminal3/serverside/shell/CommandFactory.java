package gabywald.terminal3.serverside.shell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gabywald.terminal3.serverside.TerminalServer;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * Factory for creating and registering available commands.
 * @author Gabriel Chandesris (2026)
 */
public class CommandFactory {
	
    private static final Map<String, ICommand> commands = new HashMap<String, ICommand>();
    
    static {
    	CommandFactory.getCommands().stream().forEach( cmd -> CommandFactory.registerCommand(cmd));
    }
    
	private static List<ICommand> getCommands() {
		String packagePath		= TerminalServer.getProperty( "gabywald.terminal.server.commands.package" );
		List<String> classes	= Arrays.asList( TerminalServer.getProperty( "gabywald.terminal.server.commands.list" ).split( ";" ) );
		return CommandFactory.getCommands(packagePath, classes);
	}
	
	private static List<ICommand> getCommands(String packageName, List<String> classesNames) {
		List<ICommand> toReturn = new ArrayList<ICommand>();
		
		for (String classeName : classesNames) {
			String completeClass = packageName + "." + classeName;
			Logger.printlnLog(LoggerLevel.LL_NONE, completeClass);
			try {
				Class<ICommand> loadClass = CommandFactory.extraction(completeClass);
				Logger.printlnLog(LoggerLevel.LL_INFO, loadClass.toString());
				toReturn.add( loadClass.newInstance() );
			} 
			catch (ClassNotFoundException e) { e.printStackTrace(); } 
			catch (InstantiationException e) { e.printStackTrace(); } 
			catch (IllegalAccessException e) { e.printStackTrace(); } 
		}
		
		return toReturn;
	}
	
	@SuppressWarnings("unchecked")
	private static Class<ICommand> extraction(String completeClass) throws ClassNotFoundException {
		return (Class<ICommand>) CommandFactory.class.getClassLoader().loadClass( completeClass );
	}
    
    public static void registerCommand(ICommand command) {
        if (command != null) { CommandFactory.commands.put(command.getName().toLowerCase(), command); }
    }
    
    public static ICommand getCommand(String name) {
        if (name == null || name.isEmpty()) { return null; }
        return CommandFactory.commands.get(name.toLowerCase());
    }
    
    public static boolean hasCommand(String name)			{ return CommandFactory.getCommand(name) != null; }
    public static Map<String, ICommand> getAllCommands()	{ return new HashMap<>(CommandFactory.commands); }
    public static String[] getCommandNames()				{ return CommandFactory.commands.keySet().toArray(new String[0]); }
    
}
