package reframe.controller;

import reframe.model.beans.Utente;
import reframe.model.dao.UtenteDAO;
import reframe.utils.EmailManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/AssegnaAdminServlet")
public class RiassegnaPraticaServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente superadmin = (Utente) session.getAttribute("utente");

        if (superadmin == null || superadmin.getIsAdmin() != 2) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String rma = request.getParameter("rma");
        String nuovoAdminUsername = request.getParameter("nuovoAdmin");

        try {
            // TODO: Recupera il VECCHIO admin prima di sovrascriverlo
            // String vecchioAdminUsername = praticaDAO.getAdminInCarico(rma);
            String vecchioAdminUsername = "admin_Mirko"; // Mockup
            
            // TODO: Aggiorna la pratica nel DB con il nuovo admin
            // praticaDAO.aggiornaAdminInCarico(rma, nuovoAdminUsername);

            UtenteDAO utenteDAO = new UtenteDAO();
            Utente vecchioAdmin = utenteDAO.doRetrieveByKey(vecchioAdminUsername);
            Utente nuovoAdmin = utenteDAO.doRetrieveByKey(nuovoAdminUsername);

            // 1. Invia email di revoca al vecchio admin
            if (vecchioAdmin != null && vecchioAdmin.getEmail() != null) {
                String objRevoca = "Reframe - Revoca incarico Pratica " + rma;
                String txtRevoca = "Ciao " + vecchioAdmin.getNome() + ",\n"
                                 + "Sei stato sollevato dalla gestione della pratica " + rma + ". "
                                 + "Il ticket è stato riassegnato dal Superadmin.";
                EmailManager.inviaEmail(vecchioAdmin.getEmail(), objRevoca, txtRevoca);
            }

            // 2. Invia email di assegnazione al nuovo admin
            if (nuovoAdmin != null && nuovoAdmin.getEmail() != null) {
                String objNuovo = "Reframe - Nuovo incarico Pratica " + rma;
                String txtNuovo = "Ciao " + nuovoAdmin.getNome() + ",\n"
                                + "Ti è stata assegnata la pratica " + rma + ". Accedi al tuo pannello per gestirla.";
                EmailManager.inviaEmail(nuovoAdmin.getEmail(), objNuovo, txtNuovo);
            }

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}