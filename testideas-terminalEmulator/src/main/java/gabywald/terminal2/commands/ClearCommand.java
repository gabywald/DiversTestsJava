package gabywald.terminal2.commands;

/**
 * Commande 'clear' : efface l'écran (simulé par des sauts de ligne).
 */
public class ClearCommand implements Command {
    @Override
    public String execute(String[] args, String stdin) {
        return "\n\n\n\n\n\n\n\n\n\n";
    }
}
