package reframe.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import reframe.model.beans.Utente;
import reframe.model.dao.UtenteDAO;
import reframe.utils.HashingPassword;

@WebServlet("/RegistrazioneServlet")
public class RegistrazioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RegistrazioneServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/registrazione.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        /* VALIDAZIONE INPUT E SANIFICAZIONE */
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confermaPassword = request.getParameter("confermaPassword");
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String telefono = request.getParameter("telefono");
        
        List<String> errors = new ArrayList<>();
        
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
        
        // Validazione requisiti minimi di sicurezza per la password e verifica della corrispondenza per prevenire typo
        if (password == null || password.trim().isEmpty()) {
            errors.add("Tutti i campi sono obbligatori.");
        } else if (password.length() < 8) {
            errors.add("La password deve essere di almeno 8 caratteri.");
        } else if (confermaPassword == null || !password.equals(confermaPassword)) {
            errors.add("Le password inserite non coincidono. Riprova.");
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

        if (telefono == null && telefono.trim().isEmpty()) {
            errors.add("Tutti i campi sono obbligatori.");
        } else {
            telefono = telefono.trim(); 
        }
        
        // Interrompe l'elaborazione e ricarica la view iniettando i messaggi d'errore accumulati
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/registrazione.jsp").forward(request, response);
            return; 
        }

        /* ELABORAZIONE DATI E SALVATAGGIO A DATABASE */
        UtenteDAO dao = new UtenteDAO();
        
        try {
            // Controllo di unicità dell'indirizzo email a livello applicativo per prevenire collisioni sul DB
            if (dao.VerificaEmail(email)) {
                errors.add("Questa email è già registrata nel sistema.");
                request.setAttribute("errors", errors);
                request.getRequestDispatcher("/registrazione.jsp").forward(request, response);
                return;
            }
            
            // Generazione del digest crittografico della password per l'archiviazione sicura a database
            String passwordCriptata = HashingPassword.hashPassword(password);
            
            Utente nuovoUtente = new Utente();
            nuovoUtente.setUsername(username);
            nuovoUtente.setEmail(email);
            nuovoUtente.setPassword(passwordCriptata);
            nuovoUtente.setNome(nome);
            nuovoUtente.setCognome(cognome);
            nuovoUtente.setTelefono(telefono);

            dao.doSave(nuovoUtente); 

            response.sendRedirect(request.getContextPath() + "/login.jsp?success=registrazione");
            
        } catch (SQLException e) {
            e.printStackTrace();
            errors.add("Errore interno del server durante la registrazione. Riprova più tardi.");
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/registrazione.jsp").forward(request, response);
        }
    }
}