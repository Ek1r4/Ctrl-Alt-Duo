package reframe.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import reframe.model.beans.PraticaAssistenza;
import reframe.model.beans.Utente;
import reframe.model.dao.PraticaAssistenzaDAO;

@WebServlet("/ListaPraticheServlet")
public class ListaPraticheServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private String escapeJson(String data) {
        if (data == null) return "";
        return data.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        int ruolo = utente.getIsAdmin();
        String queryRicerca = request.getParameter("q");
        if (queryRicerca == null) queryRicerca = "";
        String q = queryRicerca.toLowerCase().trim();

        PraticaAssistenzaDAO dao = new PraticaAssistenzaDAO();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            // Estraiamo TUTTE le pratiche dal DB (il DAO dovrebbe avere un metodo tipo doRetrieveAll())
            // Se il tuo metodo si chiama diversamente, adattalo qui sotto.
            List<PraticaAssistenza> tuttePratiche = dao.doRetrieveAll(null, null); 
            List<PraticaAssistenza> filtrate = new ArrayList<>();

            for (PraticaAssistenza p : tuttePratiche) {
                // 1. Filtro di Base per Ruolo
                if (ruolo == 0 && !p.getIdUtente().equals(utente.getUsername())) continue;
                if (ruolo == 1 && !utente.getUsername().equals(p.getAdminAssegnato())) continue;

                // 2. Filtro della Barra di Ricerca
                boolean match = false;
                if (q.isEmpty()) {
                    match = true;
                } else {
                    // Ricerca base per tutti: RMA, Titolo, Categoria, Stato
                    if (p.getRma().toLowerCase().contains(q) ||
                        p.getTitolo().toLowerCase().contains(q) ||
                        p.getCategoria().toLowerCase().contains(q) ||
                        p.getStato().toLowerCase().contains(q)) {
                        match = true;
                    }
                    // Ricerca aggiuntiva per Admin e Superadmin: Utente
                    if (!match && ruolo >= 1 && p.getIdUtente() != null && p.getIdUtente().toLowerCase().contains(q)) {
                        match = true;
                    }
                    // Ricerca esclusiva per Superadmin: Admin Assegnato (incluso "da assegnare")
                    if (!match && ruolo == 2 && p.getAdminAssegnato() != null && p.getAdminAssegnato().toLowerCase().contains(q)) {
                        match = true;
                    }
                }

                if (match) {
                    filtrate.add(p);
                }
            }

         // 3. Ordinamento (Superadmin: "Da assegnare" in cima, poi Data decrescente)
            Collections.sort(filtrate, new Comparator<PraticaAssistenza>() {
                public int compare(PraticaAssistenza p1, PraticaAssistenza p2) {
                    if (ruolo == 2) {
                        // Un ticket è "Da assegnare" se il campo è null, vuoto, o contiene la stringa esatta
                        boolean isDaAssegnare1 = (p1.getAdminAssegnato() == null || p1.getAdminAssegnato().trim().isEmpty() || "Da assegnare".equalsIgnoreCase(p1.getAdminAssegnato()));
                        boolean isDaAssegnare2 = (p2.getAdminAssegnato() == null || p2.getAdminAssegnato().trim().isEmpty() || "Da assegnare".equalsIgnoreCase(p2.getAdminAssegnato()));
                        
                        if (isDaAssegnare1 && !isDaAssegnare2) return -1;
                        if (!isDaAssegnare1 && isDaAssegnare2) return 1;
                    }
                    
                    // A parità di assegnazione, si ordina per data decrescente
                    if (p1.getDataApertura() == null && p2.getDataApertura() == null) return 0;
                    if (p1.getDataApertura() == null) return 1;
                    if (p2.getDataApertura() == null) return -1;
                    
                    return p2.getDataApertura().compareTo(p1.getDataApertura()); // DESC
                }
            });

            // 4. Costruzione Manuale JSON
            PrintWriter out = response.getWriter();
            StringBuilder json = new StringBuilder();

            json.append("{");
            json.append("\"ruolo\":").append(ruolo).append(",");
            json.append("\"risultati\":[");

            for (int i = 0; i < filtrate.size(); i++) {
                PraticaAssistenza p = filtrate.get(i);
                json.append("{");
                json.append("\"rma\":\"").append(escapeJson(p.getRma())).append("\",");
                json.append("\"titolo\":\"").append(escapeJson(p.getTitolo())).append("\",");
                json.append("\"utente\":\"").append(escapeJson(p.getIdUtente())).append("\",");
                json.append("\"admin\":\"").append(escapeJson(p.getAdminAssegnato() != null ? p.getAdminAssegnato() : "Da assegnare")).append("\",");
                json.append("\"stato\":\"").append(escapeJson(p.getStato())).append("\",");
                
                String dataStr = p.getDataApertura() != null ? sdf.format(p.getDataApertura()) : "";
                json.append("\"data\":\"").append(dataStr).append("\"");
                
                json.append("}");
                if (i < filtrate.size() - 1) json.append(",");
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