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
        
        /* CONFIGURAZIONE E CONTROLLO ACCESSI */
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Utente utenteLoggato = (Utente) session.getAttribute("utente");

        /* ELABORAZIONE PARAMETRI E VALIDAZIONE */
        String titolo = request.getParameter("titolo");
        String categoria = request.getParameter("categoria");
        String descrizione = request.getParameter("descrizione");

        // Validazione server-side per prevenire l'elaborazione di richieste malformate in caso di bypass dei controlli frontend
        if (titolo == null || titolo.trim().isEmpty() || 
            categoria == null || categoria.trim().isEmpty() || 
            descrizione == null || descrizione.trim().isEmpty()) {
            
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        /* CREAZIONE BEAN E SALVATAGGIO A DATABASE */
        String nuovoRma = GeneratoreID.generaRMA();

        PraticaAssistenza nuovaPratica = new PraticaAssistenza();
        nuovaPratica.setRma(nuovoRma);
        nuovaPratica.setTitolo(titolo.trim());
        nuovaPratica.setCategoria(categoria.trim());
        nuovaPratica.setDescrizione(descrizione.trim());
        nuovaPratica.setIdUtente(utenteLoggato.getUsername());

        PraticaAssistenzaDAO dao = new PraticaAssistenzaDAO();
        try {
            boolean inserita = dao.doSave(nuovaPratica);
            
            if (inserita) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}