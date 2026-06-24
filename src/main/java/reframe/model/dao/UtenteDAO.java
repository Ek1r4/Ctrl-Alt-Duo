package reframe.model.dao;

import reframe.model.beans.Utente;
import reframe.utils.*;
import java.sql.*;
import java.util.*;

public class UtenteDAO
{
	// UTILITY: Controlla se un'email è già registrata nel database (Ritorna TRUE se esiste)
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
			
		} catch (SQLException e) { /* Errore in console */ e.printStackTrace(); }
		
		finally { // Serve per rimettere nel ConnectionPool la connessione
			try 
			{
				if (ps != null) ps.close(); // Chiusura del PreparedStatement
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } // Controllo se esiste una connessione, se si viene rimessa nel ConnectionPool
		}
		
		return false;
	}
	
	// UTILITY: Mappa i dati della riga corrente del ResultSet in un oggetto Utente
	private Utente estraiUtente(ResultSet rs) throws SQLException 
	{
		Utente utente = new Utente();
		
		utente.setUsername(rs.getString("Username"));
		utente.setEmail(rs.getString("Email"));
		utente.setPassword(rs.getString("Password"));
		utente.setNome(rs.getString("Nome"));
		utente.setCognome(rs.getString("Cognome"));
		utente.setBio(rs.getString("Bio"));
		utente.setTelefono(rs.getString("Telefono"));
		utente.setIsAdmin(rs.getInt("isAdmin"));
		
		return utente;
	}
	
	// CREATE: Salva un nuovo utente nel database durante la registrazione
	// (Ritorna TRUE se l'inserimento avviene con successo, FALSE in caso di errore)
	public boolean doSave(Utente nuovoUtente) throws SQLException
	{
		String query = "INSERT INTO Utente (Username, Email, Password, Nome, Cognome, Bio, Telefono) VALUES (?, ?, ?, ?, ?, ?, ?)";
		
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
				ps.setString(7, nuovoUtente.getTelefono());
				
				int row = ps.executeUpdate();
				return row > 0;
			}
			
		} catch(SQLException e) { /* Errore nella console */ e.printStackTrace();
			return false;
		}
		
		finally { 
			try 
			{
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
		
		return false;
	}
	
	// READ (Singolo): Recupera tutte le info di un utente specifico tramite il suo Username
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
				utenteTrovato = estraiUtente(rs);
				System.out.println("DEBUG - Login effettuato per: " + utenteTrovato); // DEBUG 
			}
			else
			{
				System.out.println("DEBUG - Credenziali errate o utente non trovato."); // DEBUG 
			}
			
		} catch (SQLException e) { /* Errore in console */ e.printStackTrace(); }
		
		finally { 
			try 
			{
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
		
		return utenteTrovato;
	}

	// READ (Lista): Recupera TUTTI gli utenti registrati, con possibilità di ordinamento
	public List<Utente> doRetrieveAll(String order) throws SQLException
	{
		String query = "SELECT * FROM Utente";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		List<Utente> lista = new ArrayList<>();
		
		// Controllo sull'ordine passato
		if(order != null && !order.trim().isEmpty()) // Uso trim() per eliminare un possibile SQLException da parte del DB a causa degli spazi
		{
			query += " ORDER BY " + order;
		}
		else { System.out.println("DEBUG - Nessun ordine specificato, eseguo la query base: " + query); /* DEBUG */}

		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				Utente u = estraiUtente(rs);
				lista.add(u);	
			}
			
		} catch (SQLException e) { /* Errore in console */ e.printStackTrace(); }
		
		finally { 
			try 
			{
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
		
		return lista;
	}
	
	// READ (Lista Admin): Recupera TUTTI gli amministratori (Admin e Super Admin) registrati
		public List<Utente> doRetrieveAllAdmins() throws SQLException
		{
			// Seleziona solo chi ha permessi di amministrazione (isAdmin > 0)
			String query = "SELECT * FROM Utente WHERE isAdmin > 0 ORDER BY isAdmin DESC, Username ASC";
			
			Connection conn = null;
			PreparedStatement ps = null;
			
			List<Utente> listaAdmins = new ArrayList<>();

			try
			{
				conn = ConnessioneDB.getConnection();
				ps = conn.prepareStatement(query);
				
				ResultSet rs = ps.executeQuery();
				
				while(rs.next())
				{
					Utente u = estraiUtente(rs);
					listaAdmins.add(u);	
				}
				
			} catch (SQLException e) { /* Errore in console */ e.printStackTrace(); }
			
			finally { 
				try 
				{
					if (ps != null) ps.close(); 
				} catch (SQLException e) { e.printStackTrace(); }
				
				if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
			}
			
			return listaAdmins;
		}
	
	// UPDATE: Modifica l'anagrafica completa di un utente esistente
	// (Ritorna TRUE se la modifica avviene con successo, FALSE in caso di errore)
	public boolean doUpdate(Utente utenteModificato) throws SQLException
	{
		String query = "UPDATE Utente SET Email = ?, Password = ?, Nome = ?, Cognome = ?, Bio = ?, Telefono = ? WHERE Username = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setString(1, utenteModificato.getEmail());
			ps.setString(2, utenteModificato.getPassword());
			ps.setString(3, utenteModificato.getNome());
			ps.setString(4, utenteModificato.getCognome());
			ps.setString(5, utenteModificato.getBio());
			ps.setString(6, utenteModificato.getTelefono());
			ps.setString(7, utenteModificato.getUsername());
			
			int row = ps.executeUpdate();
			return row > 0;
			
		} catch (SQLException e) { /* Errore in console */ e.printStackTrace(); 
			return false;	}
		
		finally { 
			try 
			{
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
	}
	
	// UPDATE (Specifico): Modifica esclusivamente la password di un utente tramite la sua Email
	// (Ritorna TRUE se la modifica avviene con successo, FALSE in caso di errore)
	public boolean updatePassword(String email, String nuovaPassword) throws SQLException
	{
		String query = "UPDATE Utente SET Password = ? WHERE Email = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setString(1, nuovaPassword);
			ps.setString(2, email);
			
			int row = ps.executeUpdate();
			return row > 0;
	
		} catch (SQLException e) { /* Errore in console */ e.printStackTrace(); 	}
		
		finally { 
			try 
			{
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
		
		return false;
	}
	
	// DELETE: Rimuove definitivamente un utente dal database (Hard Delete)
	// (Ritorna TRUE se l'eliminazione avviene con successo, FALSE in caso di errore)
	public boolean doDelete(String username) throws SQLException
	{
		String query = "DELETE FROM Utente WHERE Username = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setString(1, username);
			
			int row = ps.executeUpdate();
			return row > 0;
			
		} catch (SQLException e) { /* Errore in console */ e.printStackTrace(); 	}
		
		finally { 
			try 
			{
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
		
		return false;
	}
	
	// READ (Singolo): Recupera un utente specifico tramite la sua Email
	public Utente doRetrieveByEmail(String email) throws SQLException 
	{
		String query = "SELECT * FROM Utente WHERE Email = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		Utente utenteTrovato = null;
				
		try {
			conn = ConnessioneDB.getConnection(); 
			ps = conn.prepareStatement(query);
			
			ps.setString(1, email);
			
			try( ResultSet rs = ps.executeQuery() ) 
			{
				if(rs.next()) 
				{
					utenteTrovato = estraiUtente(rs);
				}
			}
				
		} catch (SQLException e) { /* Errore in console */ e.printStackTrace(); 	}
		
		finally { 
			try 
			{
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
		
		return utenteTrovato;
	}

	// Main per testing
	/*
	public static void main(String[] args) {
		UtenteDAO dao = new UtenteDAO();
		System.out.println("=== INIZIO COLLAUDO COMPLETO REFRAME DAO ===");

		try {
			// --- 1. TEST: doSave (Registrazione nuovo utente) ---
			System.out.println("\n[TEST 1] Inserimento nuovo utente...");
			
			Utente nuovo = new Utente("cliente_test_01", "cliente@prova.it", "pass123", "3331234567","Mario", "Rossi", "Nessuna bio");
			
			dao.doSave(nuovo);
			System.out.println("-> OK: Utente salvato nel DB.");

			// --- 2. TEST: VerificaEmail (Controllo doppioni) ---
			System.out.println("\n[TEST 2] Controllo esistenza email...");
			boolean emailEsiste = dao.VerificaEmail("cliente@prova.it");
			boolean emailFalsa = dao.VerificaEmail("non_esisto@prova.it");
			System.out.println("-> OK: Email reale trovata? " + emailEsiste + " (Dovrebbe essere true)");
			System.out.println("-> OK: Email inventata trovata? " + emailFalsa + " (Dovrebbe essere false)");

			// --- 3. TEST: doRetrieveByKey (Recupero dati profilo) ---
			System.out.println("\n[TEST 3] Recupero dati tramite Username...");
			Utente recuperato = dao.doRetrieveByKey("cliente_test_01");
			System.out.println("-> OK: Trovato " + recuperato.getNome() + " " + recuperato.getCognome());

			// --- 4. TEST: doUpdate (Modifica profilo e password) ---
			System.out.println("\n[TEST 4] Aggiornamento dei dati...");
			recuperato.setNome("Mario Modificato");
			recuperato.setPassword("nuovaPassword456");
			boolean aggiornato = dao.doUpdate(recuperato);
			System.out.println("-> OK: Dati aggiornati? " + aggiornato);

			// --- 5. TEST: Login (doRetrieveByEmailAndPassword) ---
			System.out.println("\n[TEST 5] Simulazione Login con nuova password...");
			Utente loggato = dao.doRetrieveByEmailAndPassword("cliente@prova.it", "nuovaPassword456");
			if (loggato != null) {
				System.out.println("-> OK: Accesso consentito a " + loggato.getNome());
			} else {
				System.out.println("-> ERRORE: Credenziali non riconosciute!");
			}

			// --- 6. TEST: doRetrieveAll (Stampa catalogo clienti) ---
			System.out.println("\n[TEST 6] Recupero lista di TUTTI gli utenti...");
			// Passiamo un ordine vuoto per testare la query base senza crashare
			List<Utente> listaCompleta = dao.doRetrieveAll(""); 
			System.out.println("-> OK: Trovati " + listaCompleta.size() + " utenti nel database.");
			for (Utente u : listaCompleta) {
				System.out.println("   - " + u.getUsername() + " | " + u.getEmail());
			}

			// --- 7. TEST: doDelete (Eliminazione account) ---
			System.out.println("\n[TEST 7] Eliminazione utente di test...");
			boolean eliminato = dao.doDelete("cliente_test_01");
			System.out.println("-> OK: Utente cancellato definitivamente? " + eliminato);

			System.out.println("\n=== TUTTI I 7 METODI HANNO SUPERATO IL COLLAUDO! ===");

		} catch (Exception e) {
			System.out.println("\n[!] CRASH DURANTE IL TEST [!]");
			e.printStackTrace();
		}
	}
	*/

}