package reframe.model.beans;

public class Pagamento {
    
    private int idPagamento;
    private String idUtente; //Username dell'Utente
    private String nomeIntestatario;
    private String circuito;
    private String numeroCarta;
    private String dataScadenza;
    private String cvv;
    private boolean isAttivo;

    // Costruttore vuoto
    public Pagamento() {
    }

    // Costruttore pieno
    public Pagamento(int idPagamento, String idUtente, String nomeIntestatario, String circuito, 
                     String numeroCarta, String dataScadenza, String cvv) {
        this.idPagamento = idPagamento;
        this.idUtente = idUtente;
        this.nomeIntestatario = nomeIntestatario;
        this.circuito = circuito;
        this.numeroCarta = numeroCarta;
        this.dataScadenza = dataScadenza;
        this.cvv = cvv;
    }

    // Getter e Setter
    public int getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(int idPagamento) {
        this.idPagamento = idPagamento;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    public String getNomeIntestatario() {
        return nomeIntestatario;
    }

    public void setNomeIntestatario(String nomeIntestatario) {
        this.nomeIntestatario = nomeIntestatario;
    }

    public String getCircuito() {
        return circuito;
    }

    public void setCircuito(String circuito) {
        this.circuito = circuito;
    }

    public String getNumeroCarta() {
        return numeroCarta;
    }

    public void setNumeroCarta(String numeroCarta) {
        this.numeroCarta = numeroCarta;
    }

    public String getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(String dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
    
    public boolean isAttivo() {
        return isAttivo;
    }

    public void setAttivo(boolean isAttivo) {
        this.isAttivo = isAttivo;
    }

    // Override del toString
    @Override
    public String toString() {
        return "Pagamento [idPagamento=" + idPagamento + ", idUtente=" + idUtente 
                + ", nomeIntestatario=" + nomeIntestatario + ", circuito=" + circuito 
                + ", numeroCarta=" + numeroCarta + ", dataScadenza=" + dataScadenza 
                + ", cvv=" + cvv + "]";
    }
}