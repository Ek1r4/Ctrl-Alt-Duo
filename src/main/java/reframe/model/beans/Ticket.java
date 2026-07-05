package reframe.model.beans;

import java.sql.Timestamp;

public class Ticket {
    
    /* ATTRIBUTI */
    private String idTicket;
    private String rmaPratica;
    private String autore;
    
    // Vincolato a livello di database tramite tipo ENUM ('User' o 'Admin') per classificare i permessi in fase di lettura
    private String tipo;      
    private String messaggio;
    
    // Mappato con java.sql.Timestamp per garantire l'integrità del salvataggio dei valori DATETIME/TIMESTAMP sul database
    private Timestamp dataTicket;

    /* COSTRUTTORI */
    public Ticket() {
    }

    /* GETTER E SETTER */
    public String getIdTicket() { return idTicket; }
    public void setIdTicket(String idTicket) { this.idTicket = idTicket; }

    public String getRmaPratica() { return rmaPratica; }
    public void setRmaPratica(String rmaPratica) { this.rmaPratica = rmaPratica; }

    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMessaggio() { return messaggio; }
    public void setMessaggio(String messaggio) { this.messaggio = messaggio; }

    public Timestamp getDataTicket() { return dataTicket; }
    public void setDataTicket(Timestamp dataTicket) { this.dataTicket = dataTicket; }
}