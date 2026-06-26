package reframe.model.beans;

import java.io.Serializable;

public class DettaglioOrdine implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idOrdine;
    private String idProdotto;
    private double prezzoAcquisto; // Requisito OBBLIGATORIO Checklist: Integrità Storica
    private int quantitaAcquisto;
    private String nomeProdottoAcquisto;
    private int ivaAcquisto;       // Requisito OBBLIGATORIO Checklist: Integrità Storica

    public DettaglioOrdine() {
    }

    public DettaglioOrdine(String idOrdine, String idProdotto, double prezzoAcquisto, 
                           int quantitaAcquisto, String nomeProdottoAcquisto, int ivaAcquisto) {
        this.idOrdine = idOrdine;
        this.idProdotto = idProdotto;
        this.prezzoAcquisto = prezzoAcquisto;
        this.quantitaAcquisto = quantitaAcquisto;
        this.nomeProdottoAcquisto = nomeProdottoAcquisto;
        this.ivaAcquisto = ivaAcquisto;
    }

    // --- GETTER E SETTER ---

    public String getIdOrdine() { return idOrdine; }
    public void setIdOrdine(String idOrdine) { this.idOrdine = idOrdine; }

    public String getIdProdotto() { return idProdotto; }
    public void setIdProdotto(String idProdotto) { this.idProdotto = idProdotto; }

    public double getPrezzoAcquisto() { return prezzoAcquisto; }
    public void setPrezzoAcquisto(double prezzoAcquisto) { this.prezzoAcquisto = prezzoAcquisto; }

    public int getQuantitaAcquisto() { return quantitaAcquisto; }
    public void setQuantitaAcquisto(int quantitaAcquisto) { this.quantitaAcquisto = quantitaAcquisto; }

    public String getNomeProdottoAcquisto() { return nomeProdottoAcquisto; }
    public void setNomeProdottoAcquisto(String nomeProdottoAcquisto) { this.nomeProdottoAcquisto = nomeProdottoAcquisto; }

    public int getIvaAcquisto() { return ivaAcquisto; }
    public void setIvaAcquisto(int ivaAcquisto) { this.ivaAcquisto = ivaAcquisto; }
    
 // Metodo di utilità per calcolare il parziale della singola riga (IVA Inclusa)
    public double getTotaleRiga() {
        double importoIva = this.prezzoAcquisto * (this.ivaAcquisto / 100.0);
        double prezzoLordoAcquisto = this.prezzoAcquisto + importoIva;
        
        return prezzoLordoAcquisto * this.quantitaAcquisto;
    }
}