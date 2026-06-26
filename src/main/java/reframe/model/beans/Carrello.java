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

 // 1. Calcola SOLO il totale dei prodotti (Subtotale)
    public double getSubtotaleProdotti() {
        double totale = 0;
        for (CarrelloItem item : items) {
            totale += item.getPrezzoTotale();
        }
        return totale;
    }

    // 2. Costo fisso di spedizione (0 se il carrello è vuoto)
    public double getCostoSpedizione() {
        if (items.isEmpty()) return 0.0;
        return 5.00; // Costo fisso 5 Euro
    }

    // 3. Totale complessivo da pagare (Prodotti + Spedizione)
    public double getTotaleComplessivo() {
        return getSubtotaleProdotti() + getCostoSpedizione();
    }
    
    // N.B. Per compatibilità con i codici precedenti, se hai ancora getTotale() in giro, mantienilo facendogli restituire il totale complessivo:
    public double getTotale() {
        return getTotaleComplessivo();
    }
    public int getTotaleArticoli() {
        int totale = 0;
        for (CarrelloItem item : this.items) { // Assicurati che 'this.items' sia il nome della tua lista
            totale += item.getQuantita();
        }
        return totale;
    }
    
    public void svuota() {
        items.clear();
    }
}