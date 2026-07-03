package reframe.model.dao;

import reframe.model.beans.Pagamento;
import reframe.utils.*;
import java.sql.*;
import java.util.*;

public class PagamentoDAO 
{
    /* UTILITY: MAPPING RESULTSET */
    
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
        pagamento.setAttivo(rs.getBoolean("isAttivo"));
        
        return pagamento;
    }

    /* OPERAZIONI CRUD E AMMINISTRAZIONE DATABASE */

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
            
        } catch(SQLException e) { 
            e.printStackTrace();
            return false;
        }
        finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
    }

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
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return pagamentoTrovato;
    }

    public List<Pagamento> doRetrieveByUtente(String usernameUtente) throws SQLException
    {
        // Limita il recupero ai soli metodi di pagamento non contrassegnati come eliminati per preservare la cronologia degli ordini senza sporcare la UI
        String query = "SELECT * FROM Dati_Pagamento WHERE ID_Utente = ? AND isAttivo = true";
        
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
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return lista;
    }

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
            ps.setString(7, pagamento.getIdUtente()); 
            
            int row = ps.executeUpdate();
            return row > 0;
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false;   
        }
        finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
    }

    // Pattern Soft Delete: esegue un update logico del record (isAttivo = false) anziché una rimozione fisica (DELETE), 
    // garantendo la consistenza referenziale per gli ordini già fatturati legati a questo metodo di pagamento
    public boolean doDelete(int idPagamento) throws SQLException {
        String query = "UPDATE Dati_Pagamento SET isAttivo = false WHERE ID_Pagamento = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setInt(1, idPagamento);
            
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