package reframe.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import reframe.model.beans.PraticaAssistenza;
import reframe.model.beans.Utente;
import reframe.model.dao.PraticaAssistenzaDAO;
import reframe.utils.GeneratoreID;

@WebServlet("/CreaPraticaServlet")
public class CreaPraticaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	request.setCharacterEncoding("UTF-8");
        
        // 1. Controllo Autenticazione (Sicurezza)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            // Se l'utente non è loggato, la fetch AJAX riceve un 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Utente utenteLoggato = (Utente) session.getAttribute("utente");

        // 2. Recupero dei Parametri dal corpo della richiesta (inviati da JS)
        String titolo = request.getParameter("titolo");
        String categoria = request.getParameter("categoria");
        String descrizione = request.getParameter("descrizione");

        // Validazione Server-Side di sicurezza (in caso si aggiri il JS)
        if (titolo == null || titolo.trim().isEmpty() || 
            categoria == null || categoria.trim().isEmpty() || 
            descrizione == null || descrizione.trim().isEmpty()) {
            
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 3. Generazione dell'ID univoco tramite la nostra Utility
        String nuovoRma = GeneratoreID.generaRMA();

        // 4. Popolamento del Bean
        PraticaAssistenza nuovaPratica = new PraticaAssistenza();
        nuovaPratica.setRma(nuovoRma);
        nuovaPratica.setTitolo(titolo.trim());
        nuovaPratica.setCategoria(categoria.trim());
        nuovaPratica.setDescrizione(descrizione.trim());
        nuovaPratica.setIdUtente(utenteLoggato.getUsername());
        // Lo stato "Aperta" e la data vengono gestiti in automatico dal DAO/Database

        // 5. Inserimento nel Database
        PraticaAssistenzaDAO dao = new PraticaAssistenzaDAO();
        try {
            boolean inserita = dao.doSave(nuovaPratica);
            
            if (inserita) {
                // Successo! Rispondiamo al JS con HTTP 200 OK
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                // Errore generico del DB (es. violazione vincoli)
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}