package reframe.controller;

import reframe.model.beans.PraticaAssistenza;
import reframe.model.beans.Ticket;
import reframe.model.beans.Utente;
import reframe.model.dao.PraticaAssistenzaDAO;
import reframe.model.dao.TicketDAO;
import reframe.utils.GeneratoreID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/ChatServlet")
public class ChatServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        /* CONFIGURAZIONE E CONTROLLO ACCESSI */
        
        // Forza la lettura in UTF-8 per supportare accenti e caratteri speciali nei messaggi
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");

        if (utenteLoggato == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        /* VALIDAZIONE INPUT */
        
        String rma = request.getParameter("rma");
        String testo = request.getParameter("testo");

        if (testo == null || testo.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        PraticaAssistenzaDAO praticaDAO = new PraticaAssistenzaDAO();
        TicketDAO ticketDAO = new TicketDAO();

        try {
            PraticaAssistenza praticaCorrente = praticaDAO.doRetrieveByRma(rma);
            
            if (praticaCorrente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Sicurezza: Impedisce l'inserimento di nuovi messaggi se il ticket è archiviato
            if ("Chiusa".equalsIgnoreCase(praticaCorrente.getStato())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            /* SALVATAGGIO NUOVO MESSAGGIO */
            
            Ticket nuovoMessaggio = new Ticket();
            String idGenerato = GeneratoreID.generaIdTicket(); 
            
            nuovoMessaggio.setIdTicket(idGenerato);
            nuovoMessaggio.setRmaPratica(rma);
            nuovoMessaggio.setAutore(utenteLoggato.getUsername());
            nuovoMessaggio.setMessaggio(testo);
            
            // Definisce la provenienza del messaggio per il rendering UI lato client
            if (utenteLoggato.getIsAdmin() > 0) {
                nuovoMessaggio.setTipo("Admin");
            } else {
                nuovoMessaggio.setTipo("User");
            }

            if (ticketDAO.doSave(nuovoMessaggio)) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}