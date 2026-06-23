package reframe.model.dao;

import reframe.model.beans.Prodotto;
import reframe.utils.*;
import java.sql.*;
import java.util.*;

public class ProdottoDAO {
    
    public List<Prodotto> fetchProdottiByTipo(String tipo) throws SQLException {
        String query = "SELECT * FROM Prodotto WHERE Tipo = ?";
        List<Prodotto> prodottiTrovati = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection(); 
            ps = conn.prepareStatement(query);
            ps.setString(1, tipo);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prodotto prodotto = new Prodotto();
                    prodotto.setId(rs.getString("ID_Prodotto"));
                    prodotto.setMarchio(rs.getString("Marchio"));
                    prodotto.setNome(rs.getString("Nome"));
                    prodotto.setTipo(rs.getString("Tipo"));
                    prodotto.setStato(rs.getString("Stato"));
                    prodotto.setNumeroScatti(rs.getInt("Numero_scatti")); 
                    prodotto.setCondizioneCollezionistica(rs.getString("Condizione_collezionistica"));
                    prodotto.setSeriale(rs.getString("Seriale"));
                    prodotto.setPrezzo(rs.getDouble("Prezzo"));
                    prodotto.setIva(rs.getInt("IVA")); // Recupero IVA aggiunto
                    prodotto.setModelUrl(rs.getString("URL_Modello_3D"));
                    prodotto.setImageUrl(rs.getString("URL_Immagine_Copertina"));
                    prodotto.setDescrizione(rs.getString("Descrizione"));
                    prodotto.setInStock(rs.getInt("In_stock")); 

                    prodottiTrovati.add(prodotto);
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } finally { 
            if (ps != null) try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        return prodottiTrovati;
    }
    
    public Prodotto fetchProdottoById(String id) throws SQLException {
        String query = "SELECT * FROM Prodotto WHERE ID_Prodotto = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection(); 
            ps = conn.prepareStatement(query);
            ps.setString(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Prodotto prodotto = new Prodotto();
                    prodotto.setId(rs.getString("ID_Prodotto"));
                    prodotto.setMarchio(rs.getString("Marchio"));
                    prodotto.setNome(rs.getString("Nome"));
                    prodotto.setTipo(rs.getString("Tipo"));
                    prodotto.setStato(rs.getString("Stato"));
                    prodotto.setNumeroScatti(rs.getInt("Numero_scatti")); 
                    prodotto.setCondizioneCollezionistica(rs.getString("Condizione_collezionistica"));
                    prodotto.setSeriale(rs.getString("Seriale"));
                    prodotto.setPrezzo(rs.getDouble("Prezzo"));
                    prodotto.setIva(rs.getInt("IVA")); 
                    prodotto.setModelUrl(rs.getString("URL_Modello_3D"));
                    prodotto.setImageUrl(rs.getString("URL_Immagine_Copertina"));
                    prodotto.setDescrizione(rs.getString("Descrizione"));
                    prodotto.setInStock(rs.getInt("In_stock")); 

                    return prodotto;
                }
                else return null;
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } finally { 
            if (ps != null) try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) { ConnessioneDB.releaseConnection(conn); } 
        }
        return null;
    }
    
    public List<Prodotto> fetchAllProdotti() throws SQLException {
        String query = "SELECT * FROM Prodotto";
        List<Prodotto> prodotti = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prodotto prodotto = new Prodotto();
                    prodotto.setId(rs.getString("ID_Prodotto"));
                    prodotto.setMarchio(rs.getString("Marchio"));
                    prodotto.setNome(rs.getString("Nome"));
                    prodotto.setTipo(rs.getString("Tipo"));
                    prodotto.setStato(rs.getString("Stato"));
                    prodotto.setNumeroScatti(rs.getInt("Numero_scatti"));
                    prodotto.setCondizioneCollezionistica(rs.getString("Condizione_collezionistica"));
                    prodotto.setSeriale(rs.getString("Seriale"));
                    prodotto.setPrezzo(rs.getDouble("Prezzo"));
                    prodotto.setIva(rs.getInt("IVA"));
                    prodotto.setModelUrl(rs.getString("URL_Modello_3D"));
                    prodotto.setImageUrl(rs.getString("URL_Immagine_Copertina"));
                    prodotto.setDescrizione(rs.getString("Descrizione"));
                    prodotto.setInStock(rs.getInt("In_stock"));
                    prodotti.add(prodotto);
                }
            }
        } finally {
            if (ps != null) ps.close();
            if (conn != null) ConnessioneDB.releaseConnection(conn); // Allineato al Connection Pool
        }
        return prodotti;
    }

    public void insertProdotto(Prodotto p) throws SQLException {
        // Query modificata per includere esplicitamente la colonna IVA (14 parametri)
        String query = "INSERT INTO Prodotto (ID_Prodotto, Marchio, Seriale, Prezzo, URL_Modello_3D, URL_Immagine_Copertina, Descrizione, In_stock, Nome, Tipo, Stato, Numero_scatti, Condizione_collezionistica, IVA) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1, p.getId());
            ps.setString(2, p.getMarchio());
            ps.setString(3, p.getSeriale());
            ps.setDouble(4, p.getPrezzo()); 
            ps.setString(5, p.getModelUrl());
            ps.setString(6, p.getImageUrl());
            ps.setString(7, p.getDescrizione());
            ps.setInt(8, p.getInStock());
            ps.setString(9, p.getNome());
            ps.setString(10, p.getTipo());
            ps.setString(11, p.getStato());
            
            if (p.getNumeroScatti() == 0) {
                ps.setNull(12, Types.INTEGER);
            } else {
                ps.setInt(12, p.getNumeroScatti());
            }
            ps.setString(13, p.getCondizioneCollezionistica());
            ps.setInt(14, p.getIva()); // Parametro IVA aggiunto
            
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
            if (conn != null) ConnessioneDB.releaseConnection(conn); // Allineato al Connection Pool
        }
    }

    public void deleteProdotto(String idProdotto) throws SQLException {
        String query = "DELETE FROM Prodotto WHERE ID_Prodotto = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, idProdotto);
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
            if (conn != null) ConnessioneDB.releaseConnection(conn); // Allineato al Connection Pool
        }
    }
}