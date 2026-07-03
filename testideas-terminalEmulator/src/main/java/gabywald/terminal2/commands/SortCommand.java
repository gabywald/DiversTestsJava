package gabywald.terminal2.commands;

import gabywald.terminal2.FileSystem;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Commande 'sort' : trie les lignes d'un fichier ou de stdin.
 * Usage: sort [fichier] ou sort < stdin
 * @author Gabriel Chandesris (2026)
 */
public class SortCommand implements Command {
    private FileSystem fileSystem;

    public SortCommand(FileSystem fileSystem) { this.fileSystem = fileSystem; }

    @Override
    public String execute(String[] args, String stdin) {
        String content;

        if (args.length > 0) {
            String fileName = args[0];
            if (!fileSystem.exists(fileName)) { return "Fichier introuvable: " + fileName; }
            content = fileSystem.cat(fileName);
        } 
        else if (stdin != null) { content = stdin; } 
        else { return "Usage: sort [file] ou utiliser un pipe"; }

        List<String> lines = Arrays.asList(content.split("\n"));
        Collections.sort(lines);
        return String.join("\n", lines);
    }
}
