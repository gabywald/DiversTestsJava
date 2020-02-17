package gabywald.jdbc.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class MySQLConnector {

	public static void main(String[] args) {
		System.out.println( System.getProperty("os.name") );
		System.out.println( File.separator );
		System.out.println( File.pathSeparator );
		
		System.out.println("Loading driver...");

		try {
		    Class.forName("com.mysql.cj.jdbc.Driver"); // ("com.mysql.jdbc.Driver");
		    System.out.println("Driver loaded!");
		} catch (ClassNotFoundException e) {
		    throw new IllegalStateException("Cannot find the driver in the classpath!", e);
		}
		
		String url		= "jdbc:mysql://localhost/formation"; // "jdbc:mysql://localhost:3306/formation"; // javabase";
		String username	= "root"; // "java";
		String password	= "mysqlpassword"; // "password";

		System.out.println("Connecting database...");
		
		try {
//			Connection connectionSample = DriverManager.getConnection(url);
//			System.out.println("Database connected! {" + connectionSample + "}");
			
			Properties info = new Properties();
			info.put("user", username);
			info.put("password", password);
			info.put("serverTimezone", "Europe/Paris") ; // "UTC+2");
			Connection connectionMore = DriverManager.getConnection(url, info);
			System.out.println("Database connected! {" + connectionMore + "}");
			
			Statement stmt	= connectionMore.createStatement();
			String sql		= "select puid, nom, prenom from t_personne";
			ResultSet rs	= stmt.executeQuery(sql);

			while(rs.next()) {
				int puid		= rs.getInt("puid");
				String nom 		= rs.getString("prenom");
				String prenom	= rs.getString("nom");
				
				System.out.println("\t puid--: {" + puid + "}");
				System.out.println("\t nom---: {" + nom + "}");
				System.out.println("\t prenom: {" + prenom + "}");
			}
			rs.close();
			
			
//			Connection connection = DriverManager.getConnection(url, username, password);
//			System.out.println("Database connected! {" + connection + "}");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// throw new IllegalStateException("Cannot connect the database!", e);
		}
//		finally{
//			// finally block used to close resources
//			try{
//				if (stmt != null)
//					{ conn.close(); }
//			} catch(SQLException se) {
//			} // do nothing
//			try {
//				if (conn != null)
//					{ conn.close(); }
//			} catch(SQLException se ){
//				se.printStackTrace();
//			} // end finally try
//		}//end try

//		try (Connection connection = DriverManager.getConnection(url, username, password)) {
//		    System.out.println("Database connected!");
//		} catch (SQLException e) {
//		    throw new IllegalStateException("Cannot connect the database!", e);
//		}
	}
	
//	public static Collection<Personne> getAllInstances() {
//		JdbcTemplate template = getJdbcTemplate();
//		Collection<Personne> personnes = template.query(
//				"select puid, nom, prenom from t_personne",
//				new MappeurPersonne());
//		return personnes;
//	}
	
}
