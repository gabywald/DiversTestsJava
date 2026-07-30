package gabywald.terminal3.serverside.shell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gabywald.terminal3.serverside.filesystem.TerminalNode;
import gabywald.terminal3.serverside.filesystem.TerminalState;

/**
 * Command parser for extracting command name and arguments.
 * Handles quoted arguments with spaces.
 * @author Gabriel Chandesris (2026)
 */
public class CommandParser {
	
	public static String[] parse(String input) {
		if (input == null || input.trim().isEmpty()) return new String[0];
		
		String trimmed = input.trim();
		List<String> tokens = new ArrayList<>();
		boolean inQuotes = false;
		StringBuilder current = new StringBuilder();
		
		for (int i = 0; i < trimmed.length(); i++) {
			char c = trimmed.charAt(i);
			if (c == '"') {
				inQuotes = !inQuotes;
			} else if (Character.isWhitespace(c) && !inQuotes) {
				if (current.length() > 0) {
					tokens.add(current.toString());
					current.setLength(0);
				}
			} else { current.append(c); }
		}
		
		if (current.length() > 0) tokens.add(current.toString());
		return tokens.toArray(new String[0]);
	}
	
	public static String getCommandName(String input) {
		String[] parts = parse(input);
		return parts.length > 0 ? parts[0] : "";
	}
	
	public static String[] getArguments(String input) {
		String[] parts = parse(input);
		if (parts.length > 1) {
			String[] args = new String[parts.length - 1];
			System.arraycopy(parts, 1, args, 0, args.length);
			return args;
		}
		return new String[0];
	}
	
	public static boolean hasArguments(String input) { return getArguments(input).length > 0; }
	
	public static String cleanArgument(String arg) {
		if (arg == null) { return ""; }
		return arg.replaceAll("^$", "").replaceAll("\"", ""); // NOTE "^\"|"$"
	}
	
	/* ***** ***** ***** ****** ***** */
	
	/**
	 * Execute Command with redirections and pipes
	 * @param input Complete Command (ex: "ls | grep txt").
	 * @param currentDirectory Current Directory
	 * @return Execution Result
	 */
	public static String execute(TerminalState state, String input) {
		input = input.trim();
		if (input.isEmpty()) { return ""; }

		if (input.contains("|")) { return CommandParser.executePipe(state, input); }

		String[] args = input.split("\\s+");
		List<Redirection4Command> redirections = Redirection4Command.parseRedirections(args);

		if (redirections.isEmpty()) 
			{ return CommandParser.executeCommand(state, args, null); } 
		else 
			{ return CommandParser.executeWithRedirections(state, redirections); }
	}

	/**
	 * Execute command with Redirections
	 */
	private static String executeWithRedirections(TerminalState state, List<Redirection4Command> redirections) {
		Redirection4Command firstRedirection = redirections.get(0);
		String[] args = firstRedirection.cleanedArgs;

		if (args.length == 0) { return "INVALID COMMAND"; }

		String commandName = args[0];
		ICommand command = CommandFactory.getCommand(commandName);
		if (command == null) { return "UNKNOWN COMMAND: " + commandName; }

		String stdin = null;
		for (Redirection4Command redirection : redirections) {
			if (redirection.getType() == Redirection4Command.Type.INPUT) {
				TerminalNode redirFile = CommandHelper.resolveFile(state, redirection.getFileName());
				if (redirFile == null) 
					{ return "Unknown File for input: " + redirection.getFileName(); }
				else 
					{ stdin = CommandFactory.getCommand("cat").execute(state, new String[] { redirection.getFileName() }); }
			}
		}

		String output = command.execute(state, CommandParser.removefirstFrom(args), stdin);

		for (Redirection4Command redirection : redirections) {
			if (redirection.getType() == Redirection4Command.Type.OUTPUT 
					|| redirection.getType() == Redirection4Command.Type.APPEND) {
				
				if (redirection.getType() == Redirection4Command.Type.OUTPUT) 
					{ CommandFactory.getCommand("echo").execute(state, new String[] { output }, redirection.getFileName()); }
				else { // Redirection.Type.APPEND
					TerminalNode redirFile = CommandHelper.resolveFile(state, redirection.getFileName());
					String existingContent = (redirFile == null) ? "" : 
									CommandFactory.getCommand("cat").execute(state, new String[] { redirection.getFileName() });
					CommandFactory.getCommand("echo").execute(state, new String[] { existingContent + "\n" + output }, redirection.getFileName());
				}
				return "";
			}
		}

		return output;
	}

	/**
	 * Execute Command without Redirection
	 */
	private static String executeCommand(TerminalState state, String[] args, String stdin) {
		if (args.length == 0) { return ""; }

		String commandName = args[0];
		ICommand command = CommandFactory.getCommand(commandName);
		if (command == null) { return "UNKNOWN COMMAND: " + commandName; }
		
		return command.execute(state, CommandParser.removefirstFrom(args), stdin);
	}
	
	/**
	 * Execute Chain of Commands with pipes
	 */
	private static String executePipe(TerminalState state, String input) {
		Pipe4Command pipe = Pipe4Command.parsePipe(input);
		List<String[]> commandList = pipe.getCommands();

		if (commandList.isEmpty()) { return ""; }
		
		String currentOutput = CommandParser.executeCommand(state, commandList.get(0), state.getCurrentDirectory().getPath());

		for (int i = 1; i < commandList.size(); i++) {
			String[] args = commandList.get(i);
			String commandName = args[0];
			ICommand command = CommandFactory.getCommand(commandName);
			if (command == null) { return "UNKNOWN COMMAND IN PIPE: " + commandName; }
			currentOutput = command.execute(state, CommandParser.removefirstFrom(args), currentOutput);
		}

		return currentOutput;
	}
	
	/**
	 * Remove first elt of agrs ('command'). 
	 */
	static String[] removefirstFrom(String[] args) 
		{ return Arrays.asList(args).subList(1, args.length).toArray(new String[args.length - 1]); }
	
}
