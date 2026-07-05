package reframe.model.dao;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

import reframe.model.beans.DettaglioOrdine;
import reframe.model.beans.Ordine;
import reframe.utils.ConnessioneDB;

public class OrdineDAO {

    /* CREAZIONE ORDINE TRANSAZIONALE */
    
    // Esegue l'inserimento della testata dell'ordine, dei relativi dettagli e l'aggiornamento dello stock in un'unica transazione atomica.
    // L'uso di setAutoCommit(false) garantisce le proprietà ACID: in caso di eccezione su una qualsiasi query, viene eseguito il rollback totale per mantenere l'integrità del database.
    public void insertOrdineCompleto(Ordine ordine) throws SQLException {
        String queryOrdine = "INSERT INTO Ordine (ID_ordine, Data_ordine, Totale, Garanzia, Stato, ID_Utente, ID_Pagamento, ID_Spedizione) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String queryDettaglio = "INSERT INTO Ordine_Prodotto (ID_Ordine, ID_Prodotto, Prezzo_acquisto, Quantita_acquisto, Nome_prodotto_acquisto, IVA_acquisto) VALUES (?, ?, ?, ?, ?, ?)";
        String queryUpdateStock = "UPDATE Prodotto SET In_stock = In_stock - ? WHERE ID_prodotto = ?";
        
        Connection conn = null;
        PreparedStatement psOrdine = null;
        PreparedStatement psDettaglio = null;
        PreparedStatement psUpdateStock = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            conn.setAutoCommit(false);
            
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
            
            psDettaglio = conn.prepareStatement(queryDettaglio);
            psUpdateStock = conn.prepareStatement(queryUpdateStock);
            
            for (DettaglioOrdine dettaglio : ordine.getDettagli()) {
                psDettaglio.setString(1, ordine.getIdOrdine()); 
                psDettaglio.setString(2, dettaglio.getIdProdotto());
                psDettaglio.setDouble(3, dettaglio.getPrezzoAcquisto()); 
                psDettaglio.setInt(4, dettaglio.getQuantitaAcquisto());
                psDettaglio.setString(5, dettaglio.getNomeProdottoAcquisto());
                psDettaglio.setInt(6, dettaglio.getIvaAcquisto());       
                psDettaglio.executeUpdate();
                
                psUpdateStock.setInt(1, dettaglio.getQuantitaAcquisto());
                psUpdateStock.setString(2, dettaglio.getIdProdotto());
                psUpdateStock.executeUpdate();
            }
            
            conn.commit();
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transazione fallita. Eseguito Rollback dell'ordine: " + ordine.getIdOrdine());
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e; 
            
        } finally {
            try {
                if (psUpdateStock != null) psUpdateStock.close();
                if (psDettaglio != null) psDettaglio.close();
                if (psOrdine != null) psOrdine.close();
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) {
                try { 
                    conn.setAutoCommit(true); 
                } catch (SQLException e) { e.printStackTrace(); }
                ConnessioneDB.releaseConnection(conn); 
            }
        }
    }

    /* RECUPERO DATI ORDINE */
    
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
            try {
                if (rsDettagli != null) rsDettagli.close();
                if (psDettagli != null) psDettagli.close();
                if (rsOrdine != null) rsOrdine.close();
                if (psOrdine != null) psOrdine.close();
            } catch (SQLException e) { e.printStackTrace(); }

            if (conn != null) { ConnessioneDB.releaseConnection(conn); }
        }
        return ordine;
    }
    
    // Ricostruisce lo storico completo lato utente eseguendo un'interrogazione gerarchica: prima recupera le testate degli ordini e successivamente itera per popolarne i dettagli.
    public List<Ordine> getOrdiniCompletiByUtente(String username) throws SQLException {
        List<Ordine> lista = new ArrayList<>();
        String queryOrdini = "SELECT * FROM Ordine WHERE ID_Utente = ? ORDER BY Data_ordine DESC";
        String queryDettagli = "SELECT * FROM Ordine_Prodotto WHERE ID_Ordine = ?";

        Connection conn = null;
        PreparedStatement psOrdini = null;
        PreparedStatement psDettagli = null;
        ResultSet rsOrdini = null;

        try {
            conn = ConnessioneDB.getConnection();
            psOrdini = conn.prepareStatement(queryOrdini);
            psOrdini.setString(1, username);
            
            rsOrdini = psOrdini.executeQuery();
            while (rsOrdini.next()) {
                Ordine ord = new Ordine();
                ord.setIdOrdine(rsOrdini.getString("ID_ordine"));
                ord.setDataOrdine(rsOrdini.getDate("Data_ordine"));
                ord.setTotale(rsOrdini.getDouble("Totale"));
                ord.setStato(rsOrdini.getString("Stato"));

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
            
        } finally {
            try {
                if (rsOrdini != null) rsOrdini.close();
                if (psDettagli != null) psDettagli.close();
                if (psOrdini != null) psOrdini.close();
            } catch (SQLException e) { e.printStackTrace(); }

            if (conn != null) { ConnessioneDB.releaseConnection(conn); }
        }
        return lista;
    }
    
    /* FILTRAGGIO E AMMINISTRAZIONE ORDINI */

    // Implementa la costruzione dinamica della query SQL tramite StringBuilder per supportare l'applicazione opzionale e cumulativa di filtri di ricerca (utente e range di date).
    public List<Ordine> getOrdiniFiltrati(String emailCliente, java.sql.Date dataInizio, java.sql.Date dataFine) throws SQLException {
        List<Ordine> lista = new ArrayList<>();
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

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query.toString());
            
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
            
            rs = ps.executeQuery();
            while (rs.next()) {
                Ordine ord = new Ordine();
                ord.setIdOrdine(rs.getString("ID_ordine"));
                ord.setDataOrdine(rs.getDate("Data_ordine"));
                ord.setTotale(rs.getDouble("Totale"));
                ord.setStato(rs.getString("Stato"));
                ord.setIdUtente(rs.getString("ID_Utente"));
                lista.add(ord);
            }
            
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); }
        }
        return lista;
    }
    
    /* GESTIONE STATO ORDINE */
    
    public void updateStato(String idOrdine, String nuovoStato) throws SQLException {
        String query = "UPDATE Ordine SET Stato = ? WHERE ID_Ordine = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, nuovoStato);
            ps.setString(2, idOrdine);
            ps.executeUpdate();
            
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) { e.printStackTrace(); }
            
            if (conn != null) { ConnessioneDB.releaseConnection(conn); }
        }
    }
}