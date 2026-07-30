package gabywald.terminal2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe pour gérer les redirections d'entrée/sortie (>, >>, <).
 * @author Gabriel Chandesris (2026)
 */
public class Redirection {
    /**
     * Type de redirection.
     */
    public enum Type {
        NONE,       // Pas de redirection
        OUTPUT,     // > (écrasement)
        APPEND,     // >> (ajout)
        INPUT       // < (entrée)
    }

    private Type type;
    private String fileName;
    public String[] cleanedArgs; // Arguments nettoyés (sans les redirections)

    /**
     * Constructeur.
     * @param type Type de redirection.
     * @param fileName Nom du fichier.
     */
    public Redirection(Type type, String fileName) {
        this.type = type;
        this.fileName = fileName;
    }

    /**
     * Retourne le type de redirection.
     */
    public Type getType() { return this.type; }

    /**
     * Retourne le nom du fichier.
     */
    public String getFileName() { return this.fileName; }

    /**
     * Analyse une commande pour extraire les redirections.
     * @param args Arguments de la commande.
     * @return Liste des redirections trouvées.
     */
    public static List<Redirection> parseRedirections(String[] args) {
        List<Redirection> redirections = new ArrayList<>();
        List<String> cleanedArgs = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals(">") && i + 1 < args.length) {
                redirections.add(new Redirection(Type.OUTPUT, args[i + 1]));
                i++;
            } else if (arg.equals(">>") && i + 1 < args.length) {
                redirections.add(new Redirection(Type.APPEND, args[i + 1]));
                i++;
            } else if (arg.equals("<") && i + 1 < args.length) {
                redirections.add(new Redirection(Type.INPUT, args[i + 1]));
                i++;
            } else { cleanedArgs.add(arg); }
        }

        if (!cleanedArgs.isEmpty() && !redirections.isEmpty()) {
            redirections.get(0).cleanedArgs = cleanedArgs.toArray(new String[0]);
        }

        return redirections;
    }
    
    @Override
    public String toString() {
    	StringBuilder sbToReturn = new StringBuilder();
    	
    	sbToReturn.append(this.fileName).append(this.type.name()).append("\n");
    	Arrays.asList(this.cleanedArgs).stream().forEach(elt -> sbToReturn.append("\t").append(elt).append("\n"));
    	
    	return sbToReturn.toString();
    }
    
}
