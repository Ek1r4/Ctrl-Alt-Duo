package reframe.model.dao;

import reframe.model.beans.PraticaAssistenza;
import reframe.utils.ConnessioneDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PraticaAssistenzaDAO {

    // UTILITY: Mappa i dati della riga corrente del ResultSet in un oggetto PraticaAssistenza
    private PraticaAssistenza estraiPratica(ResultSet rs) throws SQLException {
        PraticaAssistenza pratica = new PraticaAssistenza();
        
        pratica.setRma(rs.getString("RMA"));
        pratica.setTitolo(rs.getString("Titolo"));
        pratica.setCategoria(rs.getString("Categoria"));
        pratica.setDescrizione(rs.getString("Descrizione"));
        pratica.setStato(rs.getString("Stato"));
        pratica.setDataApertura(rs.getTimestamp("Data_apertura"));
        pratica.setDataChiusura(rs.getTimestamp("Data_chiusura"));
        pratica.setIdUtente(rs.getString("ID_Utente"));
        pratica.setAdminAssegnato(rs.getString("Admin_Assegnato"));
        
        return pratica;
    }

    // CREATE: Salva una nuova pratica (Le date sono gestite dal default TIMESTAMP di MySQL)
    public boolean doSave(PraticaAssistenza pratica) throws SQLException {
        String query = "INSERT INTO Pratica_Assistenza (RMA, Titolo, Categoria, Descrizione, Stato, ID_Utente) VALUES (?, ?, ?, ?, 'Aperta', ?)";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection(); 
            ps = conn.prepareStatement(query); 
            
            ps.setString(1, pratica.getRma());
            ps.setString(2, pratica.getTitolo());
            ps.setString(3, pratica.getCategoria());
            ps.setString(4, pratica.getDescrizione());
            ps.setString(5, pratica.getIdUtente());
            
            int row = ps.executeUpdate();
            return row > 0;
            
        } catch(SQLException e) { 
            e.printStackTrace();
            return false;
        } finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
    }

    // READ (Singolo): Recupera una pratica specifica tramite il suo RMA
    public PraticaAssistenza doRetrieveByRma(String rma) throws SQLException {
        String query = "SELECT * FROM Pratica_Assistenza WHERE RMA = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        PraticaAssistenza praticaTrovata = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, rma);
            
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    praticaTrovata = estraiPratica(rs);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        return praticaTrovata;
    }

    // READ (Lista): Recupera TUTTE le pratiche di uno specifico UTENTE (Per la sua area personale)
    public List<PraticaAssistenza> doRetrieveByUser(String idUtente) throws SQLException {
        String query = "SELECT * FROM Pratica_Assistenza WHERE ID_Utente = ? ORDER BY Data_apertura DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        List<PraticaAssistenza> lista = new ArrayList<>();
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, idUtente);
            
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    lista.add(estraiPratica(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        return lista;
    }

    // READ (Lista Filtri): Recupera le pratiche in base allo stato o all'admin (Per Dashboard Admin/Superadmin)
    // Passa null come parametro se non vuoi applicare quel filtro
    public List<PraticaAssistenza> doRetrieveAll(String filtroStato, String idAdmin) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT * FROM Pratica_Assistenza WHERE 1=1");
        
        if (filtroStato != null && !filtroStato.isEmpty()) { query.append(" AND Stato = ?"); }
        if (idAdmin != null && !idAdmin.isEmpty()) { query.append(" AND Admin_Assegnato = ?"); }
        
        query.append(" ORDER BY Data_apertura DESC");
        
        Connection conn = null;
        PreparedStatement ps = null;
        List<PraticaAssistenza> lista = new ArrayList<>();
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query.toString());
            
            int paramIndex = 1;
            if (filtroStato != null && !filtroStato.isEmpty()) { ps.setString(paramIndex++, filtroStato); }
            if (idAdmin != null && !idAdmin.isEmpty()) { ps.setString(paramIndex, idAdmin); }
            
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    lista.add(estraiPratica(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { 
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        return lista;
    }

    // UPDATE: Cambia lo stato della pratica. Se passa a "Chiusa", compila la data in automatico.
    public boolean updateStato(String rma, String nuovoStato) throws SQLException {
        String query;
        if ("Chiusa".equalsIgnoreCase(nuovoStato)) {
            query = "UPDATE Pratica_Assistenza SET Stato = ?, Data_chiusura = CURRENT_TIMESTAMP WHERE RMA = ?";
        } else {
            // Se viene riaperta, svuotiamo la data di chiusura per rispettare il constraint del DB
            query = "UPDATE Pratica_Assistenza SET Stato = ?, Data_chiusura = NULL WHERE RMA = ?";
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, nuovoStato);
            ps.setString(2, rma);
            
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

 // UPDATE: Assegna o cambia l'admin incaricato della pratica (Azione Superadmin)
    public boolean updateAdminAssegnato(String rma, String idAdmin) throws SQLException {
        // Quando un admin viene assegnato, la pratica passa automaticamente "In carico"
        // E svuotiamo la Data_chiusura per rispettare il check constraint 'chk_coerenza_data' del DB!
        String query = "UPDATE Pratica_Assistenza SET Admin_Assegnato = ?, Stato = 'In carico', Data_chiusura = NULL WHERE RMA = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, idAdmin);
            ps.setString(2, rma);
            
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
}