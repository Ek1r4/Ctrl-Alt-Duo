package reframe.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import reframe.model.dao.UtenteDAO;

@WebServlet("/VerificaEmailServlet")
public class VerificaEmailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String email = request.getParameter("email");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Controllo di sicurezza
        if (email == null || email.trim().isEmpty()) {
            out.print("{\"esiste\": false}");
            return;
        }

        UtenteDAO dao = new UtenteDAO();
        try {
            boolean esiste = dao.VerificaEmail(email.trim());
            
            // Creazione stringa JSON di risposta
            out.print("{\"esiste\": " + esiste + "}");
            
        } catch (SQLException e) {
            e.printStackTrace();
            out.print("{\"esiste\": false}"); 
        } finally {
            out.flush();
        }
    }
}