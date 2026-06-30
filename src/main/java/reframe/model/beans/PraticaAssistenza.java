package reframe.model.beans;

import java.sql.Timestamp;

public class PraticaAssistenza {
    private String rma;
    private String stato;
    private String motivo;
    private Timestamp dataApertura;
    private Timestamp dataChiusura;
    private String idOrdine;
    private String idUtente;
    private String adminInCarico; // NUOVO CAMPO

    public PraticaAssistenza() {
    }

    // Getters
    public String getRma() { return rma; }
    public String getStato() { return stato; }
    public String getMotivo() { return motivo; }
    public Timestamp getDataApertura() { return dataApertura; }
    public Timestamp getDataChiusura() { return dataChiusura; }
    public String getIdOrdine() { return idOrdine; }
    public String getIdUtente() { return idUtente; }
    public String getAdminInCarico() { return adminInCarico; }

    // Setters
    public void setRma(String rma) { this.rma = rma; }
    public void setStato(String stato) { this.stato = stato; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public void setDataApertura(Timestamp dataApertura) { this.dataApertura = dataApertura; }
    public void setDataChiusura(Timestamp dataChiusura) { this.dataChiusura = dataChiusura; }
    public void setIdOrdine(String idOrdine) { this.idOrdine = idOrdine; }
    public void setIdUtente(String idUtente) { this.idUtente = idUtente; }
    public void setAdminInCarico(String adminInCarico) { this.adminInCarico = adminInCarico; }

    @Override
    public String toString() {
        return "PraticaAssistenza{" +
                "rma='" + rma + '\'' +
                ", stato='" + stato + '\'' +
                ", motivo='" + motivo + '\'' +
                ", dataApertura=" + dataApertura +
                ", dataChiusura=" + dataChiusura +
                ", idOrdine='" + idOrdine + '\'' +
                ", idUtente='" + idUtente + '\'' +
                ", adminInCarico='" + adminInCarico + '\'' +
                '}';
    }
}