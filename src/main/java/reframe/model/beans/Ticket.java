package reframe.model.beans;

import java.sql.Timestamp;

public class Ticket {
    private String idTicket;
    private String titolo;       // Nuovo
    private String categoria;    // Nuovo
    private String testoMessaggio;
    private Timestamp dataTicket;
    private String rmaPratica;
    private String autoreMessaggio;

    public Ticket() {}

    // Getter e Setter
    public String getIdTicket() { return idTicket; }
    public void setIdTicket(String idTicket) { this.idTicket = idTicket; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getTestoMessaggio() { return testoMessaggio; }
    public void setTestoMessaggio(String testoMessaggio) { this.testoMessaggio = testoMessaggio; }

    public Timestamp getDataTicket() { return dataTicket; }
    public void setDataTicket(Timestamp dataTicket) { this.dataTicket = dataTicket; }

    public String getRmaPratica() { return rmaPratica; }
    public void setRmaPratica(String rmaPratica) { this.rmaPratica = rmaPratica; }

    public String getAutoreMessaggio() { return autoreMessaggio; }
    public void setAutoreMessaggio(String autoreMessaggio) { this.autoreMessaggio = autoreMessaggio; }


    @Override
    public String toString() {
        return "Ticket{" +
                "idTicket='" + idTicket + '\'' +
                ", titolo='" + titolo + '\'' +
                ", categoria='" + categoria + '\'' +
                ", testoMessaggio='" + testoMessaggio + '\'' +
                ", dataTicket=" + dataTicket +
                ", rmaPratica='" + rmaPratica + '\'' +
                ", autoreMessaggio='" + autoreMessaggio + '\'' +
                '}';
    }
}