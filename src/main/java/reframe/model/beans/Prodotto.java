package reframe.model.beans;

import java.io.Serializable;

public class Prodotto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String marchio;
    private String nome;
    private String tipo;
    private String stato;
    private int numeroScatti;
    private String condizioneCollezionistica;
    private String seriale;
    private double prezzo; 
    private int iva = 22; // Default inizializzato a 22 in linea con il DB
    private String modelUrl;
    private String imageUrl;
    private String descrizione;
    private int inStock;

    public Prodotto() {
    }

    public Prodotto(String id, String marchio, String nome, String tipo, String stato, 
                          int numeroScatti, String condizioneCollezionistica, String seriale, 
                          double prezzo, int iva, String modelUrl, String imageUrl, 
                          String descrizione, int inStock) {
        this.id = id;
        this.marchio = marchio;
        this.nome = nome;
        this.tipo = tipo;
        this.stato = stato;
        this.numeroScatti = numeroScatti;
        this.condizioneCollezionistica = condizioneCollezionistica;
        this.seriale = seriale;
        this.prezzo = prezzo;
        this.iva = iva;
        this.modelUrl = modelUrl;
        this.imageUrl = imageUrl;
        this.descrizione = descrizione;
        this.inStock = inStock;
    }

    // --- GETTER E SETTER ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMarchio() { return marchio; }
    public void setMarchio(String marchio) { this.marchio = marchio; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public int getNumeroScatti() { return numeroScatti; }
    public void setNumeroScatti(int numeroScatti) { this.numeroScatti = numeroScatti; }

    public String getCondizioneCollezionistica() { return condizioneCollezionistica; }
    public void setCondizioneCollezionistica(String condizioneCollezionistica) { this.condizioneCollezionistica = condizioneCollezionistica; }

    public String getSeriale() { return seriale; }
    public void setSeriale(String seriale) { this.seriale = seriale; }

    public double getPrezzo() { return prezzo; }
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }

    public int getIva() { return iva; }
    public void setIva(int iva) { this.iva = iva; }

    public String getModelUrl() { return modelUrl; }
    public void setModelUrl(String modelUrl) { this.modelUrl = modelUrl; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public int getInStock() { return inStock; }
    public void setInStock(int inStock) { this.inStock = inStock; }

    // --- OVERRIDE toString ---
    @Override
    public String toString() {
        return "Prodotto{" +
                "id='" + id + '\'' +
                ", marchio='" + marchio + '\'' +
                ", nome='" + nome + '\'' +
                ", tipo='" + tipo + '\'' +
                ", stato='" + stato + '\'' +
                ", numeroScatti=" + numeroScatti +
                ", condizioneCollezionistica='" + condizioneCollezionistica + '\'' +
                ", seriale='" + seriale + '\'' +
                ", prezzo=" + prezzo +
                ", iva=" + iva +
                ", modelUrl='" + modelUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", inStock=" + inStock +
                '}';
    }
}