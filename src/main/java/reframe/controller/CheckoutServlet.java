package reframe.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import reframe.model.beans.Carrello;
import reframe.model.beans.CarrelloItem;
import reframe.model.beans.DettaglioOrdine;
import reframe.model.beans.Ordine;
import reframe.model.beans.Utente;
import reframe.model.dao.OrdineDAO;

import reframe.model.dao.SpedizioneDAO;
import reframe.model.dao.PagamentoDAO;
import reframe.model.beans.Spedizione;
import reframe.model.beans.Pagamento;
import java.util.List;


@WebServlet("/Checkout")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        // 1. VERIFICA AUTENTICAZIONE E CARRELLO
        Utente utente = (Utente) session.getAttribute("utente");
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (carrello == null || carrello.getItems().isEmpty()) {
            request.setAttribute("errorMessage", "Il tuo carrello è vuoto. Impossibile procedere al checkout.");
            request.getRequestDispatcher("/common/carrello.jsp").forward(request, response);            
            return;
        }

        try {
            // 2. GESTIONE SPEDIZIONE (Esistente o Nuova)
            String paramSpedizione = request.getParameter("idSpedizione");
            int idSpedizione = 0;
            
            if ("nuovo".equals(paramSpedizione)) {
                Spedizione s = new Spedizione();
                s.setIdUtente(utente.getUsername());
                s.setVia(request.getParameter("via"));
                s.setCivico(request.getParameter("civico"));
                s.setCitta(request.getParameter("citta"));
                s.setProvincia(request.getParameter("provincia"));
                s.setCap(request.getParameter("cap"));
                s.setPaese(request.getParameter("paese"));
                s.setNote(request.getParameter("note"));
                
                SpedizioneDAO sDAO = new SpedizioneDAO();
                sDAO.doSave(s); // Salva nel DB per acquisti futuri
                
                // Recupera l'ID appena generato
                List<Spedizione> listaS = sDAO.doRetrieveByUtente(utente.getUsername());
                idSpedizione = listaS.get(listaS.size() - 1).getIdSpedizione();
            } else {
                idSpedizione = Integer.parseInt(paramSpedizione);
            }

            // 3. GESTIONE PAGAMENTO (Esistente o Nuovo)
            String paramPagamento = request.getParameter("idPagamento");
            int idPagamento = 0;
            
            if ("nuovo".equals(paramPagamento)) {
                Pagamento p = new Pagamento();
                p.setIdUtente(utente.getUsername());
                p.setNomeIntestatario(request.getParameter("nomeIntestatario"));
                p.setCircuito(request.getParameter("circuito"));
                p.setNumeroCarta(request.getParameter("numeroCarta"));
                p.setDataScadenza(request.getParameter("dataScadenza"));
                p.setCvv(request.getParameter("cvv"));
                
                PagamentoDAO pDAO = new PagamentoDAO();
                pDAO.doSave(p); // Salva nel DB
                
                // Recupera l'ID appena generato
                List<Pagamento> listaP = pDAO.doRetrieveByUtente(utente.getUsername());
                idPagamento = listaP.get(listaP.size() - 1).getIdPagamento();
            } else {
                idPagamento = Integer.parseInt(paramPagamento);
            }
            
            // 4. CREAZIONE TESTATA ORDINE
            boolean garanzia = true;
            String idOrdine = "ORD" + String.format("%05d", (int)(Math.random() * 100000));
            Date dataOdierna = new Date(System.currentTimeMillis());
            
            Ordine ordine = new Ordine();
            ordine.setIdOrdine(idOrdine);
            ordine.setUrlFattura("/fatture/" + idOrdine + ".pdf"); 
            ordine.setDataOrdine(dataOdierna);
            ordine.setTotale(carrello.getTotaleComplessivo()); // Usiamo il nuovo metodo comprensivo di spedizione
            ordine.setGaranzia(garanzia);
            ordine.setStato("In lavorazione"); 
            ordine.setIdUtente(utente.getUsername());
            ordine.setIdPagamento(idPagamento);
            ordine.setIdSpedizione(idSpedizione);

            // 5. MAPPATURA DEI DETTAGLI
            for (CarrelloItem item : carrello.getItems()) {
                DettaglioOrdine dettaglio = new DettaglioOrdine();
                dettaglio.setIdOrdine(idOrdine);
                dettaglio.setIdProdotto(item.getIdProdotto());
                dettaglio.setPrezzoAcquisto(item.getPrezzo());
                dettaglio.setQuantitaAcquisto(item.getQuantita());
                dettaglio.setNomeProdottoAcquisto(item.getNome());
                dettaglio.setIvaAcquisto(item.getIva());
                ordine.addDettaglio(dettaglio);
            }

            // 6. SALVATAGGIO A DATABASE E REDIRECT DIRETTO
            OrdineDAO ordineDAO = new OrdineDAO();
            ordineDAO.insertOrdineCompleto(ordine);

            carrello.svuota();
            request.setAttribute("ordineEffettuato", ordine);
            
            // Reindirizziamo direttamente alla pagina di ringraziamento
            request.getRequestDispatcher("/common/grazie.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace(); 
            
            // SE IL DATABASE BLOCCA L'ORDINE PERCHÉ LA GIACENZA È FINITA MENTRE L'UTENTE PAGAVA
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("chk_stock")) {
                request.setAttribute("errorMessage", "Siamo spiacenti, ma uno o più prodotti nel tuo carrello sono appena andati esauriti e non sono più disponibili nelle quantità richieste.");
                request.getRequestDispatcher("/common/carrello.jsp").forward(request, response);
                return;
            }
            
            // ALTRI ERRORI SQL GENERICI
            request.setAttribute("errorMessage", "Errore critico durante l'elaborazione del pagamento: " + e.getMessage());
            request.getRequestDispatcher("/500.jsp").forward(request, response); 
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Si è verificato un errore inaspettato durante il checkout.");
            request.getRequestDispatcher("/500.jsp").forward(request, response);
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Se un utente prova ad accedere al checkout via URL direttamente, lo si rimanda al carrello o lo si elabora se consentito.
        // Di prassi, il submit del checkout dovrebbe essere sempre POST.
        response.sendRedirect(request.getContextPath() + "/common/carrello.jsp");
    }
}