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
        return prodotto;
    }
    
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
    
 // UPDATE: Modifica un prodotto esistente nel database
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
            
            // Settaggio dei nuovi valori
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
            
            // Gestione dei campi numerici che potrebbero essere nulli
            if (p.getNumeroScatti() == 0) {
                ps.setNull(11, Types.INTEGER);
            } else {
                ps.setInt(11, p.getNumeroScatti());
            }
            
            ps.setString(12, p.getCondizioneCollezionistica());
            ps.setInt(13, p.getIva());
            
            // Condizione WHERE (ID del prodotto da modificare)
            ps.setString(14, p.getId());
            
            int row = ps.executeUpdate();
            return row > 0;
            
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
    
 // ==========================================================================
    // MAIN PER IL TESTING E DEBUG DEL DAO
    // ==========================================================================
    public static void main(String[] args) {
        ProdottoDAO dao = new ProdottoDAO();
        System.out.println("=== INIZIO COLLAUDO DAO PRODOTTI ===");

        try {
            // --- 1. TEST: insertProdotto ---
            System.out.println("\n[TEST 1] Inserimento nuovo prodotto...");
            Prodotto nuovo = new Prodotto();
            nuovo.setId("TEST01");
            nuovo.setMarchio("Minolta");
            nuovo.setNome("X-700");
            nuovo.setSeriale("MNL-998877");
            nuovo.setPrezzo(250.00);
            nuovo.setModelUrl("/assets/3d/minolta_x700.gltf");
            nuovo.setImageUrl("/assets/img/minolta_x700.jpg");
            nuovo.setDescrizione("Fotocamera analogica perfetta per iniziare il restauro.");
            nuovo.setInStock(5);
            nuovo.setTipo("Usato");
            nuovo.setStato("Ottimo");
            nuovo.setNumeroScatti(1500);
            nuovo.setCondizioneCollezionistica("Grado A");
            nuovo.setIva(22);

            dao.insertProdotto(nuovo);
            System.out.println("-> OK: Fotocamera inserita nel DB.");

            // --- 2. TEST: fetchProdottoById ---
            System.out.println("\n[TEST 2] Recupero prodotto tramite ID...");
            Prodotto recuperato = dao.fetchProdottoById("TEST01");
            if (recuperato != null) {
                System.out.println("-> OK: Trovato " + recuperato.getMarchio() + " " + recuperato.getNome());
                System.out.println("         Prezzo attuale: €" + recuperato.getPrezzo());
            } else {
                System.out.println("-> ERRORE: Prodotto non trovato!");
            }

            // --- 3. TEST: updateProdotto ---
            System.out.println("\n[TEST 3] Aggiornamento del prezzo e dello stock...");
            if (recuperato != null) {
                recuperato.setPrezzo(199.99); // Sconto
                recuperato.setInStock(2);     // Scorte diminuite
                boolean aggiornato = dao.updateProdotto(recuperato);
                System.out.println("-> OK: Prodotto aggiornato? " + aggiornato);
            }

            // --- 4. TEST: fetchProdottiByTipo ---
            System.out.println("\n[TEST 4] Recupero prodotti per tipo (Usato)...");
            List<Prodotto> ricondizionati = dao.fetchProdottiByTipo("Usato");
            System.out.println("-> OK: Trovati " + ricondizionati.size() + " prodotti di questa categoria.");

            // --- 5. TEST: fetchAllProdotti ---
            System.out.println("\n[TEST 5] Recupero intero catalogo...");
            List<Prodotto> catalogo = dao.fetchAllProdotti();
            System.out.println("-> OK: Il catalogo contiene attualmente " + catalogo.size() + " prodotti.");

            // --- 6. TEST: deleteProdotto ---
            System.out.println("\n[TEST 6] Eliminazione prodotto di test...");
            dao.deleteProdotto("TEST01");
            
            // Verifica finale
            Prodotto verificaEliminazione = dao.fetchProdottoById("TEST-CAM-01");
            if (verificaEliminazione == null) {
                System.out.println("-> OK: Prodotto eliminato definitivamente dal DB.");
            } else {
                System.out.println("-> ERRORE: Il prodotto esiste ancora!");
            }

            System.out.println("\n=== COLLAUDO COMPLETATO CON SUCCESSO! ===");

        } catch (Exception e) {
            System.out.println("\n[!] CRASH DURANTE IL TEST [!]");
            e.printStackTrace();
        }
    }
}