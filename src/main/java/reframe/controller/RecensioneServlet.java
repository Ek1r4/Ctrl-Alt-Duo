package reframe.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import reframe.model.beans.Recensione;
import reframe.model.beans.Utente;
import reframe.model.dao.RecensioniDAO;

@WebServlet("/RecensioneServlet")
public class RecensioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");
        
        // 1. SICUREZZA: Se non sei loggato, la Servlet blocca tutto e ti manda al login
        if (utenteLoggato == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        RecensioniDAO dao = new RecensioniDAO();

        // ==========================================
        // AZIONE: AGGIUNGI RECENSIONE
        // ==========================================
        if ("aggiungi".equals(action)) {
        	
        	if (utenteLoggato.getIsAdmin() > 0) {
                response.sendRedirect(request.getContextPath() + "/accessoNegato.jsp");
                return;
            }
        	
            String idProdotto = request.getParameter("idProdotto");
            String ratingStr = request.getParameter("rating");
            String descrizione = request.getParameter("descrizione");
            
            // Generazione di un ID casuale di 8 caratteri (Alfanumerico e Maiuscolo)
            String idRecensione = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            
            try {
                double rating = Double.parseDouble(ratingStr);
                
                Recensione r = new Recensione();
                r.setIdRecensione(idRecensione);
                r.setDescrizione(descrizione);
                r.setRating(rating);
                r.setIdProdotto(idProdotto);
                r.setIdUtente(utenteLoggato.getUsername());
                
                // Salvataggio nel Database
                boolean successo = dao.doSave(r);
                
                // TRUCCHETTO: Leggiamo l'header "referer" per sapere esattamente da quale pagina 
                // l'utente ha inviato la recensione, così lo rimandiamo lì direttamente!
                String referer = request.getHeader("referer");
                
                if (successo) {
                    response.sendRedirect(referer != null ? referer : request.getContextPath() + "/index.jsp");
                } else {
                    response.sendRedirect(referer != null ? referer + "&error=salvataggio_recensione" : request.getContextPath() + "/index.jsp");
                }
                
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace(); // Stampa l'errore in console come da tua abitudine
                String referer = request.getHeader("referer");
                response.sendRedirect(referer != null ? referer : request.getContextPath() + "/500.jsp");
            }
            return;
        }
        
        // ==========================================
        // AZIONE: ELIMINA RECENSIONE (Admin)
        // ==========================================
        if ("elimina".equals(action)) {
            // Controllo di sicurezza aggiuntivo: solo gli admin possono eliminare i commenti
            if (utenteLoggato.getIsAdmin() == 0) {
                response.sendRedirect(request.getContextPath() + "/accessoNegato.jsp");
                return;
            }
            
            String idRecensione = request.getParameter("idRecensione");
            
            try {
                dao.doDelete(idRecensione);
                String referer = request.getHeader("referer");
                response.sendRedirect(referer != null ? referer : request.getContextPath() + "/index.jsp");
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/500.jsp");
            }
            return;
        }
    }
}