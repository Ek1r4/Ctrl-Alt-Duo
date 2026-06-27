package reframe.model.dao;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

import reframe.model.beans.DettaglioOrdine;
import reframe.model.beans.Ordine;
import reframe.utils.ConnessioneDB;

public class OrdineDAO {

	/**
     * Inserisce un nuovo ordine completo (Testata + Dettagli) 
     * e scala lo stock dei prodotti, tutto in una singola Transazione SQL.
     */
    public void insertOrdineCompleto(Ordine ordine) throws SQLException {
        // Query per la tabella Ordine
        String queryOrdine = "INSERT INTO Ordine (ID_ordine, Data_ordine, Totale, Garanzia, Stato, ID_Utente, ID_Pagamento, ID_Spedizione) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Query per la tabella Ordine_Prodotto
        String queryDettaglio = "INSERT INTO Ordine_Prodotto (ID_Ordine, ID_Prodotto, Prezzo_acquisto, Quantita_acquisto, Nome_prodotto_acquisto, IVA_acquisto) VALUES (?, ?, ?, ?, ?, ?)";
        
        String queryUpdateStock = "UPDATE Prodotto SET In_stock = In_stock - ? WHERE ID_prodotto = ?";
        
        Connection conn = null;
        PreparedStatement psOrdine = null;
        PreparedStatement psDettaglio = null;
        PreparedStatement psUpdateStock = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            
            // 1. INIZIO TRANSAZIONE: Disabilitiamo l'autocommit
            conn.setAutoCommit(false);
            
            // 2. INSERIMENTO TESTATA ORDINE
            psOrdine = conn.prepareStatement(queryOrdine);
            psOrdine.setString(1, ordine.getIdOrdine());
            psOrdine.setDate(2, ordine.getDataOrdine());
            psOrdine.setDouble(3, ordine.getTotale());
            psOrdine.setBoolean(4, ordine.isGaranzia());
            psOrdine.setString(5, ordine.getStato());
            psOrdine.setString(6, ordine.getIdUtente());
            psOrdine.setInt(7, ordine.getIdPagamento());
            psOrdine.setInt(8, ordine.getIdSpedizione());
            
            psOrdine.executeUpdate();
            
            // 3. INSERIMENTO DETTAGLI ORDINE E AGGIORNAMENTO STOCK
            psDettaglio = conn.prepareStatement(queryDettaglio);
            psUpdateStock = conn.prepareStatement(queryUpdateStock);
            
            for (DettaglioOrdine dettaglio : ordine.getDettagli()) {
                // A) Inserisce il dettaglio
                psDettaglio.setString(1, ordine.getIdOrdine()); 
                psDettaglio.setString(2, dettaglio.getIdProdotto());
                psDettaglio.setDouble(3, dettaglio.getPrezzoAcquisto()); 
                psDettaglio.setInt(4, dettaglio.getQuantitaAcquisto());
                psDettaglio.setString(5, dettaglio.getNomeProdottoAcquisto());
                psDettaglio.setInt(6, dettaglio.getIvaAcquisto());       
                psDettaglio.executeUpdate();
                
                // B) Sottrae la quantità appena acquistata dallo stock del Prodotto
                psUpdateStock.setInt(1, dettaglio.getQuantitaAcquisto());
                psUpdateStock.setString(2, dettaglio.getIdProdotto());
                psUpdateStock.executeUpdate();
            }
            
            // 4. CONFERMA TRANSAZIONE: Se arriviamo qui, tutto è andato bene!
            conn.commit();
            
        } catch (SQLException e) {
            // ERRORE: Annulliamo tutto (Rollback) per evitare dati a metà
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transazione fallita. Eseguito Rollback dell'ordine: " + ordine.getIdOrdine());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Rilanciamo l'eccezione per farla catturare dalla Servlet
            
        } finally {
            // Chiusura sicura delle risorse
            try {
                if (psUpdateStock != null) psUpdateStock.close();
                if (psDettaglio != null) psDettaglio.close();
                if (psOrdine != null) psOrdine.close();
                if (conn != null) {
                    conn.setAutoCommit(true); 
                    conn.close();             
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
    public List<Ordine> getOrdiniCompletiByUtente(String username) throws SQLException {
        List<Ordine> lista = new ArrayList<>();
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
    
    public List<Ordine> getOrdiniFiltrati(String emailCliente, java.sql.Date dataInizio, java.sql.Date dataFine) throws SQLException {
        
    	List<Ordine> lista = new ArrayList<>();
        
        // Costruzione dinamica della query
        StringBuilder query = new StringBuilder("SELECT * FROM Ordine WHERE 1=1");
        
        if (emailCliente != null && !emailCliente.trim().isEmpty()) {
            query.append(" AND ID_Utente LIKE ?");
        }
        if (dataInizio != null) {
            query.append(" AND Data_ordine >= ?");
        }
        if (dataFine != null) {
            query.append(" AND Data_ordine <= ?");
        }
        query.append(" ORDER BY Data_ordine DESC");

        try (Connection conn = ConnessioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {
            
            int paramIndex = 1;
            if (emailCliente != null && !emailCliente.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + emailCliente + "%");
            }
            if (dataInizio != null) {
                ps.setDate(paramIndex++, dataInizio);
            }
            if (dataFine != null) {
                ps.setDate(paramIndex++, dataFine);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ordine ord = new Ordine();
                    ord.setIdOrdine(rs.getString("ID_ordine"));
                    ord.setDataOrdine(rs.getDate("Data_ordine"));
                    ord.setTotale(rs.getDouble("Totale"));
                    ord.setStato(rs.getString("Stato"));
                    ord.setIdUtente(rs.getString("ID_Utente"));
                    // (Opzionale: recuperare anche i dettagli interrogando Ordine_Prodotto come fatto in getOrdiniCompletiByUtente)
                    lista.add(ord);
                }
            }
        }
        return lista;
    }
    
    public void updateStato(String idOrdine, String nuovoStato) throws SQLException {
        String query = "UPDATE Ordine SET Stato = ? WHERE ID_Ordine = ?";
        try (Connection conn = ConnessioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nuovoStato);
            ps.setString(2, idOrdine);
            ps.executeUpdate();
        }
    }
}