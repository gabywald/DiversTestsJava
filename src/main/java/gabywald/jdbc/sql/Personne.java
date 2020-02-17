package gabywald.jdbc.sql;

public class Personne {

	private Integer id;

	private String nom;

	private String prenom;


	public Personne() {
	}

	public Personne(Integer id, String nom, String prenom, String l1, String l2, String cp, String ville) {
		this.setId(id);
		this.setNom(nom);
		this.setPrenom(prenom);
	}

	public String getNomPrenom() {
		return this.getPrenom().toLowerCase() + "."
				+ this.getNom().toLowerCase();
	}

	public String getIdentite() {
		return this.getPrenom() + " " + this.getNom();
	}

	public String getNom() {
		return nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
