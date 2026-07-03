package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;
import java.util.List;

/**
 * Command 'ls' : Current Directory Listing. 
 * @author Gabriel Chandesris (2026)
 */
public class LsCommand implements Command {
    private FileSystem fileSystem;

    public LsCommand(FileSystem fileSystem) { this.fileSystem = fileSystem; }

    @Override
    public String execute(String[] args, String stdin) {
        List<String> contents = this.fileSystem.ls();
        if (contents.isEmpty()) { return ""; }
        return String.join(" ", contents);
    }
}