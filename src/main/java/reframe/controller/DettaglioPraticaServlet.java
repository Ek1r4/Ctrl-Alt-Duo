package reframe.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import reframe.model.beans.PraticaAssistenza;
import reframe.model.beans.Ticket;
import reframe.model.beans.Utente;
import reframe.model.dao.PraticaAssistenzaDAO;
import reframe.model.dao.TicketDAO;

@WebServlet("/DettaglioPraticaServlet")
public class DettaglioPraticaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Utility interna per sfuggire i caratteri speciali nel JSON manuale
    private String escapeJson(String data) {
        if (data == null) return "";
        return data.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Sicurezza: Controllo Login
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Utente utenteLoggato = (Utente) session.getAttribute("utente");
        String rma = request.getParameter("rma");

        if (rma == null || rma.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        PraticaAssistenzaDAO praticaDAO = new PraticaAssistenzaDAO();
        TicketDAO ticketDAO = new TicketDAO();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try {
            // 2. Recupero Pratica
            PraticaAssistenza pratica = praticaDAO.doRetrieveByRma(rma);
            
            if (pratica == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 3. Controllo Permessi (IDOR Prevention)
            // Se l'utente non è admin (livello 0), deve essere il proprietario della pratica
            if (utenteLoggato.getIsAdmin() == 0 && !pratica.getIdUtente().equals(utenteLoggato.getUsername())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                return;
            }

            // 4. Recupero Messaggi
            List<Ticket> messaggi = ticketDAO.doRetrieveByRma(rma);
            
            // 5. Costruzione JSON Manuale
            response.setContentType("application/json; charset=UTF-8");
            PrintWriter out = response.getWriter();
            StringBuilder json = new StringBuilder();
			boolean isProprietario = pratica.getIdUtente().equals(utenteLoggato.getUsername());
			
            json.append("{");
            json.append("\"isProprietario\":").append(isProprietario).append(",");
            json.append("\"rma\":\"").append(escapeJson(pratica.getRma())).append("\",");
            json.append("\"titolo\":\"").append(escapeJson(pratica.getTitolo())).append("\",");
            json.append("\"categoria\":\"").append(escapeJson(pratica.getCategoria())).append("\",");
            json.append("\"descrizione\":\"").append(escapeJson(pratica.getDescrizione())).append("\",");
            json.append("\"stato\":\"").append(escapeJson(pratica.getStato())).append("\",");
            
            String dataAperturaFmt = pratica.getDataApertura() != null ? sdf.format(pratica.getDataApertura()) : "";
            json.append("\"dataApertura\":\"").append(dataAperturaFmt).append("\",");
            
            String adminStr = pratica.getAdminAssegnato() != null ? escapeJson(pratica.getAdminAssegnato()) : "";
            json.append("\"adminAssegnato\":\"").append(adminStr).append("\",");

         // Array dei messaggi (Ticket)
            json.append("\"messaggi\":[");
            for (int i = 0; i < messaggi.size(); i++) {
                Ticket t = messaggi.get(i);
                json.append("{");
                
                // Determina se l'autore del messaggio è l'utente attualmente loggato
                boolean isMine = t.getAutore().equals(utenteLoggato.getUsername());
                String displayAutore = isMine ? "Tu" : t.getAutore();
                
                json.append("\"autore\":\"").append(escapeJson(displayAutore)).append("\",");
                json.append("\"isMine\":").append(isMine).append(",");
                json.append("\"tipo\":\"").append(escapeJson(t.getTipo())).append("\",");
                json.append("\"testo\":\"").append(escapeJson(t.getMessaggio())).append("\",");
                
                String dataTicketFmt = t.getDataTicket() != null ? sdf.format(t.getDataTicket()) : "";
                json.append("\"data\":\"").append(dataTicketFmt).append("\"");
                
                json.append("}");
                if (i < messaggi.size() - 1) json.append(",");
            }
            json.append("]");
            json.append("}");

            out.print(json.toString());
            out.flush();

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}