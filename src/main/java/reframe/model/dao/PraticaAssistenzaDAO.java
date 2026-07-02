package reframe.model.dao;

import reframe.model.beans.PraticaAssistenza;
import reframe.utils.*;
import java.sql.*;
import java.util.*;

public class PraticaAssistenzaDAO 
{
	private PraticaAssistenza estraiPraticaAssistenza(ResultSet rs) throws SQLException 
    {
        PraticaAssistenza pratica = new PraticaAssistenza();
        
        pratica.setRma(rs.getString("RMA"));
        pratica.setStato(rs.getString("Stato"));
        pratica.setTitolo(rs.getString("Titolo"));         // Aggiunto
        pratica.setCategoria(rs.getString("Categoria"));   // Aggiunto
        pratica.setMotivo(rs.getString("Motivo"));
        pratica.setDataApertura(rs.getTimestamp("Data_apertura"));
        pratica.setDataChiusura(rs.getTimestamp("Data_chiusura"));
        pratica.setIdUtente(rs.getString("ID_Utente"));
        pratica.setAdminInCarico(rs.getString("Admin_In_Carico")); 
        
        return pratica;
    }

    public boolean doSave(PraticaAssistenza nuovaPratica) throws SQLException 
    {
        // Corretti 10 campi e 10 punti interrogativi
        String query = "INSERT INTO Pratica_Assistenza (RMA, Stato, Titolo, Categoria, Motivo, Data_apertura, Data_chiusura, ID_Utente, Admin_In_Carico) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try 
        {
            conn = ConnessioneDB.getConnection(); 
            ps = conn.prepareStatement(query); 
            
            ps.setString(1, nuovaPratica.getRma());
            ps.setString(2, nuovaPratica.getStato());
            ps.setString(3, nuovaPratica.getTitolo());       // Aggiunto
            ps.setString(4, nuovaPratica.getCategoria());    // Aggiunto
            ps.setString(5, nuovaPratica.getMotivo());
            ps.setTimestamp(6, nuovaPratica.getDataApertura());
            ps.setTimestamp(7, nuovaPratica.getDataChiusura());
            ps.setString(8, nuovaPratica.getIdUtente());
            ps.setString(9, nuovaPratica.getAdminInCarico()); 
            
            int row = ps.executeUpdate();
            return row > 0;
            
        } catch(SQLException e) { 
            e.printStackTrace(); 
            return false; 
        } 
        finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
    }

    public PraticaAssistenza doRetrieveByKey(String rma) throws SQLException 
    {
        String query = "SELECT * FROM Pratica_Assistenza WHERE RMA = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        PraticaAssistenza praticaTrovata = null;
        
        try 
        {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, rma);
            
            try( ResultSet rs = ps.executeQuery() ) {
                if(rs.next()) praticaTrovata = estraiPraticaAssistenza(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return praticaTrovata;
    }
    
    public List<PraticaAssistenza> doRetrieveByCategoria(String categoria) throws SQLException 
    {
        // Query che filtra per la colonna Categoria
        String query = "SELECT * FROM Pratica_Assistenza WHERE Categoria = ? ORDER BY Data_apertura DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        List<PraticaAssistenza> lista = new ArrayList<>();
        
        try 
        {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, categoria); // Impostiamo il parametro della categoria
            
            try( ResultSet rs = ps.executeQuery() ) {
                while(rs.next()) {
                    lista.add(estraiPraticaAssistenza(rs));    
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return lista;
    }
    
    public List<PraticaAssistenza> doRetrieveByTitolo(String titolo) throws SQLException 
    {
        String query = "SELECT * FROM Pratica_Assistenza WHERE Titolo LIKE ? ORDER BY Data_apertura DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        List<PraticaAssistenza> lista = new ArrayList<>();
        
        try 
        {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, "%" + titolo + "%"); // Il LIKE cerca il testo all'interno del titolo
            
            try( ResultSet rs = ps.executeQuery() ) {
                while(rs.next()) lista.add(estraiPraticaAssistenza(rs));    
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return lista;
    }

    public List<PraticaAssistenza> doRetrieveAll(String order) throws SQLException 
    {
        String query = "SELECT * FROM Pratica_Assistenza";
        
        Connection conn = null;
        PreparedStatement ps = null;
        List<PraticaAssistenza> lista = new ArrayList<>();
        
        if(order != null && !order.trim().isEmpty()) {
            query += " ORDER BY " + order;
        }

        try 
        {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            try( ResultSet rs = ps.executeQuery() ) {
                while(rs.next()) lista.add(estraiPraticaAssistenza(rs));    
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return lista;
    }

    // UPDATE: Ora permette di aggiornare anche l'Admin_In_Carico
    public boolean doUpdate(PraticaAssistenza praticaModificata) throws SQLException 
    {
        String query = "UPDATE Pratica_Assistenza SET Stato = ?, Data_chiusura = ?, Admin_In_Carico = ? WHERE RMA = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try 
        {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1, praticaModificata.getStato());
            ps.setTimestamp(2, praticaModificata.getDataChiusura());
            ps.setString(3, praticaModificata.getAdminInCarico());
            ps.setString(4, praticaModificata.getRma());
            
            int row = ps.executeUpdate();
            return row > 0;
            
        } catch (SQLException e) { e.printStackTrace(); return false; } 
        finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
    }

    public boolean doDelete(String rma) throws SQLException 
    {
        String query = "DELETE FROM Pratica_Assistenza WHERE RMA = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try 
        {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, rma);
            
            int row = ps.executeUpdate();
            return row > 0;
            
        } catch (SQLException e) { e.printStackTrace(); return false; }
        finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
    }
}