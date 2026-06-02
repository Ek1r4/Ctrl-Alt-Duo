package reframe.model.dao;

import reframe.model.beans.*;
import reframe.utils.*;
import java.sql.*;
import java.util.*;

public class UtenteDAO
{
	// Metodo per confronto di email
	public boolean VerificaEmail(String email) throws SQLException
	{
		String query = "SELECT * FROM Utente WHERE Email = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			conn = ConnessioneDB.getConnection(); 
			ps = conn.prepareStatement(query);
			
			ps.setString(1, email);
			
			try( ResultSet rs = ps.executeQuery() )
			{
				if(rs.next()) return true;
			}
			
		}	catch (SQLException e) { /* Errore in console */	e.printStackTrace(); }
		
		finally { // Serve per rimettere nel ConnectionPool la connessione
	        
	        try 
	        {
	            if (ps != null) ps.close(); // Chiusura del PreparedStatement
	            
	        } catch (SQLException e) {	e.printStackTrace();	}
	        
	        
	        if (conn != null) {	ConnessioneDB.releaseConnection(conn); } // Controllo se esiste una connessione, se si viene rimessa nel ConnectionPool
	    }
		
		return false;
	}
	
	// Metodo per verificare le credenziali dell'utente nella fase di login
	public Utente doRetrieveByEmailAndPassword(String email, String password) throws SQLException
	{
		Utente utenteTrovato = null;
		String query = "SELECT * FROM Utente WHERE Email = ? AND Password = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
				
		try
		{
			conn = ConnessioneDB.getConnection(); 
			ps = conn.prepareStatement(query);
			
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

				    System.out.println("DEBUG - Login effettuato per: " + utenteTrovato); // DEBUG 
				}
				else
				{
				    System.out.println("DEBUG - Credenziali errate o utente non trovato."); // DEBUG 
				}
			}
				
		}	catch (SQLException e) { /* Errore in console */	e.printStackTrace(); }
		
		finally { // Serve per rimettere nel ConnectionPool la connessione
	        
	        try 
	        {
	            if (ps != null) ps.close(); // Chiusura del PreparedStatement
	            
	        } catch (SQLException e) {	e.printStackTrace();	}
	        
	        
	        if (conn != null) {	ConnessioneDB.releaseConnection(conn); } // Controllo se esiste una connessione, se si viene rimessa nel ConnectionPool
	    }

        return utenteTrovato;		
	}
	
	/* Metodo per l'inserimento di dati nel DB nella fase di registrazione
		TRUE = inserimento avvenuto con successo
		FALSE = errore
	*/
	public boolean doSave(Utente nuovoUtente) throws SQLException
	{
		String query = "INSERT INTO Utente (Username, Email, Password, Nome, Cognome, Bio, Token) VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			conn = ConnessioneDB.getConnection(); 
			ps = conn.prepareStatement(query); 
			
			if(!VerificaEmail(nuovoUtente.getEmail()))
			{
			ps.setString(1, nuovoUtente.getUsername());
	        ps.setString(2, nuovoUtente.getEmail());
	        ps.setString(3, nuovoUtente.getPassword());
	        ps.setString(4, nuovoUtente.getNome());
	        ps.setString(5, nuovoUtente.getCognome());
	        ps.setString(6, nuovoUtente.getBio());
	        ps.setString(7, nuovoUtente.getToken());
	        
	        int row = ps.executeUpdate();
	        return row > 0;
			}
			
		}	catch(SQLException e) { /* Errore nella console */	e.printStackTrace();
			return false;
		}
		
		finally { // Serve per rimettere nel ConnectionPool la connessione
	        
	        try 
	        {
	            if (ps != null) ps.close(); // Chiusura del PreparedStatement
	            
	        } catch (SQLException e) {	e.printStackTrace();	}
	        
	        
	        if (conn != null) {	ConnessioneDB.releaseConnection(conn); } // Controllo se esiste una connessione, se si viene rimessa nel ConnectionPool
	    }
		
		return false;
	}
	
	// Metodo per recuperare tutte le info di un utente specifico
	
	public Utente doRetrieveByKey(String Username) throws SQLException
	{
		String query = "SELECT * FROM Utente WHERE Username = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		Utente utenteTrovato = null;
		
		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setString(1, Username);
			
			ResultSet rs = ps.executeQuery();
			
			if(rs.next())
			{
				utenteTrovato = new Utente();
			    
			    utenteTrovato.setUsername(rs.getString("Username"));
			    utenteTrovato.setEmail(rs.getString("Email"));
			    utenteTrovato.setNome(rs.getString("Nome"));
			    utenteTrovato.setCognome(rs.getString("Cognome"));
			    utenteTrovato.setPassword(rs.getString("Password")); 
			    utenteTrovato.setToken(rs.getString("Token"));
			    utenteTrovato.setBio(rs.getString("Bio"));
			    
			    System.out.println("DEBUG - Login effettuato per: " + utenteTrovato); // DEBUG 
			}
			else
			{
			    System.out.println("DEBUG - Credenziali errate o utente non trovato."); // DEBUG 
			}
			
			
		} catch (SQLException e) { /* Errore in console */	e.printStackTrace(); }
		
		finally { // Serve per rimettere nel ConnectionPool la connessione
	        
	        try 
	        {
	            if (ps != null) ps.close(); // Chiusura del PreparedStatement
	            
	        } catch (SQLException e) {	e.printStackTrace();	}
	        
	        
	        if (conn != null) {	ConnessioneDB.releaseConnection(conn); } // Controllo se esiste una connessione, se si viene rimessa nel ConnectionPool
	    }
		
		return utenteTrovato;
		
	}

	// Metodo per recuperare tutti gli utenti secondo un certo ordine
	
	public List<Utente> doRetrieveAll(String order) throws SQLException
	{
		String query = "SELECT * FROM UTENTE";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		List<Utente> lista = new ArrayList<>();
		
		// Controllo sull'ordine passato
		if(order != null && !order.trim().isEmpty()) // Uso trim() per eliminare un possibile SQLException da parte del DB a causa degli spazi
		{
			query += " ORDER BY " + order;
		}
		else { System.out.println("DEBUG - Nessun ordine specificato, eseguo la query base: " + query);	/* DEBUG */}

		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				Utente u = new Utente();
				
				u.setUsername(rs.getString("Username"));
				u.setNome(rs.getString("Nome"));
				u.setCognome(rs.getString("Cognome"));
				u.setEmail(rs.getString("Email"));
				u.setPassword(rs.getString("Password"));
				u.setTelefono(rs.getString("Telefono"));
				u.setToken(rs.getString("Token"));
				u.setBio(rs.getString("Bio"));
				
				lista.add(u);	
			}
			
		} catch (SQLException e) { /* Errore in console */	e.printStackTrace(); }
		
		finally { // Serve per rimettere nel ConnectionPool la connessione
	        
	        try 
	        {
	            if (ps != null) ps.close(); // Chiusura del PreparedStatement
	            
	        } catch (SQLException e) {	e.printStackTrace();	}
	        
	        
	        if (conn != null) {	ConnessioneDB.releaseConnection(conn); } // Controllo se esiste una connessione, se si viene rimessa nel ConnectionPool
	    }
		
		return lista;
		
	}
	
	/* Metodo per sovrascrivere un campo di un utente specifico 
	 	TRUE = modifica avvenuta con successo
		FALSE = errore
	*/
	public boolean doUpdate(Utente utente) throws SQLException
	{
		String query = "UPDATE Utente SET Email = ?, Password = ?, Nome = ?, Cognome = ?, Bio = ?, Token = ? WHERE Username = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		Utente utenteModificato = null;
		
		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
	        ps.setString(1, utenteModificato.getEmail());
	        ps.setString(2, utenteModificato.getPassword());
	        ps.setString(3, utenteModificato.getNome());
	        ps.setString(4, utenteModificato.getCognome());
	        ps.setString(5, utenteModificato.getBio());
	        ps.setString(6, utenteModificato.getToken());
	        ps.setString(7, utenteModificato.getUsername());
	        
	        int row = ps.executeUpdate();
	        return row > 0;
			
		} catch (SQLException e) { /* Errore in console */	e.printStackTrace(); 
			return false;	}
		
		finally { // Serve per rimettere nel ConnectionPool la connessione
	        
	        try 
	        {
	            if (ps != null) ps.close(); // Chiusura del PreparedStatement
	            
	        } catch (SQLException e) {	e.printStackTrace();	}
	        
	        
	        if (conn != null) {	ConnessioneDB.releaseConnection(conn); } // Controllo se esiste una connessione, se si viene rimessa nel ConnectionPool
	    }
	}
	
	// Metodi ancora da fare

	public boolean updatePassword(String email, String nuovaPassword) throws SQLException;
	
	public boolean doDelete(String email) throws SQLException;
}