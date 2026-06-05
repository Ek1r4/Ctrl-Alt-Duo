package reframe.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Recupero la sessione corrente dell'utente (se esiste)
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // 2. Distruggo l'armadietto e tutto il suo contenuto (utente loggato)
            session.invalidate();
        }
        
        // 3. Rispedisco l'utente alla home page
        response.sendRedirect(request.getContextPath() + "/jsp/index.jsp");
    }
}