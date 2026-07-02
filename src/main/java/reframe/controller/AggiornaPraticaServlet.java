package reframe.controller;
import reframe.utils.GeneratoreID;

import reframe.model.beans.PraticaAssistenza;
import reframe.model.beans.Ticket;
import reframe.model.beans.Utente;
import reframe.model.dao.PraticaAssistenzaDAO;
import reframe.model.dao.TicketDAO;
import reframe.model.dao.UtenteDAO;
import reframe.utils.EmailManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/AggiornaPraticaServlet")
public class AggiornaPraticaServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");

        if (utenteLoggato == null || utenteLoggato.getIsAdmin() == 0) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String rma = request.getParameter("rma");
        String action = request.getParameter("action");

        PraticaAssistenzaDAO praticaDAO = new PraticaAssistenzaDAO();
        TicketDAO ticketDAO = new TicketDAO();
        UtenteDAO utenteDAO = new UtenteDAO();

        try {
            PraticaAssistenza praticaCorrente = praticaDAO.doRetrieveByRma(rma);
            if (praticaCorrente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if ("aggiornaStato".equals(action)) {
                String nuovoStato = request.getParameter("stato");

                // Permessi: Solo l'admin assegnato o il superadmin possono chiudere/riaprire la pratica
                if (utenteLoggato.getIsAdmin() == 1 && !utenteLoggato.getUsername().equals(praticaCorrente.getAdminAssegnato())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                
                if (praticaDAO.updateStato(rma, nuovoStato)) {
                    // Genera messaggio di sistema corretto
                    Ticket sysMsg = new Ticket();
                    sysMsg.setIdTicket(GeneratoreID.generaIdTicket()); 
                    sysMsg.setRmaPratica(rma);
                    sysMsg.setAutore(utenteLoggato.getUsername()); // Registra chi ha fatto l'azione
                    sysMsg.setTipo("Admin");
                    sysMsg.setMessaggio("[NOTIFICA DI SISTEMA] - Stato modificato in: " + nuovoStato);
                    ticketDAO.doSave(sysMsg);

                 // Invia Email al Cliente con Copy Differenziato
                    Utente cliente = utenteDAO.doRetrieveByKey(praticaCorrente.getIdUtente());
                    
                    if (cliente != null && cliente.getEmail() != null) {
                        String oggetto = "";
                        String testo = "";

                        if ("Chiusa".equalsIgnoreCase(nuovoStato)) {
                            // Copy per chiusura pratica
                            oggetto = "REFRAME - Il tuo ticket " + rma + " è stato risolto";
                            testo = "Ciao " + cliente.getNome() + ",\n\n"
                                  + "Abbiamo un aggiornamento per te: la tua pratica " + rma + " è stata contrassegnata come RISOLTA ed è ora ufficialmente chiusa.\n\n"
                                  + "Speriamo di aver gestito la tua richiesta con la cura e l'attenzione che meriti. Puoi sempre consultare lo storico della pratica nella tua area personale.\n\n"
                                  + "Se dovessi avere ancora bisogno di noi per questo o altri ordini, sai dove trovarci.\n\n"
                                  + "A presto,\n"
                                  + "Il Team REFRAME";
                        } else {
                            // Copy per riapertura / presa in carico
                            oggetto = "REFRAME - Abbiamo riaperto il tuo ticket " + rma;
                            testo = "Ciao " + cliente.getNome() + ",\n\n"
                                  + "Ti contattiamo per informarti che la tua pratica " + rma + " è stata riaperta ed è nuovamente IN CARICO al nostro team.\n\n"
                                  + "Stiamo approfondendo i dettagli della tua richiesta per garantirti la migliore soluzione possibile. Riceverai presto una nostra risposta direttamente all'interno del ticket.\n\n"
                                  + "Grazie per la pazienza e la collaborazione.\n\n"
                                  + "Il Team REFRAME";
                        }

                        EmailManager.inviaEmail(cliente.getEmail(), oggetto, testo);
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }

            } else if ("assegnaAdmin".equals(action)) {
                // Permessi: Solo il Superadmin (Livello 2) può riassegnare
                if (utenteLoggato.getIsAdmin() != 2) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                String nuovoAdminUsername = request.getParameter("nuovoAdmin");
                String vecchioAdminUsername = praticaCorrente.getAdminAssegnato();

                if (praticaDAO.updateAdminAssegnato(rma, nuovoAdminUsername)) {
                        
                        // Genera messaggio di sistema corretto
                        Ticket sysMsg = new Ticket();
                        sysMsg.setIdTicket(GeneratoreID.generaIdTicket());
                        sysMsg.setRmaPratica(rma);
                        sysMsg.setAutore(utenteLoggato.getUsername()); // Registra chi ha fatto l'azione
                        sysMsg.setTipo("Admin");
                        sysMsg.setMessaggio("[NOTIFICA DI SISTEMA] - Pratica assegnata a: " + nuovoAdminUsername);
                        ticketDAO.doSave(sysMsg);

                    Utente vecchioAdmin = (vecchioAdminUsername != null && !vecchioAdminUsername.equals("Da assegnare")) ? utenteDAO.doRetrieveByKey(vecchioAdminUsername) : null;
                    Utente nuovoAdmin = utenteDAO.doRetrieveByKey(nuovoAdminUsername);

                    if (vecchioAdmin != null && vecchioAdmin.getEmail() != null) {
                        String objRevoca = "Reframe - Revoca incarico Pratica " + rma;
                        String txtRevoca = "Ciao " + vecchioAdmin.getNome() + ",\nSei stato sollevato dalla gestione della pratica " + rma + ". Il ticket è stato riassegnato.";
                        EmailManager.inviaEmail(vecchioAdmin.getEmail(), objRevoca, txtRevoca);
                    }

                    if (nuovoAdmin != null && nuovoAdmin.getEmail() != null) {
                        String objNuovo = "Reframe - Nuovo incarico Pratica " + rma;
                        String txtNuovo = "Ciao " + nuovoAdmin.getNome() + ",\nTi è stata assegnata la pratica " + rma + ".";
                        EmailManager.inviaEmail(nuovoAdmin.getEmail(), objNuovo, txtNuovo);
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                 response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}