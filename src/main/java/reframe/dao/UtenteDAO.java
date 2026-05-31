package reframe.dao;

import java.sql.*;
import reframe.model.Utente;

public class UtenteDAO
{	
	// Metodo per verificare le credenziali dell'utente nella fase di login
	public Utente VerificaCredenziali(String email, String password)
	{
		Utente utenteTrovato = null;
		String query = "SELECT * FROM Utente WHERE Email = ? AND Password = ?";
		
		try(Connection conn = ConnessioneDB.getConnection(); PreparedStatement ps = conn.prepareStatement(query);)
		{
			ps.setString(1,email);
			ps.setString(1,email);
			
			try( ResultSet rs = ps.executeQuery() )
			{
				if(rs.next())
				{
					utenteTrovato = new Utente();
				    
				    utenteTrovato.setUsername(rs.getString("Username"));
				    utenteTrovato.setEmail(rs.getString("Email"));
				    utenteTrovato.setNome(rs.getString("Nome"));
				    utenteTrovato.setCognome(rs.getString("Cognome"));
				    utenteTrovato.setPassword(rs.getString("Password")); 
				    utenteTrovato.setToken(rs.getString("Token"));
				    
				    // DEBUG 
				    
				    System.out.println("DEBUG - Login effettuato per: " + utenteTrovato);
				}
				else
				{
				    System.out.println("DEBUG - Credenziali errate o utente non trovato.");
				}
			}
				
		}	catch (SQLException e) { /* Errore in console */	e.printStackTrace(); }

        return utenteTrovato;
			
	}
}