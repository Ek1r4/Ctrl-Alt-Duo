package reframe.model.dao;

import reframe.model.beans.Pagamento;
import reframe.utils.*;
import java.sql.*;
import java.util.*;

public class PagamentoDAO 
{
	// UTILITY: Mappa i dati della riga corrente del ResultSet in un oggetto Pagamento
	private Pagamento estraiPagamento(ResultSet rs) throws SQLException 
	{
		Pagamento pagamento = new Pagamento();
		
		pagamento.setIdPagamento(rs.getInt("ID_Pagamento"));
		pagamento.setIdUtente(rs.getString("ID_Utente"));
		pagamento.setNomeIntestatario(rs.getString("Nome_intestatario"));
		pagamento.setCircuito(rs.getString("Circuito"));
		pagamento.setNumeroCarta(rs.getString("Numero_carta"));
		pagamento.setDataScadenza(rs.getString("Data_scadenza"));
		pagamento.setCvv(rs.getString("CVV"));
		
		return pagamento;
	}

	// CREATE: Salva un nuovo metodo di pagamento nel database
	// (Ritorna TRUE se l'inserimento avviene con successo, FALSE in caso di errore)
	public boolean doSave(Pagamento pagamento) throws SQLException
	{
		String query = "INSERT INTO Dati_Pagamento (ID_Utente, Nome_intestatario, Circuito, Numero_carta, Data_scadenza, CVV) VALUES (?, ?, ?, ?, ?, ?)";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			conn = ConnessioneDB.getConnection(); 
			ps = conn.prepareStatement(query); 
			
			ps.setString(1, pagamento.getIdUtente());
			ps.setString(2, pagamento.getNomeIntestatario());
			ps.setString(3, pagamento.getCircuito());
			ps.setString(4, pagamento.getNumeroCarta());
			ps.setString(5, pagamento.getDataScadenza());
			ps.setString(6, pagamento.getCvv());
			
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

	// READ (Singolo): Recupera un pagamento specifico tramite il suo ID
	public Pagamento doRetrieveById(int idPagamento) throws SQLException
	{
		String query = "SELECT * FROM Dati_Pagamento WHERE ID_Pagamento = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		Pagamento pagamentoTrovato = null;
		
		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setInt(1, idPagamento);
			
			ResultSet rs = ps.executeQuery();
			
			if(rs.next())
			{
				pagamentoTrovato = estraiPagamento(rs);
			}
			
		} catch (SQLException e) { /* Errore in console */ e.printStackTrace(); }
		finally { 
			try {
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
		
		return pagamentoTrovato;
	}

	// READ (Lista): Recupera TUTTE le carte salvate da un utente tramite il suo Username
	public List<Pagamento> doRetrieveByUtente(String usernameUtente) throws SQLException
	{
		String query = "SELECT * FROM Dati_Pagamento WHERE ID_Utente = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		List<Pagamento> lista = new ArrayList<>();
		
		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setString(1, usernameUtente);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				Pagamento p = estraiPagamento(rs);
				lista.add(p);	
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

	// UPDATE: Modifica i dati di una carta esistente (es. rinnovo scadenza)
	// (Ritorna TRUE se la modifica avviene con successo, FALSE in caso di errore)
	public boolean doUpdate(Pagamento pagamento) throws SQLException
	{
		String query = "UPDATE Dati_Pagamento SET Nome_intestatario = ?, Circuito = ?, Numero_carta = ?, Data_scadenza = ?, CVV = ? WHERE ID_Pagamento = ? AND ID_Utente = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setString(1, pagamento.getNomeIntestatario());
			ps.setString(2, pagamento.getCircuito());
			ps.setString(3, pagamento.getNumeroCarta());
			ps.setString(4, pagamento.getDataScadenza());
			ps.setString(5, pagamento.getCvv());
			ps.setInt(6, pagamento.getIdPagamento());
			ps.setString(7, pagamento.getIdUtente()); // Misura di sicurezza aggiuntiva
			
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

	// DELETE: Rimuove un metodo di pagamento dal database
	// (Ritorna TRUE se l'eliminazione avviene con successo, FALSE in caso di errore)
	public boolean doDelete(int idPagamento) throws SQLException
	{
		String query = "DELETE FROM Dati_Pagamento WHERE ID_Pagamento = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setInt(1, idPagamento);
			
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
			PagamentoDAO dao = new PagamentoDAO();
			System.out.println("=== INIZIO COLLAUDO PAGAMENTO DAO ===");

			try {
				// --- 1. TEST: doSave (Inserimento nuova carta) ---
				System.out.println("\n[TEST 1] Inserimento nuovo metodo di pagamento...");
				// L'ID pagamento lo mettiamo a 0, ci penserà l'AUTO_INCREMENT del DB a gestirlo
				Pagamento nuovaCarta = new Pagamento(0, "cliente_test_01", "Mario Rossi", "Visa", "4123456789123456", "12/28", "123");
				
				boolean salvato = dao.doSave(nuovaCarta);
				System.out.println("-> OK: Carta salvata nel DB? " + salvato);

				// --- 2. TEST: doRetrieveByUtente (Lista carte) ---
				System.out.println("\n[TEST 2] Recupero portafoglio dell'utente...");
				List<Pagamento> portafoglio = dao.doRetrieveByUtente("cliente_test_01");
				System.out.println("-> OK: Trovate " + portafoglio.size() + " carte.");
				
				int idCartaTest = -1; // Ci salviamo l'ID generato dal DB per usarlo nei test successivi
				
				if (!portafoglio.isEmpty()) {
					Pagamento carta = portafoglio.get(0);
					idCartaTest = carta.getIdPagamento();
					System.out.println("   - Carta ID " + idCartaTest + " | Circuito: " + carta.getCircuito() + " | Terminante con: " + carta.getNumeroCarta().substring(12));
				}

				// Proseguiamo con i test solo se abbiamo trovato almeno una carta
				if (idCartaTest != -1) {
					
					// --- 3. TEST: doRetrieveById (Recupero singola carta) ---
					System.out.println("\n[TEST 3] Recupero singola carta tramite ID...");
					Pagamento cartaRecuperata = dao.doRetrieveById(idCartaTest);
					System.out.println("-> OK: Recuperata la carta di " + cartaRecuperata.getNomeIntestatario());

					// --- 4. TEST: doUpdate (Modifica scadenza) ---
					System.out.println("\n[TEST 4] Aggiornamento data di scadenza della carta...");
					cartaRecuperata.setDataScadenza("01/30");
					boolean aggiornato = dao.doUpdate(cartaRecuperata);
					System.out.println("-> OK: Scadenza aggiornata? " + aggiornato);

					// --- 5. TEST: doDelete (Rimozione carta) ---
					System.out.println("\n[TEST 5] Eliminazione carta dal portafoglio...");
					boolean eliminato = dao.doDelete(idCartaTest);
					System.out.println("-> OK: Carta eliminata definitivamente? " + eliminato);
				}

				System.out.println("\n=== COLLAUDO PAGAMENTO COMPLETATO! ===");

			} catch (Exception e) {
				System.out.println("\n[!] CRASH DURANTE IL TEST [!]");
				e.printStackTrace();
			}
		}
		*/
}