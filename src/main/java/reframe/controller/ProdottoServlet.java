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
        // 1. Inizializziamo il DAO subito
        ProdottoDAO dao = new ProdottoDAO();
        
        // 2. Recuperiamo i parametri (se sono null, le variabili saranno null, è normale)
        String isAjax = request.getParameter("ajax");
        String[] marcheScelte = request.getParameterValues("marca");
        String[] prezziScelti = request.getParameterValues("prezzo");
        String searchTesto = request.getParameter("search");
        String tipoFiltro = request.getParameter("tipo");
        
        try {
            List<Prodotto> catalogo;
            
            // 3. Logica di filtraggio
            boolean hasFiltri = (marcheScelte != null && marcheScelte.length > 0) || 
                                (prezziScelti != null && prezziScelti.length > 0) ||
                                (searchTesto != null && !searchTesto.trim().isEmpty());
            
            if (hasFiltri) {
                catalogo = dao.fetchProdottiFiltrati(marcheScelte, prezziScelti, searchTesto);
                request.setAttribute("titoloVetrina", "Risultati Ricerca");
            } else if (tipoFiltro != null && !tipoFiltro.trim().isEmpty()) {
                catalogo = dao.fetchProdottiByTipo(tipoFiltro);
                request.setAttribute("titoloVetrina", "FOTOCAMERE DI TIPO " + tipoFiltro);
            } else {
                catalogo = dao.fetchAllProdotti();
                request.setAttribute("titoloVetrina", "Tutto il Catalogo");
            }
            
            request.setAttribute("listaProdotti", catalogo);
            
            // 4. Recupero marche per la sidebar (ATTENZIONE: questo deve stare qui!)
            // Se dao è null, qui avresti la NullPointerException
            List<String> marcheDisponibili = dao.fetchDistinctMarche(); 
            request.setAttribute("marcheDisponibili", marcheDisponibili);
            
            // ... resto del codice ...
            
            // INTERCETTAZIONE AJAX: Restituiamo solo il frammento HTML senza ricaricare la pagina
            if ("true".equals(isAjax)) {
                // Percorso corretto partendo dalla root dell'applicazione, senza contextPath
                request.getRequestDispatcher("/WEB-INF/components/griglia-prodotti.jsp").forward(request, response);
                return; // Ferma l'esecuzione per non inviare anche l'header e il footer
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("erroreDatabase", "Errore di caricamento del catalogo.");
        }

        // Normale caricamento della pagina intera (es. prima visita o refresh manuale col tasto F5)
        request.getRequestDispatcher("/vetrina.jsp").forward(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // INTERCETTAZIONE AZIONE: ELIMINA PRODOTTO (Lato Admin)
        if ("delete".equals(action)) {
            
            // 1. Controllo di sicurezza per accertarsi che chi richiede l'eliminazione sia un Admin loggato
            Utente utenteLoggato = (Utente) request.getSession().getAttribute("utente");
            if (utenteLoggato == null || utenteLoggato.getIsAdmin() == 0) {
                response.sendRedirect(request.getContextPath() + "/accessoNegato.jsp");
                return;
            }
            
            // 2. Recupero ID del prodotto da eliminare
            String idProdotto = request.getParameter("idProdotto");
            if (idProdotto != null && !idProdotto.trim().isEmpty()) {
                ProdottoDAO dao = new ProdottoDAO();
                try {
                    dao.deleteProdotto(idProdotto);
                    response.sendRedirect(request.getContextPath() + "/ProdottoServlet?success=eliminato");
                } catch (SQLException e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/ProdottoServlet?erroreDatabase=Impossibile eliminare il prodotto.");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/ProdottoServlet");
            }
            
            return; // Ferma l'esecuzione per evitare di procedere con altro codice doPost
        }
        
    }
}