package reframe.model.dao;

import reframe.model.beans.Recensione;
import reframe.utils.*;
import java.sql.*;
import java.util.*;

public class RecensioniDAO 
{
	// UTILITY: Mappa i dati della riga corrente del ResultSet in un oggetto Recensione
	private Recensione estraiRecensione(ResultSet rs) throws SQLException 
	{
		Recensione recensione = new Recensione();
		
		recensione.setIdRecensione(rs.getString("ID_recensione"));
		recensione.setDescrizione(rs.getString("Descrizione"));
		recensione.setRating(rs.getDouble("Rating"));
		recensione.setIdProdotto(rs.getString("ID_Prodotto"));
		recensione.setIdUtente(rs.getString("ID_Utente"));
		
		return recensione;
	}
	
	// CREATE: Salva una nuova recensione nel database
	// (Ritorna TRUE se l'inserimento avviene con successo, FALSE in caso di errore)
	public boolean doSave(Recensione nuovaRecensione) throws SQLException
	{
		String query = "INSERT INTO Recensione (ID_recensione, Descrizione, Rating, ID_Prodotto, ID_Utente) VALUES (?, ?, ?, ?, ?)";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			conn = ConnessioneDB.getConnection(); 
			ps = conn.prepareStatement(query); 
			
			ps.setString(1, nuovaRecensione.getIdRecensione());
			ps.setString(2, nuovaRecensione.getDescrizione());
			ps.setDouble(3, nuovaRecensione.getRating());
			ps.setString(4, nuovaRecensione.getIdProdotto());
			ps.setString(5, nuovaRecensione.getIdUtente());
			
			int row = ps.executeUpdate();
			return row > 0;
			
		} catch(SQLException e) { /* Errore nella console */ e.printStackTrace();
			return false;
		}
		
		finally { // Serve per rimettere nel ConnectionPool la connessione
			try 
			{
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
	}
	
	// READ (Lista): Recupera tutte le recensioni di un determinato prodotto
	public List<Recensione> doRetrieveByProdotto(String idProdotto) throws SQLException
	{
		// Mostriamo prima le recensioni più recenti ordinandole in modo decrescente
		String query = "SELECT * FROM Recensione WHERE ID_Prodotto = ? ORDER BY ID_recensione DESC";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		List<Recensione> lista = new ArrayList<>();

		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setString(1, idProdotto);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				Recensione r = estraiRecensione(rs);
				lista.add(r);	
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
	
	// DELETE: Rimuove una recensione dal database (Utile per la moderazione Admin)
	// (Ritorna TRUE se l'eliminazione avviene con successo, FALSE in caso di errore)
	public boolean doDelete(String idRecensione) throws SQLException
	{
		String query = "DELETE FROM Recensione WHERE ID_recensione = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			conn = ConnessioneDB.getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setString(1, idRecensione);
			
			int row = ps.executeUpdate();
			return row > 0;
			
		} catch (SQLException e) { /* Errore in console */ e.printStackTrace(); 
			return false;	
		}
		
		finally { 
			try 
			{
				if (ps != null) ps.close(); 
			} catch (SQLException e) { e.printStackTrace(); }
			
			if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
		}
	}
}