package reframe.dao;

import java.sql.*;
import reframe.model.Utente;

public class UtenteDAO
{
	// Metodo per confronto di email
	public boolean VerificaEmail(String email)
	{
		String query = "SELECT * FROM Utente WHERE Email = ?";
		
		try(Connection conn = ConnessioneDB.getConnection(); PreparedStatement ps = conn.prepareStatement(query);)
		{
			ps.setString(1, email);
			
			try( ResultSet rs = ps.executeQuery() )
			{
				if(rs.next()) return true;
			}
			
		}	catch (SQLException e) { /* Errore in console */	e.printStackTrace(); }
		
		return false;
	}
	
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
	
	/* Metodo per l'inserimento di dati nel DB nella fase di registrazione
		TRUE = inserimento avvenuto con successo
		FALSE = errore
	*/
	public boolean doSave(Utente nuovoUtente)
	{
		String query = "INSERT INTO Utente (Username, Email, Password, Nome, Cognome, Token) VALUES (?, ?, ?, ?, ?, ?)";
		
		try( Connection conn = ConnessioneDB.getConnection(); PreparedStatement ps = conn.prepareStatement(query); )
		{
			if(!VerificaEmail(nuovoUtente.getEmail()))
			{
			ps.setString(1, nuovoUtente.getUsername());
	        ps.setString(2, nuovoUtente.getEmail());
	        ps.setString(3, nuovoUtente.getPassword());
	        ps.setString(4, nuovoUtente.getNome());
	        ps.setString(5, nuovoUtente.getCognome());
	        ps.setString(6, nuovoUtente.getToken());
	        
	        int row = ps.executeUpdate();
	        return row > 0;
			}
			
		}	catch(SQLException e) { /* Errore nella console */	e.printStackTrace();
			return false;
		}
		return false;
	}
}