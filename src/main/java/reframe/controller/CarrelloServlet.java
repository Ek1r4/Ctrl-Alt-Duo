package reframe.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import reframe.model.beans.Carrello;
import reframe.model.beans.CarrelloItem;
import reframe.model.beans.Prodotto;
import reframe.model.dao.ProdottoDAO;

@WebServlet("/Carrello")
public class CarrelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Se si arriva tramite URL diretto, rimandiamo la richiesta al doPost per sicurezza
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        
        // Se il carrello non esiste in sessione, lo creiamo (Gestione sessioni OBBLIGATORIA)
        if (carrello == null) {
            carrello = new Carrello();
            session.setAttribute("carrello", carrello);
        }

        String action = request.getParameter("action");
        String idProdotto = request.getParameter("id");
        String isAjax = request.getParameter("ajax"); // Rileva se è una chiamata asincrona Fetch API

        if (action != null) {
            try {
                if ("add".equals(action)) {
                    int quantita = Integer.parseInt(request.getParameter("quantita"));
                    
                    // Recupero sicuro del prodotto reale e dei suoi prezzi dal database
                    ProdottoDAO prodottoDAO = new ProdottoDAO();
                    Prodotto prodottoScelto = prodottoDAO.fetchProdottoById(idProdotto);
                    
                    if (prodottoScelto != null && prodottoScelto.getInStock() >= quantita) {
                        carrello.aggiungiProdotto(new CarrelloItem(
                            prodottoScelto.getId(), 
                            prodottoScelto.getNome(), 
                            prodottoScelto.getPrezzo(), 
                            prodottoScelto.getIva(), 
                            quantita
                        ));
                        request.setAttribute("successMessage", "Prodotto aggiunto al carrello con successo!");
                    } else {
                        request.setAttribute("errorMessage", "Errore: Prodotto non trovato o quantità non disponibile.");
                    }
                    
                } else if ("remove".equals(action)) {
                    carrello.rimuoviProdotto(idProdotto);
                    request.setAttribute("successMessage", "Prodotto rimosso dal carrello.");
                    
                } else if ("update".equals(action)) {
                    int nuovaQuantita = Integer.parseInt(request.getParameter("quantita"));
                    carrello.aggiornaQuantita(idProdotto, nuovaQuantita);
                    request.setAttribute("successMessage", "Quantità aggiornata correttamente.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Si è verificato un errore durante l'operazione.");
            }
        }

        // --- BLOCCO AJAX: Risposta JSON per l'aggiornamento in tempo reale ---
        if ("true".equals(isAjax)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            double nuovoTotaleCarrello = carrello.getTotale();
            double nuovoTotaleRiga = 0;
            
            // Se stiamo aggiornando le quantità, calcoliamo il nuovo subtotale di quella specifica riga
            if ("update".equals(action)) {
                for (CarrelloItem item : carrello.getItems()) {
                    if (item.getIdProdotto().equals(idProdotto)) {
                        nuovoTotaleRiga = item.getPrezzoTotale();
                        break;
                    }
                }
            }

            // Costruzione manuale della stringa JSON senza usare librerie esterne non ammesse
            String jsonResponse = "{"
                + "\"status\":\"success\", "
                + "\"totaleCarrello\":" + String.valueOf(nuovoTotaleCarrello) + ", "
                + "\"totaleRiga\":" + String.valueOf(nuovoTotaleRiga)
                + "}";
                
            response.getWriter().write(jsonResponse);
            return; // Interrompiamo qui: niente forward alla JSP perché è una chiamata asincrona JavaScript!
        }

        // Se è una navigazione standard (es. aggiunta prodotto dal pulsante), ricarichiamo l'interfaccia
        request.getRequestDispatcher("/common/carrello.jsp").forward(request, response);
    }
}