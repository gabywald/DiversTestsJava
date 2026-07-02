package gabywald.terminal2.commands;

/**
 * Interface pour les commandes du terminal.
 * Supporte les entrées standard (stdin) pour les pipes et redirections.
 */
public interface Command {
    /**
     * Exécute la commande avec les arguments fournis.
     * @param args Arguments de la commande.
     * @return Résultat de l'exécution.
     */
    default String execute(String[] args) {
        return execute(args, null);
    }

    /**
     * Exécute la commande avec une entrée standard (pour les pipes et redirections <).
     * @param args Arguments de la commande.
     * @param stdin Entrée standard (peut être null).
     * @return Résultat de l'exécution.
     */
    String execute(String[] args, String stdin);
}
