package reframe.model.dao;

import reframe.model.beans.Ticket;
import reframe.utils.ConnessioneDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    // UTILITY: Mappa la riga del ResultSet in un oggetto Ticket
    private Ticket estraiTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setIdTicket(rs.getString("ID_ticket"));
        ticket.setRmaPratica(rs.getString("RMA_Pratica"));
        ticket.setAutore(rs.getString("Autore"));
        ticket.setTipo(rs.getString("Tipo"));
        ticket.setMessaggio(rs.getString("Messaggio"));
        ticket.setDataTicket(rs.getTimestamp("Data_ticket"));
        return ticket;
    }

    // CREATE: Inserisce un nuovo messaggio nella chat
    public boolean doSave(Ticket ticket) throws SQLException {
        String query = "INSERT INTO Ticket (ID_ticket, RMA_Pratica, Autore, Tipo, Messaggio) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1, ticket.getIdTicket());
            ps.setString(2, ticket.getRmaPratica());
            ps.setString(3, ticket.getAutore());
            ps.setString(4, ticket.getTipo());
            ps.setString(5, ticket.getMessaggio());
            
            int row = ps.executeUpdate();
            return row > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); }
        }
    }

    // READ (Lista): Estrae l'intera cronologia chat di una singola Pratica, ordinata per data (dal più vecchio al più recente)
    public List<Ticket> doRetrieveByRma(String rma) throws SQLException {
        String query = "SELECT * FROM Ticket WHERE RMA_Pratica = ? ORDER BY Data_ticket ASC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        List<Ticket> cronologia = new ArrayList<>();
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, rma);
            
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    cronologia.add(estraiTicket(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); }
        }
        return cronologia;
    }
}