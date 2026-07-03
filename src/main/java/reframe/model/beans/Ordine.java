package reframe.model.beans;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Ordine implements Serializable {
    private static final long serialVersionUID = 1L;

    /* ATTRIBUTI BASE */
    
    private String idOrdine;
    private String urlFattura;
    private Date dataOrdine; 
    private double totale;
    private boolean garanzia;
    
    // Vincolato a livello di database tramite tipo ENUM ('In lavorazione', 'In consegna', 'Consegnato')
    private String stato;    
    
    /* CHIAVI ESTERNE E RELAZIONI */
    
    private String idUtente;
    private int idPagamento;
    private int idSpedizione;

    private List<DettaglioOrdine> dettagli;

    /* COSTRUTTORI */
    
    public Ordine() {
        this.dettagli = new ArrayList<>();
    }

    public Ordine(String idOrdine, String urlFattura, Date dataOrdine, double totale, 
                  boolean garanzia, String stato, String idUtente, int idPagamento, int idSpedizione) {
        this.idOrdine = idOrdine;
        this.urlFattura = urlFattura;
        this.dataOrdine = dataOrdine;
        this.totale = totale;
        this.garanzia = garanzia;
        this.stato = stato;
        this.idUtente = idUtente;
        this.idPagamento = idPagamento;
        this.idSpedizione = idSpedizione;
        this.dettagli = new ArrayList<>();
    }

    /* GETTER E SETTER */
    
    public String getIdOrdine() { return idOrdine; }
    public void setIdOrdine(String idOrdine) { this.idOrdine = idOrdine; }

    public String getUrlFattura() { return urlFattura; }
    public void setUrlFattura(String urlFattura) { this.urlFattura = urlFattura; }

    public Date getDataOrdine() { return dataOrdine; }
    public void setDataOrdine(Date dataOrdine) { this.dataOrdine = dataOrdine; }

    public double getTotale() { return totale; }
    public void setTotale(double totale) { this.totale = totale; }

    public boolean isGaranzia() { return garanzia; }
    public void setGaranzia(boolean garanzia) { this.garanzia = garanzia; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public String getIdUtente() { return idUtente; }
    public void setIdUtente(String idUtente) { this.idUtente = idUtente; }

    public int getIdPagamento() { return idPagamento; }
    public void setIdPagamento(int idPagamento) { this.idPagamento = idPagamento; }

    public int getIdSpedizione() { return idSpedizione; }
    public void setIdSpedizione(int idSpedizione) { this.idSpedizione = idSpedizione; }

    public List<DettaglioOrdine> getDettagli() { return dettagli; }
    public void setDettagli(List<DettaglioOrdine> dettagli) { this.dettagli = dettagli; }
    
    public void addDettaglio(DettaglioOrdine dettaglio) {
        this.dettagli.add(dettaglio);
    }
}