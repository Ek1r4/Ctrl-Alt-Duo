package reframe.model.dao;

import reframe.model.beans.Utente;
import reframe.utils.*;
import java.sql.*;
import java.util.*;

public class UtenteDAO {

    /* UTILITY E MAPPING RESULTSET */

    // Esegue un controllo preventivo sull'unicità dell'indirizzo email a database per evitare eccezioni di violazione dei vincoli in fase di registrazione.
    public boolean VerificaEmail(String email) throws SQLException {
        String query = "SELECT * FROM Utente WHERE Email = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection(); 
            ps = conn.prepareStatement(query);
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return true;
            }
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return false;
    }
    
    private Utente estraiUtente(ResultSet rs) throws SQLException {
        Utente utente = new Utente();
        
        utente.setUsername(rs.getString("Username"));
        utente.setEmail(rs.getString("Email"));
        utente.setPassword(rs.getString("Password"));
        utente.setNome(rs.getString("Nome"));
        utente.setCognome(rs.getString("Cognome"));
        utente.setBio(rs.getString("Bio"));
        utente.setTelefono(rs.getString("Telefono"));
        utente.setIsAdmin(rs.getInt("isAdmin"));
        
        return utente;
    }
    
    /* OPERAZIONI DI CREAZIONE E REGISTRAZIONE (CREATE) */
    
    public boolean doSave(Utente nuovoUtente) throws SQLException {
        String query = "INSERT INTO Utente (Username, Email, Password, Nome, Cognome, Bio, Telefono) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection(); 
            ps = conn.prepareStatement(query); 
            
            if (!VerificaEmail(nuovoUtente.getEmail())) {
                ps.setString(1, nuovoUtente.getUsername());
                ps.setString(2, nuovoUtente.getEmail());
                ps.setString(3, nuovoUtente.getPassword());
                ps.setString(4, nuovoUtente.getNome());
                ps.setString(5, nuovoUtente.getCognome());
                ps.setString(6, nuovoUtente.getBio());
                ps.setString(7, nuovoUtente.getTelefono());
                
                int row = ps.executeUpdate();
                return row > 0;
            }
            
        } catch(SQLException e) { 
            e.printStackTrace();
            return false;
        } finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return false;
    }
    
    // Definisce l'inserimento forzato di un nuovo profilo amministratore nel sistema: vincola l'attributo isAdmin a 1 a livello applicativo e predispone un recapito telefonico di default.
    public boolean doSaveAdmin(Utente nuovoUtente) throws SQLException {
        String query = "INSERT INTO Utente (Username, Email, Password, Nome, Cognome, isAdmin, Telefono) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection(); 
            ps = conn.prepareStatement(query); 
            
            if (!VerificaEmail(nuovoUtente.getEmail())) {
                ps.setString(1, nuovoUtente.getUsername());
                ps.setString(2, nuovoUtente.getEmail());
                ps.setString(3, nuovoUtente.getPassword());
                ps.setString(4, nuovoUtente.getNome());
                ps.setString(5, nuovoUtente.getCognome());
                ps.setInt(6, 1);
                ps.setString(7, "0000000000");
                
                int row = ps.executeUpdate();
                return row > 0;
            }
            
        } catch(SQLException e) { 
            e.printStackTrace();
            return false;
        } finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return false;
    }
    
    /* OPERAZIONI DI RECUPERO DATI (READ) */
    
    public Utente doRetrieveByKey(String Username) throws SQLException {
        String query = "SELECT * FROM Utente WHERE Username = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        Utente utenteTrovato = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1, Username);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                utenteTrovato = estraiUtente(rs);
                System.out.println("DEBUG - Login effettuato per: " + utenteTrovato);
            } else {
                System.out.println("DEBUG - Credenziali errate o utente non trovato."); 
            }
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return utenteTrovato;
    }

    // Implementa un accodamento dinamico dell'ordinamento: sanitizza l'input tramite trim() prima della concatenazione SQL per prevenire SQLException.
    public List<Utente> doRetrieveAll(String order) throws SQLException {
        String query = "SELECT * FROM Utente";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        List<Utente> lista = new ArrayList<>();
        
        if (order != null && !order.trim().isEmpty()) {
            query += " ORDER BY " + order;
        } else { 
            System.out.println("DEBUG - Nessun ordine specificato, eseguo la query base: " + query); 
        }

        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Utente u = estraiUtente(rs);
                lista.add(u);	
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
    
    public List<Utente> doRetrieveAllAdmins() throws SQLException {
        // Isola logicamente i profili admin escludendo i clienti base (isAdmin = 0).
        String query = "SELECT * FROM Utente WHERE isAdmin > 0 ORDER BY isAdmin DESC, Username ASC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        List<Utente> listaAdmins = new ArrayList<>();

        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Utente u = estraiUtente(rs);
                listaAdmins.add(u);	
            }
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return listaAdmins;
    }
    
    public Utente doRetrieveByEmail(String email) throws SQLException {
        String query = "SELECT * FROM Utente WHERE Email = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        Utente utenteTrovato = null;
                
        try {
            conn = ConnessioneDB.getConnection(); 
            ps = conn.prepareStatement(query);
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    utenteTrovato = estraiUtente(rs);
                }
            }
                
        } catch (SQLException e) { 
            e.printStackTrace(); 	
        } finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return utenteTrovato;
    }

    /* OPERAZIONI DI AGGIORNAMENTO (UPDATE) */
    
    public boolean doUpdate(Utente utenteModificato) throws SQLException {
        String query = "UPDATE Utente SET Email = ?, Password = ?, Nome = ?, Cognome = ?, Bio = ?, Telefono = ? WHERE Username = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1, utenteModificato.getEmail());
            ps.setString(2, utenteModificato.getPassword());
            ps.setString(3, utenteModificato.getNome());
            ps.setString(4, utenteModificato.getCognome());
            ps.setString(5, utenteModificato.getBio());
            ps.setString(6, utenteModificato.getTelefono());
            ps.setString(7, utenteModificato.getUsername());
            
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
    
    public boolean updatePassword(String email, String nuovaPassword) throws SQLException {
        String query = "UPDATE Utente SET Password = ? WHERE Email = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1, nuovaPassword);
            ps.setString(2, email);
            
            int row = ps.executeUpdate();
            return row > 0;
    
        } catch (SQLException e) { 
            e.printStackTrace(); 	
        } finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return false;
    }
    
    /* GESTIONE STATO E RIMOZIONE (DELETE) */
    
    // Esegue l'eliminazione fisica e definitiva del record a database.
    public boolean doDelete(String username) throws SQLException {
        String query = "DELETE FROM Utente WHERE Username = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1, username);
            
            int row = ps.executeUpdate();
            return row > 0;
            
        } catch (SQLException e) { 
            e.printStackTrace(); 	
        } finally { 
            try {
                if (ps != null) ps.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        
        return false;
    }
}