package reframe.controller;

import reframe.model.beans.Utente;
import reframe.model.dao.PraticaAssistenzaDAO;
import reframe.model.dao.UtenteDAO;
import reframe.utils.EmailManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/InviaNotaServlet")
public class InviaNotaServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        /* CONTROLLO ACCESSI E PERMESSI */
        HttpSession session = request.getSession();
        Utente superadmin = (Utente) session.getAttribute("utente");

        // RBAC (Role-Based Access Control): Restringe l'esecuzione esclusivamente agli utenti con privilegi di Livello 2 (Superadmin)
        if (superadmin == null || superadmin.getIsAdmin() != 2) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        /* ELABORAZIONE NOTA E INVIO EMAIL */
        String rma = request.getParameter("rma");
        String nota = request.getParameter("nota");
        
        try {
            PraticaAssistenzaDAO praticaDAO = new PraticaAssistenzaDAO();
            String usernameAdmin = praticaDAO.getAdminInCarico(rma);

            if (usernameAdmin == null || usernameAdmin.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            UtenteDAO utenteDAO = new UtenteDAO();
            Utente adminTarget = utenteDAO.doRetrieveByKey(usernameAdmin); 

            if (adminTarget != null && adminTarget.getEmail() != null) {
                String oggetto = "Reframe - Nota Superadmin per Pratica " + rma;
                String testo = "Ciao " + adminTarget.getNome() + ",\n\n"
                             + "Il superadmin " + superadmin.getUsername() + " ha lasciato una nota per la pratica " + rma + ":\n\n"
                             + "\"" + nota + "\"\n\n"
                             + "Accedi al pannello per maggiori dettagli.\nIl Team Reframe.";

                EmailManager.inviaEmail(adminTarget.getEmail(), oggetto, testo);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}