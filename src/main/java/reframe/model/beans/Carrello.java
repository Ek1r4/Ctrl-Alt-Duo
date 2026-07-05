package reframe.model.beans;

import java.util.ArrayList;
import java.util.List;

public class Carrello {
    private List<CarrelloItem> items;

    /* INIZIALIZZAZIONE */
    public Carrello() {
        this.items = new ArrayList<>();
    }

    public List<CarrelloItem> getItems() {
        return items;
    }

    /* GESTIONE ELEMENTI DEL CARRELLO */
    public void aggiungiProdotto(CarrelloItem nuovoItem) {
        // Verifica la presenza del prodotto: se già presente, ne incrementa unicamente la quantità
        for (CarrelloItem item : items) {
            if (item.getIdProdotto().equals(nuovoItem.getIdProdotto())) {
                item.setQuantita(item.getQuantita() + nuovoItem.getQuantita());
                return;
            }
        }
        items.add(nuovoItem);
    }

    public void rimuoviProdotto(String idProdotto) {
        items.removeIf(item -> item.getIdProdotto().equals(idProdotto));
    }

    public void aggiornaQuantita(String idProdotto, int nuovaQuantita) {
        for (CarrelloItem item : items) {
            if (item.getIdProdotto().equals(idProdotto)) {
                // Eliminazione implicita del prodotto: se la quantità aggiornata scende a 0 o meno, l'item viene rimosso dal carrello
                if (nuovaQuantita > 0) {
                    item.setQuantita(nuovaQuantita);
                } else {
                    rimuoviProdotto(idProdotto);
                }
                return;
            }
        }
    }

    /* CALCOLO TOTALI E SPEDIZIONE */
    public double getSubtotaleProdotti() {
        double totale = 0;
        for (CarrelloItem item : items) {
            totale += item.getPrezzoTotale();
        }
        return totale;
    }

    public double getCostoSpedizione() {
        if (items.isEmpty()) return 0.0;
        return 5.00; 
    }

    public double getTotaleComplessivo() {
        return getSubtotaleProdotti() + getCostoSpedizione();
    }
    
    public double getTotale() {
        return getTotaleComplessivo();
    }

    /* UTILITY */
    public int getTotaleArticoli() {
        int totale = 0;
        for (CarrelloItem item : this.items) { 
            totale += item.getQuantita();
        }
        return totale;
    }
    
    public void svuota() {
        items.clear();
    }
}