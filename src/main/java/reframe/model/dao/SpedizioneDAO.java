package reframe.model.dao;

import reframe.model.beans.Spedizione;
import reframe.utils.*;
import java.sql.*;
import java.util.*;

public class SpedizioneDAO {

    /* UTILITY E MAPPING RESULTSET */

    private Spedizione estraiSpedizione(ResultSet rs) throws SQLException {
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
        spedizione.setAttivo(rs.getBoolean("isAttivo"));
        
        return spedizione;
    }

    /* OPERAZIONI DI CREAZIONE (CREATE) */

    public boolean doSave(Spedizione spedizione) throws SQLException {
        String query = "INSERT INTO Dati_Spedizione (ID_Utente, Citta, Provincia, Paese, CAP, Via, Civico, Note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
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

    public Spedizione doRetrieveById(int idSpedizione) throws SQLException {
        String query = "SELECT * FROM Dati_Spedizione WHERE ID_Spedizione = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        Spedizione spedizioneTrovata = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setInt(1, idSpedizione);
            
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()) {
                spedizioneTrovata = estraiSpedizione(rs);
            }
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return spedizioneTrovata;
    }

    public List<Spedizione> doRetrieveByUtente(String usernameUtente) throws SQLException {
        // Applica un filtro sul flag isAttivo per escludere gli indirizzi eliminati logicamente, 
        // mostrando all'utente solo la sua rubrica attualmente valida.
        String query = "SELECT * FROM Dati_Spedizione WHERE ID_Utente = ? AND isAttivo = true";
        
        Connection conn = null;
        PreparedStatement ps = null;
        List<Spedizione> lista = new ArrayList<>();
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1, usernameUtente);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Spedizione s = estraiSpedizione(rs);
                lista.add(s);	
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

    /* OPERAZIONI DI AGGIORNAMENTO (UPDATE) */

    public boolean doUpdate(Spedizione spedizione) throws SQLException {
        String query = "UPDATE Dati_Spedizione SET Citta = ?, Provincia = ?, Paese = ?, CAP = ?, Via = ?, Civico = ?, Note = ? WHERE ID_Spedizione = ? AND ID_Utente = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
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
            ps.setString(9, spedizione.getIdUtente()); 
            
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

    /* GESTIONE STATO (DELETE) */

    // Pattern Soft Delete: esegue un update logico del record (isAttivo = false) anziché una DELETE fisica, 
    // garantendo l'integrità referenziale degli ordini storici collegati a questo indirizzo di spedizione.
    public boolean doDelete(int idSpedizione) throws SQLException {
        String query = "UPDATE Dati_Spedizione SET isAttivo = false WHERE ID_Spedizione = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setInt(1, idSpedizione);
            
            int row = ps.executeUpdate();
            return row > 0;
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false;
        } finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { 
                e.printStackTrace(); 
            }
            
            if (conn != null) { 
                ConnessioneDB.releaseConnection(conn); 
            } 
        }
    }
}