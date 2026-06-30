package reframe.model.dao;

import reframe.model.beans.Ticket;
import reframe.utils.*;
import java.sql.*;
import java.util.*;

public class TicketDAO 
{
    private Ticket estraiTicket(ResultSet rs) throws SQLException 
    {
        Ticket ticket = new Ticket();
        
        ticket.setIdTicket(rs.getString("ID_ticket"));
        ticket.setTestoMessaggio(rs.getString("Testo_Messaggio"));
        ticket.setDataTicket(rs.getTimestamp("Data_ticket"));
        ticket.setRmaPratica(rs.getString("RMA_Pratica"));
        ticket.setAutoreMessaggio(rs.getString("Autore_Messaggio"));
        
        return ticket;
    }

    public boolean doSave(Ticket nuovoTicket) throws SQLException 
    {
        String query = "INSERT INTO Ticket (ID_ticket, Testo_Messaggio, Data_ticket, RMA_Pratica, Autore_Messaggio) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try 
        {
            conn = ConnessioneDB.getConnection(); 
            ps = conn.prepareStatement(query); 
            
            ps.setString(1, nuovoTicket.getIdTicket());
            ps.setString(2, nuovoTicket.getTestoMessaggio());
            ps.setTimestamp(3, nuovoTicket.getDataTicket());
            ps.setString(4, nuovoTicket.getRmaPratica());
            ps.setString(5, nuovoTicket.getAutoreMessaggio());
            
            int row = ps.executeUpdate();
            return row > 0;
            
        } catch(SQLException e) { e.printStackTrace(); return false; } 
        finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
    }

    public Ticket doRetrieveByKey(String idTicket) throws SQLException 
    {
        String query = "SELECT * FROM Ticket WHERE ID_ticket = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        Ticket ticketTrovato = null;
        
        try 
        {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, idTicket);
            
            try( ResultSet rs = ps.executeQuery() ) {
                if(rs.next()) ticketTrovato = estraiTicket(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return ticketTrovato;
    }

    // Ora cerca per la nuova colonna RMA_Pratica
    public List<Ticket> doRetrieveByPratica(String rmaPratica) throws SQLException 
    {
        String query = "SELECT * FROM Ticket WHERE RMA_Pratica = ? ORDER BY Data_ticket ASC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        List<Ticket> lista = new ArrayList<>();
        
        try 
        {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, rmaPratica);
            
            try( ResultSet rs = ps.executeQuery() ) {
                while(rs.next()) lista.add(estraiTicket(rs));    
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return lista;
    }

    public List<Ticket> doRetrieveAll(String order) throws SQLException 
    {
        String query = "SELECT * FROM Ticket";
        
        Connection conn = null;
        PreparedStatement ps = null;
        List<Ticket> lista = new ArrayList<>();
        
        if(order != null && !order.trim().isEmpty()) {
            query += " ORDER BY " + order;
        }

        try 
        {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            try( ResultSet rs = ps.executeQuery() ) {
                while(rs.next()) lista.add(estraiTicket(rs));    
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return lista;
    }
    
    public boolean doDelete(String idTicket) throws SQLException 
    {
        String query = "DELETE FROM Ticket WHERE ID_ticket = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try 
        {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, idTicket);
            
            int row = ps.executeUpdate();
            return row > 0;
            
        } catch (SQLException e) { e.printStackTrace(); return false; }
        finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
    }
}