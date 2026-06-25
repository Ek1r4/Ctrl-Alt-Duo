package reframe.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
        
    	String username = request.getParameter("username");
    	String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("adminEmail");
        String password = request.getParameter("adminPassword");
        
        List<String> errors = new ArrayList<>();
        
        // Controlli e trim
        if (username == null || username.trim().isEmpty()) {
            errors.add("Tutti i campi sono obbligatori.");
        } else {
            username = username.trim();
        }
        
        if (email == null || email.trim().isEmpty()) {
            errors.add("Tutti i campi sono obbligatori.");
        } else {
            email = email.trim();
        }
        
        if (password == null || password.trim().isEmpty()) {
            errors.add("Tutti i campi sono obbligatori.");
        } else if (password.length() < 8) {
            errors.add("La password deve essere di almeno 8 caratteri.");
        } else {
            password = password.trim();
        }
        
        if (nome == null || nome.trim().isEmpty()) {
            errors.add("Tutti i campi sono obbligatori.");
        } else {
            nome = nome.trim();
        }
        
        if (cognome == null || cognome.trim().isEmpty()) {
            errors.add("Tutti i campi sono obbligatori.");
        } else {
            cognome = cognome.trim();
        }
        
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/admin/pannelloAdmin.jsp").forward(request, response);
            return; 
        }
        
        UtenteDAO dao = new UtenteDAO();
        
        try {
        	// Controllo email
            if (dao.VerificaEmail(email)) {
                errors.add("Questa email è già registrata nel sistema.");
                request.setAttribute("errors", errors);
                request.getRequestDispatcher("/admin/pannelloAdmin.jsp").forward(request, response);
                return;
            }
        
            Utente nuovoAdmin = new Utente();
            nuovoAdmin.setUsername(username);
            nuovoAdmin.setEmail(email);
            nuovoAdmin.setPassword(password);
            nuovoAdmin.setNome(nome);
            nuovoAdmin.setCognome(cognome);
            
            
            dao.doSaveAdmin(nuovoAdmin);
            
            response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?success=adminCreato#superadmin");
        } catch (SQLException e) {
            e.printStackTrace();
            errors.add("Errore interno del server durante la registrazione. Riprova più tardi.");
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/admin/pannelloAdmin.jsp").forward(request, response);
        }
    }
}