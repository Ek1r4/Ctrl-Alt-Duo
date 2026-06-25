package reframe.model.dao;

import java.sql.*;

import reframe.model.beans.DettaglioOrdine;
import reframe.model.beans.Ordine;
import reframe.utils.ConnessioneDB;

public class OrdineDAO {

    /**
     * Inserisce un nuovo ordine completo (Testata + Dettagli) 
     * utilizzando una Transazione SQL per garantire l'integrità dei dati.
     */
    public void insertOrdineCompleto(Ordine ordine) throws SQLException {
        // Query per la tabella Ordine
        String queryOrdine = "INSERT INTO Ordine (ID_ordine, URL_fattura, Data_ordine, Totale, Garanzia, Stato, ID_Utente, ID_Pagamento, ID_Spedizione) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Query per la tabella Ordine_Prodotto (con campi IVA e Prezzo bloccati come da checklist)
        String queryDettaglio = "INSERT INTO Ordine_Prodotto (ID_Ordine, ID_Prodotto, Prezzo_acquisto, Quantita_acquisto, Nome_prodotto_acquisto, IVA_acquisto) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement psOrdine = null;
        PreparedStatement psDettaglio = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            
            // 1. INIZIO TRANSAZIONE: Disabilitiamo l'autocommit
            conn.setAutoCommit(false);
            
            // 2. INSERIMENTO TESTATA ORDINE
            psOrdine = conn.prepareStatement(queryOrdine);
            psOrdine.setString(1, ordine.getIdOrdine());
            psOrdine.setString(2, ordine.getUrlFattura());
            psOrdine.setDate(3, ordine.getDataOrdine());
            psOrdine.setDouble(4, ordine.getTotale());
            psOrdine.setBoolean(5, ordine.isGaranzia());
            psOrdine.setString(6, ordine.getStato());
            psOrdine.setString(7, ordine.getIdUtente());
            psOrdine.setInt(8, ordine.getIdPagamento());
            psOrdine.setInt(9, ordine.getIdSpedizione());
            
            psOrdine.executeUpdate();
            
            // 3. INSERIMENTO DETTAGLI ORDINE (Ciclo sui prodotti acquistati)
            psDettaglio = conn.prepareStatement(queryDettaglio);
            for (DettaglioOrdine dettaglio : ordine.getDettagli()) {
                psDettaglio.setString(1, ordine.getIdOrdine()); // Colleghiamo la riga all'ordine padre
                psDettaglio.setString(2, dettaglio.getIdProdotto());
                psDettaglio.setDouble(3, dettaglio.getPrezzoAcquisto()); // Integrità storica mantenuta!
                psDettaglio.setInt(4, dettaglio.getQuantitaAcquisto());
                psDettaglio.setString(5, dettaglio.getNomeProdottoAcquisto());
                psDettaglio.setInt(6, dettaglio.getIvaAcquisto());       // Integrità storica mantenuta!
                
                psDettaglio.executeUpdate();
            }
            
            // 4. CONFERMA TRANSAZIONE: Se arriviamo qui, tutto è andato bene!
            conn.commit();
            
        } catch (SQLException e) {
            // ERRORE: Annulliamo tutto quello che è stato fatto finora
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transazione fallita. Eseguito Rollback dell'ordine: " + ordine.getIdOrdine());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Rilanciamo l'eccezione per farla gestire al Controller
            
        } finally {
            // Chiusura sicura delle risorse e ripristino dell'autocommit
            try {
                if (psDettaglio != null) psDettaglio.close();
                if (psOrdine != null) psOrdine.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Ripristiniamo il comportamento di default
                    conn.close();             // Rimettiamo la connessione nel Pool
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
 // Recupera l'ordine e tutti i suoi prodotti per stampare la fattura
    public Ordine fetchOrdineById(String idOrdine) throws SQLException {
        String queryOrdine = "SELECT * FROM Ordine WHERE ID_ordine = ?";
        String queryDettagli = "SELECT * FROM Ordine_Prodotto WHERE ID_Ordine = ?";
        
        Connection conn = null;
        PreparedStatement psOrdine = null;
        PreparedStatement psDettagli = null;
        ResultSet rsOrdine = null;
        ResultSet rsDettagli = null;
        Ordine ordine = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            
            psOrdine = conn.prepareStatement(queryOrdine);
            psOrdine.setString(1, idOrdine);
            rsOrdine = psOrdine.executeQuery();
            
            if (rsOrdine.next()) {
                ordine = new Ordine();
                ordine.setIdOrdine(rsOrdine.getString("ID_ordine"));
                ordine.setUrlFattura(rsOrdine.getString("URL_fattura"));
                ordine.setDataOrdine(rsOrdine.getDate("Data_ordine"));
                ordine.setTotale(rsOrdine.getDouble("Totale"));
                ordine.setGaranzia(rsOrdine.getBoolean("Garanzia"));
                ordine.setStato(rsOrdine.getString("Stato"));
                ordine.setIdUtente(rsOrdine.getString("ID_Utente"));
                ordine.setIdPagamento(rsOrdine.getInt("ID_Pagamento"));
                ordine.setIdSpedizione(rsOrdine.getInt("ID_Spedizione"));
                
                psDettagli = conn.prepareStatement(queryDettagli);
                psDettagli.setString(1, idOrdine);
                rsDettagli = psDettagli.executeQuery();
                
                while (rsDettagli.next()) {
                    DettaglioOrdine dettaglio = new DettaglioOrdine();
                    dettaglio.setIdOrdine(idOrdine);
                    dettaglio.setIdProdotto(rsDettagli.getString("ID_Prodotto"));
                    dettaglio.setPrezzoAcquisto(rsDettagli.getDouble("Prezzo_acquisto"));
                    dettaglio.setQuantitaAcquisto(rsDettagli.getInt("Quantita_acquisto"));
                    dettaglio.setNomeProdottoAcquisto(rsDettagli.getString("Nome_prodotto_acquisto"));
                    dettaglio.setIvaAcquisto(rsDettagli.getInt("IVA_acquisto"));
                    ordine.addDettaglio(dettaglio);
                }
            }
        } finally {
            if (rsDettagli != null) rsDettagli.close();
            if (psDettagli != null) psDettagli.close();
            if (rsOrdine != null) rsOrdine.close();
            if (psOrdine != null) psOrdine.close();
            if (conn != null) conn.close();
        }
        return ordine;
    }
    
    /**
     * Recupera tutti gli ordini (con i relativi dettagli) di un utente specifico, 
     * ordinati dal più recente al più vecchio.
     */
    public java.util.List<Ordine> getOrdiniCompletiByUtente(String username) throws SQLException {
        java.util.List<Ordine> lista = new java.util.ArrayList<>();
        String queryOrdini = "SELECT * FROM Ordine WHERE ID_Utente = ? ORDER BY Data_ordine DESC";
        String queryDettagli = "SELECT * FROM Ordine_Prodotto WHERE ID_Ordine = ?";

        Connection conn = null;
        PreparedStatement psOrdini = null;
        PreparedStatement psDettagli = null;

        try {
            conn = ConnessioneDB.getConnection();
            psOrdini = conn.prepareStatement(queryOrdini);
            psOrdini.setString(1, username);
            
            try (ResultSet rsOrdini = psOrdini.executeQuery()) {
                while (rsOrdini.next()) {
                    Ordine ord = new Ordine();
                    ord.setIdOrdine(rsOrdini.getString("ID_ordine"));
                    ord.setDataOrdine(rsOrdini.getDate("Data_ordine"));
                    ord.setTotale(rsOrdini.getDouble("Totale"));
                    ord.setStato(rsOrdini.getString("Stato"));

                    // Recupera i prodotti per questo specifico ordine
                    psDettagli = conn.prepareStatement(queryDettagli);
                    psDettagli.setString(1, ord.getIdOrdine());
                    try (ResultSet rsDettagli = psDettagli.executeQuery()) {
                        while (rsDettagli.next()) {
                            DettaglioOrdine dett = new DettaglioOrdine();
                            dett.setNomeProdottoAcquisto(rsDettagli.getString("Nome_prodotto_acquisto"));
                            dett.setQuantitaAcquisto(rsDettagli.getInt("Quantita_acquisto"));
                            dett.setPrezzoAcquisto(rsDettagli.getDouble("Prezzo_acquisto"));
                            dett.setIvaAcquisto(rsDettagli.getInt("IVA_acquisto"));
                            ord.addDettaglio(dett);
                        }
                    }
                    psDettagli.close();
                    lista.add(ord);
                }
            }
        } finally {
            if (psDettagli != null) psDettagli.close();
            if (psOrdini != null) psOrdini.close();
            if (conn != null) conn.close();
        }
        return lista;
    }
}