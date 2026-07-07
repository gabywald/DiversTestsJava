package gabywald.terminal3.serverside.shell;

import java.util.ArrayList;
import java.util.List;

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
    
}
