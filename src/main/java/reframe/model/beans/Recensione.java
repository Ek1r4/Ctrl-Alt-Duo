package reframe.model.beans;

public class Recensione {
    
    /* ATTRIBUTI */
    private String idRecensione;
    private String descrizione;
    private double rating;
    private String idProdotto;
    private String idUtente;

    /* COSTRUTTORI */
    public Recensione() {}

    /* GETTER E SETTER */
    public String getIdRecensione() { return idRecensione; }
    public void setIdRecensione(String idRecensione) { this.idRecensione = idRecensione; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getIdProdotto() { return idProdotto; }
    public void setIdProdotto(String idProdotto) { this.idProdotto = idProdotto; }

    public String getIdUtente() { return idUtente; }
    public void setIdUtente(String idUtente) { this.idUtente = idUtente; }
}