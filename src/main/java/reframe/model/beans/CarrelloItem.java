package reframe.model.beans;

public class CarrelloItem {
    
    /* ATTRIBUTI */
    private String idProdotto;
    private String nome;
    private double prezzo;
    private int iva; 
    private int quantita;
    private int inStock;

    /* COSTRUTTORI */
    public CarrelloItem() {}

    public CarrelloItem(String idProdotto, String nome, double prezzo, int iva, int quantita, int inStock) {
        this.idProdotto = idProdotto;
        this.nome = nome;
        this.prezzo = prezzo;
        this.iva = iva;
        this.quantita = quantita;
        this.inStock = inStock;
    }

    /* GETTER E SETTER */
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
    
    public int getInStock() { return inStock; }
    public void setInStock(int inStock) { this.inStock = inStock; }

    /* LOGICA DI BUSINESS */
    
    // Calcola l'importo totale della riga ordine maggiorando dinamicamente il prezzo base netto con l'aliquota IVA
    public double getPrezzoTotale() {
        double importoIva = this.prezzo * (this.iva / 100.0);
        
        double prezzoLordo = this.prezzo + importoIva;
        
        return prezzoLordo * this.quantita;
    }
}