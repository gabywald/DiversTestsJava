package gabywald.terminal2.editors;

/**
 * Interface pour les éditeurs de texte adaptés à Swing.
 */
public interface TextEditorSwing {
    /**
     * Démarre l'édition.
     */
    void start();

    /**
     * Gère une entrée utilisateur.
     * @param input Entrée de l'utilisateur.
     * @return Résultat de l'édition (null pour continuer, "SAVE:..." pour sauvegarder, "CANCEL" pour annuler).
     */
    String handleInput(String input);

    /**
     * Retourne le nom de l'éditeur.
     */
    String getName();
}
