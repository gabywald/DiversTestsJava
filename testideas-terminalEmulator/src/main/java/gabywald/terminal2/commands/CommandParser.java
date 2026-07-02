package gabywald.terminal2.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gabywald.terminal2.FileSystem;
import gabywald.terminal2.Pipe;
import gabywald.terminal2.Redirection;

/**
 * Classe responsable de l'analyse et de l'exécution des commandes.
 * Supporte les redirections (>, >>, <) et les pipes (|).
 */
public class CommandParser {
    private FileSystem fileSystem;
    private Map<String, Command> commands;

    /**
     * Constructeur : initialise les commandes disponibles.
     */
    public CommandParser(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.commands = new HashMap<>();
        initializeCommands();
    }

    /**
     * Initialise les commandes disponibles.
     */
    private void initializeCommands() {
        commands.put("ls", new LsCommand(fileSystem));
        commands.put("cd", new CdCommand(fileSystem));
        commands.put("cat", new CatCommand(fileSystem));
        commands.put("echo", new EchoCommand(fileSystem));
        commands.put("mkdir", new MkdirCommand(fileSystem));
        commands.put("touch", new TouchCommand(fileSystem));
        commands.put("rm", new RmCommand(fileSystem));
        commands.put("pwd", new PwdCommand(fileSystem));
        commands.put("help", new HelpCommand());
        commands.put("clear", new ClearCommand());
        commands.put("edit", new EditCommand(fileSystem, this));
        commands.put("run", new RunCommand(fileSystem, this));
        commands.put("grep", new GrepCommand(fileSystem));
        commands.put("wc", new WcCommand(fileSystem));
        commands.put("tr", new TrCommand(fileSystem));
        commands.put("sort", new SortCommand(fileSystem));
    }

    /**
     * Exécute une commande avec gestion des redirections et pipes.
     * @param input Commande complète (ex: "ls | grep txt").
     * @param currentDirectory Répertoire courant.
     * @return Résultat de l'exécution.
     */
    public String execute(String input, String currentDirectory) {
        input = input.trim();
        if (input.isEmpty()) {
            return "";
        }

        if (input.contains("|")) {
            return executePipe(input);
        }

        String[] args = input.split("\\s+");
        List<Redirection> redirections = Redirection.parseRedirections(args);

        if (redirections.isEmpty()) {
            return executeCommand(args, currentDirectory, null);
        } else {
            return executeWithRedirections(redirections, currentDirectory);
        }
    }

    /**
     * Exécute une commande avec des redirections.
     */
    private String executeWithRedirections(List<Redirection> redirections, String currentDirectory) {
        Redirection firstRedirection = redirections.get(0);
        String[] args = firstRedirection.cleanedArgs;

        if (args.length == 0) {
            return "Commande invalide.";
        }

        String commandName = args[0];
        Command command = commands.get(commandName);
        if (command == null) {
            return "Commande introuvable: " + commandName;
        }

        String stdin = null;
        for (Redirection r : redirections) {
            if (r.getType() == Redirection.Type.INPUT) {
                if (fileSystem.exists(r.getFileName())) {
                    stdin = fileSystem.cat(r.getFileName());
                } else {
                    return "Fichier introuvable pour l'entrée: " + r.getFileName();
                }
            }
        }

        String output = command.execute(args, stdin);

        for (Redirection r : redirections) {
            if (r.getType() == Redirection.Type.OUTPUT || r.getType() == Redirection.Type.APPEND) {
                if (r.getType() == Redirection.Type.OUTPUT) {
                    fileSystem.echo(r.getFileName(), output);
                } else {
                    String existingContent = fileSystem.exists(r.getFileName()) ?
                        fileSystem.cat(r.getFileName()) : "";
                    fileSystem.echo(r.getFileName(), existingContent + output);
                }
                return "";
            }
        }

        return output;
    }

    /**
     * Exécute une commande sans redirections.
     */
    private String executeCommand(String[] args, String currentDirectory, String stdin) {
        if (args.length == 0) {
            return "";
        }

        String commandName = args[0];
        Command command = commands.get(commandName);
        if (command == null) {
            return "Commande introuvable: " + commandName;
        }

        return command.execute(args, stdin);
    }

    /**
     * Exécute une chaîne de commandes avec pipes.
     */
    private String executePipe(String input) {
        Pipe pipe = Pipe.parsePipe(input);
        List<String[]> commandList = pipe.getCommands();

        if (commandList.isEmpty()) {
            return "";
        }

        String currentOutput = executeCommand(commandList.get(0), fileSystem.getCurrentDirectory(), null);

        for (int i = 1; i < commandList.size(); i++) {
            String[] args = commandList.get(i);
            String commandName = args[0];
            Command command = commands.get(commandName);
            if (command == null) {
                return "Commande introuvable dans le pipe: " + commandName;
            }
            currentOutput = command.execute(args, currentOutput);
        }

        return currentOutput;
    }
}
