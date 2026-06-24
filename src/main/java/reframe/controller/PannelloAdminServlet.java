package reframe.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import reframe.model.beans.Prodotto;
import reframe.model.beans.Utente;
import reframe.model.dao.ProdottoDAO; 
import reframe.model.dao.UtenteDAO;

@WebServlet("/PannelloAdminServlet")
public class PannelloAdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente adminLoggato = (Utente) session.getAttribute("utente");

        // 1. Controllo di sicurezza: se non sei loggato o non sei admin, fuori!
        if (adminLoggato == null || adminLoggato.getIsAdmin() == 0) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // 2. Recupero i Prodotti dal Database
            ProdottoDAO prodottoDAO = new ProdottoDAO();
            List<Prodotto> listaProdotti = prodottoDAO.fetchAllProdotti();
            request.setAttribute("listaProdotti", listaProdotti);

            // 3. Se è Super Admin, recupero anche la lista degli altri Admin
            if (adminLoggato.getIsAdmin() == 2) {
                UtenteDAO utenteDAO = new UtenteDAO();
                List<Utente> listaAdmins = utenteDAO.doRetrieveAllAdmins();
                request.setAttribute("listaAdmins", listaAdmins);
            }

            // 4. Passo tutti i dati alla JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/pannelloAdmin.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Gestione errore
            response.sendRedirect(request.getContextPath() + "/500.jsp");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}