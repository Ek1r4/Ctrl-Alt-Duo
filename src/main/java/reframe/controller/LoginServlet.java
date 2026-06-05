package reframe.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.sql.*;
import java.util.*;

import reframe.model.dao.UtenteDAO;
import reframe.model.beans.Utente;
import reframe.utils.HashingPassword;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public LoginServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String emailInserita = request.getParameter("email");
        String passwordInserita = request.getParameter("password");
        List<String> errors = new java.util.ArrayList<>();
        
        // Controlli e trim
        if (emailInserita == null || emailInserita.trim().isEmpty()) {
            errors.add("Il campo email è obbligatorio e non può contenere solo spazi.");
        } else {	emailInserita = emailInserita.trim();	}
        
        if (passwordInserita == null || passwordInserita.trim().isEmpty()) {
            errors.add("Il campo password è obbligatorio.");
        } else {	passwordInserita = passwordInserita.trim();	}
        
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            return;
        }
        
        // Hashing
         String passwordCriptata = HashingPassword.hashPassword(passwordInserita);
        
        UtenteDAO dao = new UtenteDAO();
 
        try        
        {
        	Utente utenteLoggato = dao.doRetrieveByEmail(emailInserita);
        	
        	if (utenteLoggato != null && utenteLoggato.getPassword().equals(passwordCriptata)) {
            HttpSession session = request.getSession();
            session.setAttribute("utente", utenteLoggato);
            response.sendRedirect(request.getContextPath() + "/jsp/index.jsp");
        	} 
        	else {	response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?errore=credenziali_errate");	}
        } catch(SQLException e) {	e.printStackTrace();	}
    }
}

