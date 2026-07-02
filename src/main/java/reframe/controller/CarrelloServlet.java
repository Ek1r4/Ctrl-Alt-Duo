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

        if (action != null && idProdotto != null) {
            try {
                // RECUPERO DAL DATABASE: Interroghiamo subito il DB per avere lo stock reale aggiornato al millisecondo
                ProdottoDAO prodottoDAO = new ProdottoDAO();
                Prodotto prodottoScelto = prodottoDAO.fetchProdottoById(idProdotto);

                if ("add".equals(action)) {
                    int quantitaRichiesta = Integer.parseInt(request.getParameter("quantita"));
                    
                    // 1. Calcoliamo quanti pezzi di questo prodotto ci sono GIÀ nel carrello dell'utente
                    int quantitaGiaPresente = 0;
                    for (CarrelloItem item : carrello.getItems()) {
                        if (item.getIdProdotto().equals(idProdotto)) {
                            quantitaGiaPresente = item.getQuantita();
                            break;
                        }
                    }
                    
                    int quantitaTotale = quantitaGiaPresente + quantitaRichiesta;

                    if (prodottoScelto != null) {
                        // 2. CONTROLLO STOCK IN AGGIUNTA
                        if (quantitaTotale > prodottoScelto.getInStock()) {
                            
                            // Se la richiesta arriva tramite AJAX blocchiamo tutto e inviamo l'errore JSON
                            if ("true".equals(isAjax)) {
                                String jsonError = "{"
                                    + "\"status\":\"error\", "
                                    + "\"message\":\"Impossibile aggiungere! Sono disponibili solo " + prodottoScelto.getInStock() + " pezzi in magazzino per questo articolo.\""
                                    + "}";
                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                response.getWriter().write(jsonError);
                                return; // BLOCCO IMMEDIATO
                            } else {
                                // Fallback se si usa un form HTML classico senza JS
                                request.setAttribute("errorMessage", "Errore: Quantità non disponibile in magazzino. Massimo consentito: " + prodottoScelto.getInStock());
                            }
                        } else {
                            carrello.aggiungiProdotto(new CarrelloItem(
                                prodottoScelto.getId(), 
                                prodottoScelto.getNome(), 
                                prodottoScelto.getPrezzo(), 
                                prodottoScelto.getIva(), 
                                quantitaRichiesta
                            ));
                            request.setAttribute("successMessage", "Prodotto aggiunto al carrello con successo!");
                        }
                    } else {
                        request.setAttribute("errorMessage", "Errore: Prodotto non trovato nel database.");
                    }
                    
                } else if ("remove".equals(action)) {
                    carrello.rimuoviProdotto(idProdotto);
                    request.setAttribute("successMessage", "Prodotto rimosso dal carrello.");
                    
                } else if ("update".equals(action)) {
                    int nuovaQuantita = Integer.parseInt(request.getParameter("quantita"));
                    
                    // 3. CONTROLLO STOCK IN AGGIORNAMENTO (Click sul "+" nel carrello)
                    if (prodottoScelto != null && nuovaQuantita > prodottoScelto.getInStock()) {
                        if ("true".equals(isAjax)) {
                            String jsonError = "{"
                                + "\"status\":\"error\", "
                                + "\"message\":\"Attenzione! Sono disponibili solo " + prodottoScelto.getInStock() + " pezzi in magazzino per questo articolo.\""
                                + "}";
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(jsonError);
                            return; // BLOCCO IMMEDIATO
                        }
                    } else {
                        carrello.aggiornaQuantita(idProdotto, nuovaQuantita);
                        request.setAttribute("successMessage", "Quantità aggiornata correttamente.");
                    }
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
            
            double nuovoTotaleRiga = 0;
            int nuovaQuantitaRiga = 0;
            String nomeProdottoRiga = "";
            
            // Troviamo la riga appena aggiunta/aggiornata per estrarne i dati
            if ("update".equals(action) || "add".equals(action)) {
                for (CarrelloItem item : carrello.getItems()) {
                    if (item.getIdProdotto().equals(idProdotto)) {
                        nuovoTotaleRiga = item.getPrezzoTotale();
                        nuovaQuantitaRiga = item.getQuantita();
                        nomeProdottoRiga = item.getNome();
                        break;
                    }
                }
            }

            // Costruzione manuale della stringa JSON 
            String jsonResponse = "{"
                + "\"status\":\"success\", "
                + "\"totaleRiga\":" + String.valueOf(nuovoTotaleRiga) + ", "
                + "\"quantitaRiga\":" + nuovaQuantitaRiga + ", "
                + "\"nomeProdottoRiga\":\"" + nomeProdottoRiga.replace("\"", "\\\"") + "\", "
                + "\"subtotale\":" + String.valueOf(carrello.getSubtotaleProdotti()) + ", "
                + "\"spedizione\":" + String.valueOf(carrello.getCostoSpedizione()) + ", "
                + "\"totaleCarrello\":" + String.valueOf(carrello.getTotaleComplessivo()) + ", "
                + "\"quantitaTotale\":" + carrello.getTotaleArticoli()
                + "}";
                
            response.getWriter().write(jsonResponse);
            return; 
        }
        // PATTERN PRG: Redirigiamo l'utente pulendo l'URL per evitare doppi inserimenti al refresh
        response.sendRedirect(request.getContextPath() + "/common/carrello.jsp");
    }
}