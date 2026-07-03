package gabywald.terminal2.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gabywald.terminal2.FileSystem;
import gabywald.terminal2.Pipe;
import gabywald.terminal2.Redirection;

/**
 * Classe responsable de l'analyse et de l'exécution des commandes.
 * Supporte les redirections (>, >>, <) et les pipes (|).
 * @author Gabriel Chandesris (2026)
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
        this.initializeCommands();
    }

    /**
     * Initialise les commandes disponibles.
     */
    private void initializeCommands() {
    	this.commands.put("ls", new LsCommand(fileSystem));
    	this.commands.put("cd", new CdCommand(fileSystem));
    	this.commands.put("cat", new CatCommand(fileSystem));
    	this.commands.put("echo", new EchoCommand(fileSystem));
    	this.commands.put("mkdir", new MkdirCommand(fileSystem));
    	this.commands.put("touch", new TouchCommand(fileSystem));
    	this.commands.put("rm", new RmCommand(fileSystem));
    	this.commands.put("pwd", new PwdCommand(fileSystem));
    	this.commands.put("help", new HelpCommand());
    	this.commands.put("clear", new ClearCommand());
    	this.commands.put("edit", new EditCommand(fileSystem, this));
    	this.commands.put("run", new RunCommand(fileSystem, this));
    	this.commands.put("grep", new GrepCommand(fileSystem));
    	this.commands.put("wc", new WcCommand(fileSystem));
    	this.commands.put("tr", new TrCommand(fileSystem));
    	this.commands.put("sort", new SortCommand(fileSystem));
    }

    /**
     * Exécute une commande avec gestion des redirections et pipes.
     * @param input Commande complète (ex: "ls | grep txt").
     * @param currentDirectory Répertoire courant.
     * @return Résultat de l'exécution.
     */
    public String execute(String input, String currentDirectory) {
        input = input.trim();
        if (input.isEmpty()) { return ""; }

        if (input.contains("|")) { return this.executePipe(input); }

        String[] args = input.split("\\s+");
        List<Redirection> redirections = Redirection.parseRedirections(args);

        if (redirections.isEmpty()) 
        	{ return this.executeCommand(args, currentDirectory, null); } 
        else 
        	{ return this.executeWithRedirections(redirections, currentDirectory); }
    }

    /**
     * Execute command with Redirections
     */
    private String executeWithRedirections(List<Redirection> redirections, String currentDirectory) {
        Redirection firstRedirection = redirections.get(0);
        String[] args = firstRedirection.cleanedArgs;

        if (args.length == 0) { return "Commande invalide."; }

        String commandName = args[0];
        Command command = this.commands.get(commandName);
        if (command == null) { return "Commande introuvable: " + commandName; }

        String stdin = null;
        for (Redirection r : redirections) {
            if (r.getType() == Redirection.Type.INPUT) {
                if (this.fileSystem.exists(r.getFileName())) 
                	{ stdin = this.fileSystem.cat(r.getFileName()); } 
                else 
                	{ return "Fichier introuvable pour l'entrée: " + r.getFileName(); }
            }
        }

        String output = command.execute(CommandParser.removefirstFrom(args), stdin);

        for (Redirection r : redirections) {
            if (r.getType() == Redirection.Type.OUTPUT || r.getType() == Redirection.Type.APPEND) {
                if (r.getType() == Redirection.Type.OUTPUT) 
                	{ this.fileSystem.echo(r.getFileName(), output); }
                else { // Redirection.Type.APPEND
                    String existingContent = this.fileSystem.exists(r.getFileName()) ? 
                    		this.fileSystem.cat(r.getFileName()) : "";
                    this.fileSystem.echo(r.getFileName(), existingContent + output);
                }
                return "";
            }
        }

        return output;
    }

    /**
     * Execute Command without Redirection
     */
    private String executeCommand(String[] args, String currentDirectory, String stdin) {
        if (args.length == 0) { return ""; }

        String commandName = args[0];
        Command command = this.commands.get(commandName);
        if (command == null) { return "Commande introuvable: " + commandName; }
        
        return command.execute(CommandParser.removefirstFrom(args), stdin);
    }
    
    /**
     * Remove first elt of agrs ('command'). 
     */
    static String[] removefirstFrom(String[] args) 
    	{ return Arrays.asList(args).subList(1, args.length).toArray(new String[args.length - 1]); }

    /**
     * Execute chain of Commands with pipes
     */
    private String executePipe(String input) {
        Pipe pipe = Pipe.parsePipe(input);
        List<String[]> commandList = pipe.getCommands();

        if (commandList.isEmpty()) { return ""; }

        String currentOutput = this.executeCommand(commandList.get(0), this.fileSystem.getCurrentDirectory(), null);

        for (int i = 1; i < commandList.size(); i++) {
            String[] args = commandList.get(i);
            String commandName = args[0];
            Command command = this.commands.get(commandName);
            if (command == null) { return "Commande introuvable dans le pipe: " + commandName; }
            currentOutput = command.execute(CommandParser.removefirstFrom(args), currentOutput);
        }

        return currentOutput;
    }
}
