package gabywald.terminal2.commands;

/**
 * Commande 'help' : affiche la liste des commandes disponibles.
 */
public class HelpCommand implements Command {
    @Override
    public String execute(String[] args, String stdin) {
        return "Commandes disponibles:\n" +
               "  ls              - Lister les fichiers/répertoires\n" +
               "  cd <dir>        - Changer de répertoire\n" +
               "  cat <file>      - Afficher le contenu d'un fichier\n" +
               "  echo <text>     - Afficher du texte\n" +
               "  echo <text> > <file> - Écrire dans un fichier (écrasement)\n" +
               "  echo <text> >> <file> - Ajouter à un fichier\n" +
               "  mkdir <dir>     - Créer un répertoire\n" +
               "  touch <file>    - Créer un fichier\n" +
               "  rm <file|dir>   - Supprimer un fichier/répertoire\n" +
               "  pwd             - Afficher le répertoire courant\n" +
               "  edit <file>     - Éditer un fichier (nano par défaut)\n" +
               "  edit --nano <file> - Éditer avec nano\n" +
               "  edit --vim <file>  - Éditer avec vim\n" +
               "  run <script>     - Exécuter un script\n" +
               "  grep <pattern>   - Filtrer les lignes contenant un motif\n" +
               "  wc              - Compter les lignes, mots, caractères\n" +
               "  tr <set1> <set2> - Remplacer des caractères\n" +
               "  sort            - Trier les lignes\n" +
               "  < cmd           - Rediriger l'entrée depuis un fichier\n" +
               "  cmd > file      - Rediriger la sortie vers un fichier\n" +
               "  cmd >> file     - Ajouter la sortie à un fichier\n" +
               "  cmd1 | cmd2     - Pipe: sortie de cmd1 → entrée de cmd2\n" +
               "  help            - Afficher cette aide\n" +
               "  clear           - Effacer l'écran";
    }
}
