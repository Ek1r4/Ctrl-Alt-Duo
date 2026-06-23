package reframe.model.beans;

import java.util.ArrayList;
import java.util.List;

public class Carrello {
    private List<CarrelloItem> items;

    public Carrello() {
        this.items = new ArrayList<>();
    }

    public List<CarrelloItem> getItems() {
        return items;
    }

    // Aggiunge un prodotto o incrementa la quantità se già presente [cite: 20]
    public void aggiungiProdotto(CarrelloItem nuovoItem) {
        for (CarrelloItem item : items) {
            if (item.getIdProdotto().equals(nuovoItem.getIdProdotto())) {
                item.setQuantita(item.getQuantita() + nuovoItem.getQuantita());
                return;
            }
        }
        items.add(nuovoItem);
    }

    // Rimuove un prodotto [cite: 20]
    public void rimuoviProdotto(String idProdotto) {
        items.removeIf(item -> item.getIdProdotto().equals(idProdotto));
    }

    // Aggiorna la quantità esatta [cite: 20]
    public void aggiornaQuantita(String idProdotto, int nuovaQuantita) {
        for (CarrelloItem item : items) {
            if (item.getIdProdotto().equals(idProdotto)) {
                if (nuovaQuantita > 0) {
                    item.setQuantita(nuovaQuantita);
                } else {
                    rimuoviProdotto(idProdotto);
                }
                return;
            }
        }
    }

    // Calcola il totale del carrello
    public double getTotale() {
        double totale = 0;
        for (CarrelloItem item : items) {
            totale += item.getPrezzoTotale();
        }
        return totale;
    }
    
    public void svuota() {
        items.clear();
    }
}