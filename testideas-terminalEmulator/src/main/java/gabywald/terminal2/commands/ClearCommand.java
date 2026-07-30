package gabywald.terminal2.commands;

/**
 * Commande 'clear' : efface l'écran (simulé par des sauts de ligne).
 * @author Gabriel Chandesris (2026)
 */
public class ClearCommand implements Command {
    @Override
    public String execute(String[] args, String stdin) 
    	{ return "\n\n\n\n\n\n\n\n\n\n"; }
}
