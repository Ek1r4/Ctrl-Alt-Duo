package reframe.model.dao;

import reframe.model.beans.Ticket;
import reframe.utils.*;
import java.sql.*;
import java.util.*;

public class TicketDAO 
{
	// Dentro TicketDAO.java

	private Ticket estraiTicket(ResultSet rs) throws SQLException {
	    Ticket ticket = new Ticket();
	    ticket.setIdTicket(rs.getString("ID_ticket"));
	    ticket.setTitolo(rs.getString("Titolo"));           // Aggiornato
	    ticket.setCategoria(rs.getString("Categoria"));     // Aggiornato
	    ticket.setTestoMessaggio(rs.getString("Testo_Messaggio"));
	    ticket.setDataTicket(rs.getTimestamp("Data_ticket"));
	    ticket.setRmaPratica(rs.getString("RMA_Pratica"));
	    ticket.setAutoreMessaggio(rs.getString("Autore_Messaggio"));
	    return ticket;
	}

	public boolean doSave(Ticket nuovoTicket) throws SQLException {
	    // Aggiornata la query con le nuove colonne
	    String query = "INSERT INTO Ticket (ID_ticket, Titolo, Categoria, Testo_Messaggio, Data_ticket, RMA_Pratica, Autore_Messaggio) VALUES (?, ?, ?, ?, ?, ?, ?)";
	    
	    Connection conn = null;
	    PreparedStatement ps = null;
	    
	    try {
	        conn = ConnessioneDB.getConnection(); 
	        ps = conn.prepareStatement(query); 
	        
	        ps.setString(1, nuovoTicket.getIdTicket());
	        ps.setString(2, nuovoTicket.getTitolo());       // Nuovo
	        ps.setString(3, nuovoTicket.getCategoria());    // Nuovo
	        ps.setString(4, nuovoTicket.getTestoMessaggio());
	        ps.setTimestamp(5, nuovoTicket.getDataTicket());
	        ps.setString(6, nuovoTicket.getRmaPratica());
	        ps.setString(7, nuovoTicket.getAutoreMessaggio());
	        
	        return ps.executeUpdate() > 0;
	    } finally {
	        if (ps != null) ps.close();
	        if (conn != null) ConnessioneDB.releaseConnection(conn);
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
    
    public List<Ticket> doRetrieveByCategoria(String categoria) throws SQLException 
    {
        String query = "SELECT * FROM Ticket WHERE Categoria = ? ORDER BY Data_ticket ASC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        List<Ticket> lista = new ArrayList<>();
        
        try 
        {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, categoria);
            
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
    
    public List<Ticket> doRetrieveByTitolo(String titolo) throws SQLException 
    {
        String query = "SELECT * FROM Ticket WHERE Titolo LIKE ? ORDER BY Data_ticket ASC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        List<Ticket> lista = new ArrayList<>();
        
        try 
        {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, "%" + titolo + "%");
            
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