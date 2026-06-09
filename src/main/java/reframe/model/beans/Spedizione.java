package reframe.model.beans;

public class Spedizione {
    
    private int idSpedizione;
    private String idUtente; //Username dell'Utente
    private String citta;
    private String provincia;
    private String paese;
    private String cap;
    private String via;
    private String civico;
    private String note;

    // Costruttore vuoto
    public Spedizione() {
    }

    // Costruttore pieno
    public Spedizione(int idSpedizione, String idUtente, String citta, String provincia, 
                      String paese, String cap, String via, String civico, String note) {
        this.idSpedizione = idSpedizione;
        this.idUtente = idUtente;
        this.citta = citta;
        this.provincia = provincia;
        this.paese = paese;
        this.cap = cap;
        this.via = via;
        this.civico = civico;
        this.note = note;
    }

    // Getter e Setter
    public int getIdSpedizione() {
        return idSpedizione;
    }

    public void setIdSpedizione(int idSpedizione) {
        this.idSpedizione = idSpedizione;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getPaese() {
        return paese;
    }

    public void setPaese(String paese) {
        this.paese = paese;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getCivico() {
        return civico;
    }

    public void setCivico(String civico) {
        this.civico = civico;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // Override del toString
    @Override
    public String toString() {
        return "Spedizione [idSpedizione=" + idSpedizione + ", idUtente=" + idUtente 
                + ", citta=" + citta + ", provincia=" + provincia + ", paese=" + paese 
                + ", cap=" + cap + ", via=" + via + ", civico=" + civico 
                + ", note=" + note + "]";
    }
}