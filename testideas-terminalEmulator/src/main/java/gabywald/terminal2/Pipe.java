package gabywald.terminal2;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour gérer les pipes entre commandes.
 * @author Gabriel Chandesris (2026)
 */
public class Pipe {
    private List<String[]> commands;

    /**
     * Constructeur.
     * @param commands Liste des commandes séparées par des pipes.
     */
    public Pipe(List<String[]> commands) { this.commands = commands; }

    /**
     * Retourne la liste des commandes.
     */
    public List<String[]> getCommands() { return this.commands; }

    /**
     * Analyse une chaîne de commandes avec pipes.
     * @param input Chaîne complète (ex: "ls | grep txt").
     * @return Objet Pipe contenant les commandes séparées.
     */
    public static Pipe parsePipe(String input) {
        String[] parts = input.split("\\|");
        List<String[]> commandList = new ArrayList<>();
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (!trimmedPart.isEmpty()) 
            	{ commandList.add(trimmedPart.split("\\s+")); }
        }
        return new Pipe(commandList);
    }
}
