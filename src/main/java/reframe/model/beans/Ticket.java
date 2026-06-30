package reframe.model.beans;

import java.sql.Timestamp;

public class Ticket {
    private String idTicket;
    private String testoMessaggio;
    private Timestamp dataTicket;
    private String rmaPratica;
    private String autoreMessaggio;

    public Ticket() {
    }

    // Getters
    public String getIdTicket() { return idTicket; }
    public String getTestoMessaggio() { return testoMessaggio; }
    public Timestamp getDataTicket() { return dataTicket; }
    public String getRmaPratica() { return rmaPratica; }
    public String getAutoreMessaggio() { return autoreMessaggio; }

    // Setters
    public void setIdTicket(String idTicket) { this.idTicket = idTicket; }
    public void setTestoMessaggio(String testoMessaggio) { this.testoMessaggio = testoMessaggio; }
    public void setDataTicket(Timestamp dataTicket) { this.dataTicket = dataTicket; }
    public void setRmaPratica(String rmaPratica) { this.rmaPratica = rmaPratica; }
    public void setAutoreMessaggio(String autoreMessaggio) { this.autoreMessaggio = autoreMessaggio; }

    @Override
    public String toString() {
        return "Ticket{" +
                "idTicket='" + idTicket + '\'' +
                ", testoMessaggio='" + testoMessaggio + '\'' +
                ", dataTicket=" + dataTicket +
                ", rmaPratica='" + rmaPratica + '\'' +
                ", autoreMessaggio='" + autoreMessaggio + '\'' +
                '}';
    }
}