package gabywald.jdbc.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

// import org.springframework.jdbc.core.RowMapper;

public class MappeurPersonne { // implements RowMapper<Personne> {

	public Personne mapRow(ResultSet rs, int rowNum) throws SQLException {
		Personne personne = new Personne();
		personne.setId(rs.getInt("puid"));
		personne.setPrenom(rs.getString("prenom"));
		personne.setNom(rs.getString("nom"));
		return personne;
	}

}
