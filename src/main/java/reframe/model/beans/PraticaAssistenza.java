package reframe.model.beans;

import java.sql.Timestamp;

public class PraticaAssistenza {
    
    /* ATTRIBUTI */
    private String rma;
    private String titolo;
    private String categoria;
    private String descrizione;
    private String stato;
    
    // Utilizzo di java.sql.Timestamp per garantire il mapping diretto e accurato dei campi DATETIME/TIMESTAMP a livello di database
    private Timestamp dataApertura;
    private Timestamp dataChiusura;
    
    private String idUtente;
    private String adminAssegnato;

    /* COSTRUTTORI */
    public PraticaAssistenza() {
    }

    /* GETTER E SETTER */
    public String getRma() { return rma; }
    public void setRma(String rma) { this.rma = rma; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public Timestamp getDataApertura() { return dataApertura; }
    public void setDataApertura(Timestamp dataApertura) { this.dataApertura = dataApertura; }

    public Timestamp getDataChiusura() { return dataChiusura; }
    public void setDataChiusura(Timestamp dataChiusura) { this.dataChiusura = dataChiusura; }

    public String getIdUtente() { return idUtente; }
    public void setIdUtente(String idUtente) { this.idUtente = idUtente; }

    public String getAdminAssegnato() { return adminAssegnato; }
    public void setAdminAssegnato(String adminAssegnato) { this.adminAssegnato = adminAssegnato; }
}