package gabywald.terminal3.clientside.editors;

/**
 * Interface for Text Editors (adapted). .
 * @author Gabriel Chandesris (2026)
 */
public interface ITextEditor {
	/** Start Edition. */
	void start();

	/**
	 * 
	 * @param input Entrée de l'utilisateur.
	 * @return Edition Result (null to continue, "SAVE:..." to record, "CANCEL" for canceling).
	 */
	String handleInput(String input);

	String getName();
}
