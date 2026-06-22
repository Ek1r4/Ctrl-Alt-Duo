package reframe.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import reframe.model.beans.Prodotto;
import reframe.model.dao.ProdottoDAO;
import reframe.model.beans.Utente;

@WebServlet("/ProdottoServlet")
public class ProdottoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProdottoDAO dao = new ProdottoDAO();
        
        // Controlliamo se l'utente ha cliccato su una categoria specifica (es. "Ricondizionate")
        String tipoFiltro = request.getParameter("tipo");
        
        try {
            List<Prodotto> catalogo;
            
            if (tipoFiltro != null && !tipoFiltro.trim().isEmpty()) {
                // Filtriamo per tipo
                catalogo = dao.fetchProdottiByTipo(tipoFiltro);
                request.setAttribute("titoloVetrina", "Fotocamere di tipo " + tipoFiltro);
            } else {
                // Nessun filtro: mostriamo tutto il catalogo
                catalogo = dao.fetchAllProdotti();
                request.setAttribute("titoloVetrina", "Tutto il Catalogo");
            }
            
            // Inseriamo la lista nella request per farla leggere alla JSP
            request.setAttribute("listaProdotti", catalogo);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("erroreDatabase", "Ci scusiamo, impossibile caricare il catalogo in questo momento.");
        }

        // Passiamo la palla all'interfaccia grafica
        request.getRequestDispatcher("/vetrina.jsp").forward(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // INTERCETTAZIONE AZIONE: ELIMINA PRODOTTO
        if ("delete".equals(action)) {
            
            // 1. CONTROLLO DI SICUREZZA MANUALE (Fondamentale!)
            Utente utenteLoggato = (Utente) request.getSession().getAttribute("utente");
            if (utenteLoggato == null || utenteLoggato.getIsAdmin() == 0) {
                // Se non sei loggato o non sei admin, ti sbatto fuori
                response.sendRedirect(request.getContextPath() + "/accessoNegato.jsp");
                return;
            }
            
            // 2. RECUPERO ID ED ELIMINAZIONE
            String idProdotto = request.getParameter("idProdotto");
            if (idProdotto != null && !idProdotto.trim().isEmpty()) {
                reframe.model.dao.ProdottoDAO dao = new reframe.model.dao.ProdottoDAO();
                try {
                    dao.deleteProdotto(idProdotto);
                    response.sendRedirect(request.getContextPath() + "/ProdottoServlet?success=eliminato");
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/ProdottoServlet?erroreDatabase=Impossibile eliminare il prodotto.");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/ProdottoServlet");
            }
            
            return;
        }
        
        
        // ... Qui sotto ci sarà il resto del tuo codice doPost (es. per inserire un prodotto o per le recensioni) ...
        
    }
}