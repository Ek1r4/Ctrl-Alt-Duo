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

@WebServlet("/Checkout")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        // 1. VERIFICA AUTENTICAZIONE E CARRELLO
        Utente utente = (Utente) session.getAttribute("utente");
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (utente == null) {
            // L'utente non è loggato, lo rimandiamo al login
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (carrello == null || carrello.getItems().isEmpty()) {
            // Carrello vuoto o inesistente
            request.setAttribute("errorMessage", "Il tuo carrello è vuoto. Impossibile procedere al checkout.");
            request.getRequestDispatcher("/common/carrello.jsp").forward(request, response);            return;
        }

        try {
            // 2. RECUPERO DATI DAL FORM DI CHECKOUT (Pagamento e Spedizione scelti)
            // Nota: per ora prendiamo i parametri in modo protetto, in attesa di avere la JSP finale
            String paramPagamento = request.getParameter("idPagamento");
            String paramSpedizione = request.getParameter("idSpedizione");
            
            // Usiamo gli ID mockati che abbiamo inserito nello script SQL di default (1) se i parametri mancano
            int idPagamento = (paramPagamento != null && !paramPagamento.isEmpty()) ? Integer.parseInt(paramPagamento) : 1; 
            int idSpedizione = (paramSpedizione != null && !paramSpedizione.isEmpty()) ? Integer.parseInt(paramSpedizione) : 1;
            
         // Requisito: Garanzia forzata sempre a 1 (true)
            boolean garanzia = true;

            // 3. CREAZIONE TESTATA ORDINE
            // Generiamo un ID_Ordine univoco di 8 caratteri come da vincolo DB (es. ORD12345)
            String idOrdine = "ORD" + String.format("%05d", (int)(Math.random() * 100000));
            Date dataOdierna = new Date(System.currentTimeMillis());
            
            Ordine ordine = new Ordine();
            ordine.setIdOrdine(idOrdine);
            ordine.setUrlFattura("/fatture/" + idOrdine + ".pdf"); // Placeholder, in futuro generabile dinamicamente
            ordine.setDataOrdine(dataOdierna);
            ordine.setTotale(carrello.getTotale());
            ordine.setGaranzia(garanzia);
            ordine.setStato("In lavorazione"); // Vincolo ENUM rispettato
            ordine.setIdUtente(utente.getUsername());
            ordine.setIdPagamento(idPagamento);
            ordine.setIdSpedizione(idSpedizione);

            // 4. MAPPATURA DEI DETTAGLI DAL CARRELLO
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

            // 5. SALVATAGGIO A DATABASE (Transazione Sicura)
            OrdineDAO ordineDAO = new OrdineDAO();
            ordineDAO.insertOrdineCompleto(ordine);

         // Svuotamento carrello e messaggi (già presenti nel tuo codice)
            carrello.svuota();
            
            // Passiamo l'oggetto ordine intero alla JSP
            request.setAttribute("ordineEffettuato", ordine);
            
            // Reindirizziamo alla nuova pagina di ringraziamento
            request.getRequestDispatcher("/common/grazie.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace(); // Utile per debuggare la console di Tomcat
            request.setAttribute("errorMessage", "Errore critico durante l'elaborazione del pagamento: " + e.getMessage());
            // Il server non deve mai perdere il controllo (Requisito Checklist Sicurezza)
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