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

    // Metodo di utility per sanificare le stringhe durante la serializzazione JSON manuale
    private String escapeJson(String data) {
        if (data == null) return "";
        return data.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        /* SETUP E CONTROLLO ACCESSI */
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
            /* RECUPERO DATI E FILTRAGGIO (RBAC E RICERCA) */
            List<PraticaAssistenza> tuttePratiche = dao.doRetrieveAll(null, null); 
            List<PraticaAssistenza> filtrate = new ArrayList<>();

            for (PraticaAssistenza p : tuttePratiche) {
                
                // RBAC: Restringe la visibilità del record in base ai privilegi dell'utente.
                // Ruolo 0 (Cliente): limitato alle proprie pratiche. Ruolo 1 (Admin): limitato alle pratiche a lui assegnate.
                if (ruolo == 0 && !p.getIdUtente().equals(utente.getUsername())) continue;
                if (ruolo == 1 && !utente.getUsername().equals(p.getAdminAssegnato())) continue;

                boolean match = false;
                if (q.isEmpty()) {
                    match = true;
                } else {
                    // Applicazione filtri di ricerca progressivi basati sui campi base
                    if (p.getRma().toLowerCase().contains(q) ||
                        p.getTitolo().toLowerCase().contains(q) ||
                        p.getCategoria().toLowerCase().contains(q) ||
                        p.getStato().toLowerCase().contains(q)) {
                        match = true;
                    }
                    
                    // Ricerca estesa ai riferimenti dell'utente, concessa solo a privilegi >= 1
                    if (!match && ruolo >= 1 && p.getIdUtente() != null && p.getIdUtente().toLowerCase().contains(q)) {
                        match = true;
                    }
                    
                    // Ricerca estesa all'amministratore in carico, esclusiva per il Superadmin (Livello 2)
                    if (!match && ruolo == 2 && p.getAdminAssegnato() != null && p.getAdminAssegnato().toLowerCase().contains(q)) {
                        match = true;
                    }
                }

                if (match) {
                    filtrate.add(p);
                }
            }

            /* ORDINAMENTO RISULTATI */
            Collections.sort(filtrate, new Comparator<PraticaAssistenza>() {
                public int compare(PraticaAssistenza p1, PraticaAssistenza p2) {
                    
                    // Override per Superadmin: forza i ticket "Da assegnare" all'apice dei risultati scavalcando l'ordinamento cronologico
                    if (ruolo == 2) {
                        boolean isDaAssegnare1 = (p1.getAdminAssegnato() == null || p1.getAdminAssegnato().trim().isEmpty() || "Da assegnare".equalsIgnoreCase(p1.getAdminAssegnato()));
                        boolean isDaAssegnare2 = (p2.getAdminAssegnato() == null || p2.getAdminAssegnato().trim().isEmpty() || "Da assegnare".equalsIgnoreCase(p2.getAdminAssegnato()));
                        
                        if (isDaAssegnare1 && !isDaAssegnare2) return -1;
                        if (!isDaAssegnare1 && isDaAssegnare2) return 1;
                    }
                    
                    if (p1.getDataApertura() == null && p2.getDataApertura() == null) return 0;
                    if (p1.getDataApertura() == null) return 1;
                    if (p2.getDataApertura() == null) return -1;
                    
                    return p2.getDataApertura().compareTo(p1.getDataApertura()); 
                }
            });

            /* COSTRUZIONE E SERIALIZZAZIONE JSON */
            PrintWriter out = response.getWriter();
            StringBuilder json = new StringBuilder();

            json.append("{");
            json.append("\"ruolo\":").append(ruolo).append(",");
            json.append("\"risultati\":[");

            // Costruzione iterativa dell'array JSON dei risultati filtrati e ordinati
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