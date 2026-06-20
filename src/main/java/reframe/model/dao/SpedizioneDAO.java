package reframe.model.dao;

import reframe.model.beans.Spedizione;
import reframe.utils.*;
import java.sql.*;
import java.util.*;

public class SpedizioneDAO 
{
	// UTILITY: Mappa i dati della riga corrente del ResultSet in un oggetto Spedizione
	private Spedizione estraiSpedizione(ResultSet rs) throws SQLException 
	{
		Spedizione spedizione = new Spedizione();
		
		spedizione.setIdSpedizione(rs.getInt("ID_Spedizione"));
		spedizione.setIdUtente(rs.getString("ID_Utente"));
		spedizione.setCitta(rs.getString("Citta"));
		spedizione.setProvincia(rs.getString("Provincia"));
		spedizione.setPaese(rs.getString("Paese"));
		spedizione.setCap(rs.getString("CAP"));
		spedizione.setVia(rs.getString("Via"));
		spedizione.setCivico(rs.getString("Civico"));
		spedizione.setNote(rs.getString("Note"));
		
		return spedizione;
	}

	// CREATE: Salva un nuovo indirizzo nel database
	// (Ritorna TRUE se l'inserimento avviene con successo, FALSE in caso di errore)
	public boolean doSave(Spedizione spedizione) throws SQLException
	{
		String query = "INSERT INTO Dati_Spedizione (ID_Utente, Citta, Provincia, Paese, CAP, Via, Civico, Note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			conn = ConnessioneDB.getConnection(); 
			ps = conn.prepareStatement(query); 
			
			ps.setString(1, spedizione.getIdUtente());
			ps.setString(2, spedizione.getCitta());
			ps.setString(3, spedizione.getProvincia());
			ps.setString(4, spedizione.getPaese());
			ps.setString(5, spedizione.getCap());
			ps.setString(6, spedizione.getVia());
			ps.setString(7, spedizione.getCivico());
			ps.setString(8, spedizione.getNote());
			
			int row = ps.executeUpdate();
			return row > 0;
			
		} catch(SQLException e) { /* Errore nella console */ e.printStackTrace();
			return false;
		}
		finally { 
			try {
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
	}

	// READ (Singolo): Recupera un indirizzo specifico tramite il suo ID
	public Spedizione doRetrieveById(int idSpedizione) throws SQLException
	{
		String query = "SELECT * FROM Dati_Spedizione WHERE ID_Spedizione = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		Spedizione spedizioneTrovata = null;
		
		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setInt(1, idSpedizione);
			
			ResultSet rs = ps.executeQuery();
			
			if(rs.next())
			{
				spedizioneTrovata = estraiSpedizione(rs);
			}
			
		} catch (SQLException e) { /* Errore in console */ e.printStackTrace(); }
		finally { 
			try {
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
		
		return spedizioneTrovata;
	}

	// READ (Lista): Recupera TUTTI gli indirizzi salvati da un utente
	public List<Spedizione> doRetrieveByUtente(String usernameUtente) throws SQLException
	{
		String query = "SELECT * FROM Dati_Spedizione WHERE ID_Utente = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		List<Spedizione> lista = new ArrayList<>();
		
		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setString(1, usernameUtente);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				Spedizione s = estraiSpedizione(rs);
				lista.add(s);	
			}
			
		} catch (SQLException e) { /* Errore in console */ e.printStackTrace(); }
		finally { 
			try {
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
		
		return lista;
	}

	// UPDATE: Modifica un indirizzo esistente
	// (Ritorna TRUE se la modifica avviene con successo, FALSE in caso di errore)
	public boolean doUpdate(Spedizione spedizione) throws SQLException
	{
		String query = "UPDATE Dati_Spedizione SET Citta = ?, Provincia = ?, Paese = ?, CAP = ?, Via = ?, Civico = ?, Note = ? WHERE ID_Spedizione = ? AND ID_Utente = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setString(1, spedizione.getCitta());
			ps.setString(2, spedizione.getProvincia());
			ps.setString(3, spedizione.getPaese());
			ps.setString(4, spedizione.getCap());
			ps.setString(5, spedizione.getVia());
			ps.setString(6, spedizione.getCivico());
			ps.setString(7, spedizione.getNote());
			ps.setInt(8, spedizione.getIdSpedizione());
			ps.setString(9, spedizione.getIdUtente()); // Misura di sicurezza
			
			int row = ps.executeUpdate();
			return row > 0;
			
		} catch (SQLException e) { /* Errore in console */ e.printStackTrace(); 
			return false;	
		}
		finally { 
			try {
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
	}

	// DELETE: Rimuove un indirizzo dal database
	// (Ritorna TRUE se l'eliminazione avviene con successo, FALSE in caso di errore)
	public boolean doDelete(int idSpedizione) throws SQLException
	{
		String query = "DELETE FROM Dati_Spedizione WHERE ID_Spedizione = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setInt(1, idSpedizione);
			
			int row = ps.executeUpdate();
			return row > 0;
			
		} catch (SQLException e) { /* Errore in console */ e.printStackTrace(); 
			return false;
		}
		finally { 
			try {
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
	}
	
	// Main per testing
		/*
		public static void main(String[] args) {
			SpedizioneDAO dao = new SpedizioneDAO();
			System.out.println("=== INIZIO COLLAUDO SPEDIZIONE DAO ===");

			try {
				// --- 1. TEST: doSave (Inserimento nuovo indirizzo) ---
				System.out.println("\n[TEST 1] Inserimento nuovo indirizzo di spedizione...");
				// L'ID lo impostiamo a 0 per via dell'AUTO_INCREMENT
				Spedizione nuovoIndirizzo = new Spedizione(0, "cliente_test_01", "Salerno", "SA", "Italia", "84084", "Via Roma", "10", "Citofono Rossi");
				
				boolean salvato = dao.doSave(nuovoIndirizzo);
				System.out.println("-> OK: Indirizzo salvato nel DB? " + salvato);

				// --- 2. TEST: doRetrieveByUtente (Lista indirizzi) ---
				System.out.println("\n[TEST 2] Recupero rubrica indirizzi dell'utente...");
				List<Spedizione> rubrica = dao.doRetrieveByUtente("cliente_test_01");
				System.out.println("-> OK: Trovati " + rubrica.size() + " indirizzi.");
				
				int idIndirizzoTest = -1; // Salviamo l'ID per usarlo dopo
				
				if (!rubrica.isEmpty()) {
					Spedizione indirizzo = rubrica.get(0);
					idIndirizzoTest = indirizzo.getIdSpedizione();
					System.out.println("   - Indirizzo ID " + idIndirizzoTest + " | " + indirizzo.getVia() + " " + indirizzo.getCivico() + ", " + indirizzo.getCitta());
				}

				// Proseguiamo con i test solo se l'inserimento precedente è andato a buon fine
				if (idIndirizzoTest != -1) {
					
					// --- 3. TEST: doRetrieveById (Recupero singolo indirizzo) ---
					System.out.println("\n[TEST 3] Recupero singolo indirizzo tramite ID...");
					Spedizione indirizzoRecuperato = dao.doRetrieveById(idIndirizzoTest);
					System.out.println("-> OK: Recuperato indirizzo di " + indirizzoRecuperato.getCitta());

					// --- 4. TEST: doUpdate (Modifica note corriere) ---
					System.out.println("\n[TEST 4] Aggiornamento delle note per il corriere...");
					indirizzoRecuperato.setNote("Lasciare il pacco al portiere se non c'è nessuno");
					boolean aggiornato = dao.doUpdate(indirizzoRecuperato);
					System.out.println("-> OK: Note corriere aggiornate? " + aggiornato);

					// --- 5. TEST: doDelete (Rimozione indirizzo) ---
					System.out.println("\n[TEST 5] Eliminazione indirizzo dalla rubrica...");
					boolean eliminato = dao.doDelete(idIndirizzoTest);
					System.out.println("-> OK: Indirizzo eliminato definitivamente? " + eliminato);
				}

				System.out.println("\n=== COLLAUDO SPEDIZIONE COMPLETATO! ===");

			} catch (Exception e) {
				System.out.println("\n[!] CRASH DURANTE IL TEST [!]");
				e.printStackTrace();
			}
		}
		*/
}