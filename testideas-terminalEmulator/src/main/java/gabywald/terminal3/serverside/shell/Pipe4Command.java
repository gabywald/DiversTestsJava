package gabywald.terminal3.serverside.shell;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to make pipes available betwwen commands.
 * @author Gabriel Chandesris (2026)
 */
public class Pipe4Command {
    private List<String[]> commands;

    /**
     * Constructor. .
     * @param commands List of Commands separated by pipes. 
     */
    public Pipe4Command(List<String[]> commands) { this.commands = commands; }

    public List<String[]> getCommands() { return this.commands; }

    /**
     * Analyze Chain of Commands. 
     * @param input Complete Chain. (ex: "ls | grep txt").
     * @return Pipe Instance with separated Commands. 
     */
    public static Pipe4Command parsePipe(String input) {
        String[] parts = input.split("\\|");
        List<String[]> commandList = new ArrayList<>();
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (!trimmedPart.isEmpty()) 
            	{ commandList.add(trimmedPart.split("\\s+")); }
        }
        return new Pipe4Command(commandList);
    }
}
