package reframe.model.dao;

import reframe.model.beans.Recensione;
import reframe.utils.*;
import java.sql.*;
import java.util.*;

public class RecensioniDAO {
    
    /* UTILITY E MAPPING RESULTSET */
    
    private Recensione estraiRecensione(ResultSet rs) throws SQLException {
        Recensione recensione = new Recensione();
        
        recensione.setIdRecensione(rs.getString("ID_recensione"));
        recensione.setDescrizione(rs.getString("Descrizione"));
        recensione.setRating(rs.getDouble("Rating"));
        recensione.setIdProdotto(rs.getString("ID_Prodotto"));
        recensione.setIdUtente(rs.getString("ID_Utente"));
        
        return recensione;
    }
    
    /* OPERAZIONI DI CREAZIONE (CREATE) */
    
    public boolean doSave(Recensione nuovaRecensione) throws SQLException {
        String query = "INSERT INTO Recensione (ID_recensione, Descrizione, Rating, ID_Prodotto, ID_Utente) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection(); 
            ps = conn.prepareStatement(query); 
            
            ps.setString(1, nuovaRecensione.getIdRecensione());
            ps.setString(2, nuovaRecensione.getDescrizione());
            ps.setDouble(3, nuovaRecensione.getRating());
            ps.setString(4, nuovaRecensione.getIdProdotto());
            ps.setString(5, nuovaRecensione.getIdUtente());
            
            int row = ps.executeUpdate();
            return row > 0;
            
        } catch(SQLException e) { 
            e.printStackTrace();
            return false;
        } finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
    }
    
    /* OPERAZIONI DI RECUPERO DATI (READ) */
    
    public List<Recensione> doRetrieveByProdotto(String idProdotto) throws SQLException {
        // L'ordinamento decrescente sull'ID garantisce il recupero prioritario delle recensioni cronologicamente più recenti.
        String query = "SELECT * FROM Recensione WHERE ID_Prodotto = ? ORDER BY ID_recensione DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        List<Recensione> lista = new ArrayList<>();

        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1, idProdotto);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Recensione r = estraiRecensione(rs);
                lista.add(r);	
            }
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return lista;
    }
    
    /* OPERAZIONI DI MODERAZIONE (DELETE) */
    
    // Esegue l'eliminazione fisica del record a database: l'azione è strettamente riservata agli admin.
    public boolean doDelete(String idRecensione) throws SQLException {
        String query = "DELETE FROM Recensione WHERE ID_recensione = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1, idRecensione);
            
            int row = ps.executeUpdate();
            return row > 0;
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false;	
        } finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
    }
}