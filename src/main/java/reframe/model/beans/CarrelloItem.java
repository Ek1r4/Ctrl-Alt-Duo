package reframe.model.beans;

public class CarrelloItem {
    private String idProdotto;
    private String nome;
    private double prezzo;
    private int iva; // NUOVO CAMPO OBBLIGATORIO
    private int quantita;

    public CarrelloItem() {}

    public CarrelloItem(String idProdotto, String nome, double prezzo, int iva, int quantita) {
        this.idProdotto = idProdotto;
        this.nome = nome;
        this.prezzo = prezzo;
        this.iva = iva;
        this.quantita = quantita;
    }

    // Getter e Setter
    public String getIdProdotto() { return idProdotto; }
    public void setIdProdotto(String idProdotto) { this.idProdotto = idProdotto; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public double getPrezzo() { return prezzo; }
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }
    
    public int getIva() { return iva; }
    public void setIva(int iva) { this.iva = iva; }
    
    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }

 // Calcolo del totale per la riga: (Prezzo Singolo + IVA) * Quantità
    public double getPrezzoTotale() {
        // 1. Calcoliamo a quanto ammonta l'IVA in euro per il singolo prodotto
        double importoIva = this.prezzo * (this.iva / 100.0);
        
        // 2. Sommiamo l'IVA al prezzo netto
        double prezzoLordo = this.prezzo + importoIva;
        
        // 3. Moltiplichiamo per la quantità
        return prezzoLordo * this.quantita;
    }
}