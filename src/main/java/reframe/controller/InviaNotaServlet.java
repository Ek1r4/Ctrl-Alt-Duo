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

@WebServlet("/InviaNotaServlet")
public class InviaNotaServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente superadmin = (Utente) session.getAttribute("utente");

        // Controllo di sicurezza: solo il Superadmin (isAdmin = 2) può inviare note
        if (superadmin == null || superadmin.getIsAdmin() != 2) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String rma = request.getParameter("rma");
        String nota = request.getParameter("nota");
        
        try {
            // TODO: Recupera l'username dell'admin in carico tramite PraticaDAO
            // String usernameAdmin = praticaDAO.getAdminInCarico(rma);
            String usernameAdmin = "admin_Erika"; // Mockup

            UtenteDAO utenteDAO = new UtenteDAO();
            Utente adminTarget = utenteDAO.doRetrieveByKey(usernameAdmin); // Devi avere questo metodo nel DAO

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