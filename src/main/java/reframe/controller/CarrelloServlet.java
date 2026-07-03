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
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        /* GESTIONE SESSIONE CARRELLO */
        HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        
        if (carrello == null) {
            carrello = new Carrello();
            session.setAttribute("carrello", carrello);
        }

        /* ELABORAZIONE RICHIESTA E CHECK STOCK */
        String action = request.getParameter("action");
        String idProdotto = request.getParameter("id");
        String isAjax = request.getParameter("ajax");

        if (action != null && idProdotto != null) {
            try {
                // Recupero dati DB in tempo reale per validazione stock
                ProdottoDAO prodottoDAO = new ProdottoDAO();
                Prodotto prodottoScelto = prodottoDAO.fetchProdottoById(idProdotto);

                /* AZIONE: ADD */
                if ("add".equals(action)) {
                    int quantitaRichiesta = Integer.parseInt(request.getParameter("quantita"));
                    
                    int quantitaGiaPresente = 0;
                    for (CarrelloItem item : carrello.getItems()) {
                        if (item.getIdProdotto().equals(idProdotto)) {
                            quantitaGiaPresente = item.getQuantita();
                            break;
                        }
                    }
                    
                    int quantitaTotale = quantitaGiaPresente + quantitaRichiesta;

                    if (prodottoScelto != null) {
                        // Validazione disponibilità magazzino in aggiunta
                        if (quantitaTotale > prodottoScelto.getInStock()) {
                            
                            if ("true".equals(isAjax)) {
                                String jsonError = "{"
                                    + "\"status\":\"error\", "
                                    + "\"message\":\"Impossibile aggiungere! Sono disponibili solo " + prodottoScelto.getInStock() + " pezzi in magazzino per questo articolo.\""
                                    + "}";
                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                response.getWriter().write(jsonError);
                                return; 
                            } else {
                                request.setAttribute("errorMessage", "Errore: Quantità non disponibile in magazzino. Massimo consentito: " + prodottoScelto.getInStock());
                            }
                        } else {
                            carrello.aggiungiProdotto(new CarrelloItem(
                                prodottoScelto.getId(), 
                                prodottoScelto.getNome(), 
                                prodottoScelto.getPrezzo(), 
                                prodottoScelto.getIva(), 
                                quantitaRichiesta,
                                prodottoScelto.getInStock()
                            ));
                            request.setAttribute("successMessage", "Prodotto aggiunto al carrello con successo!");
                        }
                    } else {
                        request.setAttribute("errorMessage", "Errore: Prodotto non trovato nel database.");
                    }
                    
                /* AZIONE: REMOVE */
                } else if ("remove".equals(action)) {
                    carrello.rimuoviProdotto(idProdotto);
                    request.setAttribute("successMessage", "Prodotto rimosso dal carrello.");
                    
                /* AZIONE: UPDATE */
                } else if ("update".equals(action)) {
                    int nuovaQuantita = Integer.parseInt(request.getParameter("quantita"));
                    
                    // Validazione disponibilità magazzino in aggiornamento
                    if (prodottoScelto != null && nuovaQuantita > prodottoScelto.getInStock()) {
                        if ("true".equals(isAjax)) {
                            String jsonError = "{"
                                + "\"status\":\"error\", "
                                + "\"message\":\"Attenzione! Sono disponibili solo " + prodottoScelto.getInStock() + " pezzi in magazzino per questo articolo.\""
                                + "}";
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(jsonError);
                            return; 
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

        /* RISPOSTA CLIENT (AJAX) */
        if ("true".equals(isAjax)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            double nuovoTotaleRiga = 0;
            int nuovaQuantitaRiga = 0;
            String nomeProdottoRiga = "";
            
            // Recupero dati specifici della riga alterata per update parziale DOM
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
        
        /* RISPOSTA CLIENT (PRG) */
        // Pattern PRG (Post-Redirect-Get) per prevenire form resubmission
        response.sendRedirect(request.getContextPath() + "/common/carrello.jsp");
    }
}