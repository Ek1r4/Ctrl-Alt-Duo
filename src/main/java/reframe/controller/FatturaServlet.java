package reframe.controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import reframe.model.beans.*;
import reframe.model.dao.OrdineDAO;

@WebServlet("/Fattura")
public class FatturaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        /* AUTENTICAZIONE E VALIDAZIONE INPUT */
        Utente utente = (Utente) request.getSession().getAttribute("utente");
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String idOrdine = request.getParameter("id");
        if (idOrdine == null || idOrdine.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/404.jsp");
            return;
        }

        /* RECUPERO DATI E AUTORIZZAZIONE */
        try {
            OrdineDAO dao = new OrdineDAO();
            Ordine ordine = dao.fetchOrdineById(idOrdine);

            // IDOR Prevention: Previene vulnerabilità di Insecure Direct Object Reference assicurandosi che 
            // l'utente in sessione sia l'effettivo intestatario dell'ordine richiesto via parametro GET
            if (ordine != null && ordine.getIdUtente().equals(utente.getUsername())) {
                request.setAttribute("ordineFattura", ordine);
                request.getRequestDispatcher("/common/fattura.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/accessoNegato.jsp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.getRequestDispatcher("/500.jsp").forward(request, response);
        }
    }
}