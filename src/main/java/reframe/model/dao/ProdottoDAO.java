package reframe.model.dao;

import reframe.model.beans.Prodotto;
import reframe.utils.*;
import java.sql.*;
import java.util.*;

public class ProdottoDAO {

    // METODO DI UTILITÀ: Mappa la riga corrente del ResultSet in un oggetto Prodotto
    private Prodotto estraiProdotto(ResultSet rs) throws SQLException {
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
        
        // Mappatura del campo per il Soft Delete
        prodotto.setAttivo(rs.getBoolean("isAttivo")); 
        
        return prodotto;
    }
    
    public List<Prodotto> fetchProdottiByTipo(String tipo) throws SQLException {
        // FILTRO SOFT DELETE: Mostra solo i prodotti attivi
        String query = "SELECT * FROM Prodotto WHERE Tipo = ? AND isAttivo = true";
        List<Prodotto> prodottiTrovati = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection(); 
            ps = conn.prepareStatement(query);
            ps.setString(1, tipo);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prodottiTrovati.add(estraiProdotto(rs));
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
        // NESSUN FILTRO SOFT DELETE: Serve per far funzionare i vecchi ordini e il pannello admin
        String query = "SELECT * FROM Prodotto WHERE ID_Prodotto = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection(); 
            ps = conn.prepareStatement(query);
            ps.setString(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return estraiProdotto(rs);
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
        // NESSUN FILTRO SOFT DELETE: L'Admin deve vedere tutto il catalogo
        String query = "SELECT * FROM Prodotto";
        List<Prodotto> prodotti = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prodotti.add(estraiProdotto(rs));
                }
            }
        } finally {
            if (ps != null) ps.close();
            if (conn != null) ConnessioneDB.releaseConnection(conn);
        }
        return prodotti;
    }
    
    public List<Prodotto> fetchProdottiPerAdmin(String parametro) throws SQLException {
        // Cerca corrispondenze nel nome, marchio o seriale (inclusi i prodotti oscurati)
        String query = "SELECT * FROM Prodotto WHERE nome LIKE ? OR marchio LIKE ? OR seriale LIKE ?";
        List<Prodotto> prodottiTrovati = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection(); 
            ps = conn.prepareStatement(query);
            
            // Creiamo il pattern per il LIKE aggiungendo i % prima e dopo la parola cercata
            String searchPattern = "%" + parametro + "%";
            
            // Settiamo lo stesso pattern per tutti e tre i campi di ricerca
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prodottiTrovati.add(estraiProdotto(rs));
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

    public void insertProdotto(Prodotto p) throws SQLException {
        // Il campo isAttivo sarà automaticamente TRUE grazie al DEFAULT impostato nel database
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
            ps.setInt(14, p.getIva()); 
            
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
            if (conn != null) ConnessioneDB.releaseConnection(conn); 
        }
    }
    
    public boolean updateProdotto(Prodotto p) throws SQLException {
        String query = "UPDATE Prodotto SET Marchio = ?, Seriale = ?, Prezzo = ?, URL_Modello_3D = ?, " +
                       "URL_Immagine_Copertina = ?, Descrizione = ?, In_stock = ?, Nome = ?, Tipo = ?, " +
                       "Stato = ?, Numero_scatti = ?, Condizione_collezionistica = ?, IVA = ? " +
                       "WHERE ID_Prodotto = ?";
                       
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1, p.getMarchio());
            ps.setString(2, p.getSeriale());
            ps.setDouble(3, p.getPrezzo()); 
            ps.setString(4, p.getModelUrl());
            ps.setString(5, p.getImageUrl());
            ps.setString(6, p.getDescrizione());
            ps.setInt(7, p.getInStock());
            ps.setString(8, p.getNome());
            ps.setString(9, p.getTipo());
            ps.setString(10, p.getStato());
            
            if (p.getNumeroScatti() == 0) {
                ps.setNull(11, Types.INTEGER);
            } else {
                ps.setInt(11, p.getNumeroScatti());
            }
            
            ps.setString(12, p.getCondizioneCollezionistica());
            ps.setInt(13, p.getIva());
            ps.setString(14, p.getId());
            
            int row = ps.executeUpdate();
            return row > 0;
            
        } finally {
            if (ps != null) ps.close();
            if (conn != null) ConnessioneDB.releaseConnection(conn); 
        }
    }

    public void deleteProdotto(String idProdotto) throws SQLException {
        // TRASFORMATO IN SOFT DELETE: Aggiorna lo stato invece di eliminare la riga
        String query = "UPDATE Prodotto SET isAttivo = false WHERE ID_Prodotto = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, idProdotto);
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
            if (conn != null) ConnessioneDB.releaseConnection(conn); 
        }
    }
    
    public List<String> fetchDistinctMarche() throws SQLException {
        // FILTRO SOFT DELETE: Non mostrare le marche se tutti i loro prodotti sono stati oscurati
        List<String> marche = new ArrayList<>();
        String query = "SELECT DISTINCT Marchio FROM Prodotto WHERE isAttivo = true ORDER BY Marchio ASC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    marche.add(rs.getString("Marchio"));
                }
            }
        } finally {
            if (ps != null) ps.close();
            if (conn != null) ConnessioneDB.releaseConnection(conn);
        }
        return marche;
    }
    
    public List<Prodotto> fetchProdottiFiltrati(String[] marche, String[] fascePrezzo, String search, String tipo) throws SQLException {
        List<Prodotto> prodotti = new ArrayList<>();
        
        // FILTRO SOFT DELETE: La base della query parte solo dai prodotti attivi
        StringBuilder query = new StringBuilder("SELECT * FROM Prodotto WHERE isAttivo = true ");

        // 1. Costruzione dinamica per la Categoria (Tipo)
        if (tipo != null && !tipo.trim().isEmpty()) {
            query.append("AND Tipo = ? ");
        }

        // 2. Costruzione dinamica per i Marchi
        if (marche != null && marche.length > 0) {
            query.append("AND Marchio IN (");
            for (int i = 0; i < marche.length; i++) {
                query.append("?");
                if (i < marche.length - 1) query.append(",");
            }
            query.append(") ");
        }

        // 3. Costruzione dinamica per i Prezzi
        if (fascePrezzo != null && fascePrezzo.length > 0) {
            query.append("AND (");
            for (int i = 0; i < fascePrezzo.length; i++) {
                switch (fascePrezzo[i]) {
                    case "0-500": query.append("(Prezzo BETWEEN 0 AND 500)"); break;
                    case "500-1000": query.append("(Prezzo BETWEEN 500.01 AND 1000)"); break;
                    case "1000-2000": query.append("(Prezzo BETWEEN 1000.01 AND 2000)"); break;
                    case "2000-max": query.append("(Prezzo > 2000)"); break;
                }
                if (i < fascePrezzo.length - 1) query.append(" OR ");
            }
            query.append(") ");
        }

        // 4. Costruzione dinamica per la Barra di Ricerca
        boolean hasSearch = (search != null && !search.trim().isEmpty());
        if (hasSearch) {
            query.append("AND (LOWER(Nome) LIKE ? OR LOWER(Marchio) LIKE ? OR LOWER(Seriale) LIKE ?) ");
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query.toString());

            int paramIndex = 1;
            
            if (tipo != null && !tipo.trim().isEmpty()) {
                ps.setString(paramIndex++, tipo);
            }
            
            if (marche != null && marche.length > 0) {
                for (String marca : marche) {
                    ps.setString(paramIndex++, marca);
                }
            }
            
            if (hasSearch) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%"; 
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern); 
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prodotti.add(estraiProdotto(rs)); 
                }
            }
        } finally {
            if (ps != null) ps.close();
            if (conn != null) ConnessioneDB.releaseConnection(conn);
        }
        return prodotti;
    }
    
    public void ripristinaProdotto(String idProdotto) throws SQLException {
        // Riporta isAttivo a true per far ricomparire il prodotto in vetrina
        String query = "UPDATE Prodotto SET isAttivo = true WHERE ID_Prodotto = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, idProdotto);
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
            if (conn != null) ConnessioneDB.releaseConnection(conn); 
        }
    }
}