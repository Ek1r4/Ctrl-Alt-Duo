package reframe.controller;

import reframe.model.beans.Utente;
import reframe.model.dao.UtenteDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/RevocaAccessoServlet")
public class RevocaAccessoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente superadmin = (Utente) session.getAttribute("utente");

        if (superadmin == null || superadmin.getIsAdmin() != 2) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // L'username dell'admin da declassare
        String targetAdminUsername = request.getParameter("usernameAdmin");

        try {
            UtenteDAO utenteDAO = new UtenteDAO();
            Utente targetAdmin = utenteDAO.doRetrieveByKey(targetAdminUsername);

            if (targetAdmin != null && targetAdmin.getIsAdmin() == 1) {
                
                // TODO: Nel tuo UtenteDAO devi avere un metodo per fare l'update del ruolo
                // utenteDAO.aggiornaRuolo(targetAdminUsername, 0); 
                
                // NOTA: Se preferisci l'eliminazione fisica usa: utenteDAO.doDelete(targetAdminUsername);
                // ma ricordati che le Foreign Key potrebbero bloccarti se ha pratiche a suo nome.

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