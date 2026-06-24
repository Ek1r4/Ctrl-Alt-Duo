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
        // --- LOGICA DI VISUALIZZAZIONE CATALOGO (Invariata) ---
        ProdottoDAO dao = new ProdottoDAO();
        
        String isAjax = request.getParameter("ajax");
        String[] marcheScelte = request.getParameterValues("marca");
        String[] prezziScelti = request.getParameterValues("prezzo");
        String searchTesto = request.getParameter("search");
        String tipoFiltro = request.getParameter("tipo");
        
        try {
            List<Prodotto> catalogo;
            
            boolean hasFiltri = (marcheScelte != null && marcheScelte.length > 0) || 
                                (prezziScelti != null && prezziScelti.length > 0) ||
                                (searchTesto != null && !searchTesto.trim().isEmpty());
            
            if (hasFiltri) {
            	catalogo = dao.fetchProdottiFiltrati(marcheScelte, prezziScelti, searchTesto, tipoFiltro);
                request.setAttribute("titoloVetrina", "Risultati Ricerca");
            } else if (tipoFiltro != null && !tipoFiltro.trim().isEmpty()) {
                catalogo = dao.fetchProdottiByTipo(tipoFiltro);
                request.setAttribute("titoloVetrina", "FOTOCAMERE DI TIPO " + tipoFiltro);
            } else {
                catalogo = dao.fetchAllProdotti();
                request.setAttribute("titoloVetrina", "Tutto il Catalogo");
            }
            
            request.setAttribute("listaProdotti", catalogo);
            
            List<String> marcheDisponibili = dao.fetchDistinctMarche(); 
            request.setAttribute("marcheDisponibili", marcheDisponibili);
            
            if ("true".equals(isAjax)) {
                request.getRequestDispatcher("/WEB-INF/components/griglia-prodotti.jsp").forward(request, response);
                return; 
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("erroreDatabase", "Errore di caricamento del catalogo.");
        }

        request.getRequestDispatcher("/vetrina.jsp").forward(request, response);
    }
    
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. CONTROLLO SICUREZZA GLOBALE PER TUTTE LE AZIONI POST
        // Nessun utente normale o non loggato deve poter aggiungere o eliminare nulla.
        Utente utenteLoggato = (Utente) request.getSession().getAttribute("utente");
        if (utenteLoggato == null || utenteLoggato.getIsAdmin() == 0) {
            response.sendRedirect(request.getContextPath() + "/accessoNegato.jsp");
            return;
        }

        String action = request.getParameter("action");
        ProdottoDAO dao = new ProdottoDAO();
        
        
        // ==========================================
        // AZIONE: ELIMINA PRODOTTO
        // ==========================================
        if ("delete".equals(action)) {
            String idProdotto = request.getParameter("idProdotto");
            
            if (idProdotto != null && !idProdotto.trim().isEmpty()) {
                try {
                    dao.deleteProdotto(idProdotto);
                    // Modificato: Ora rimanda alla Dashboard Admin e non più alla Vetrina!
                    response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?success=eliminato");
                } catch (SQLException e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?errore=Impossibile eliminare il prodotto");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet");
            }
            return; 
        }
        
        
        // ==========================================
        // AZIONE: AGGIUNGI PRODOTTO
        // ==========================================
        if ("add".equals(action)) {
            try {
                // Recupero i dati inviati dal form della Dashboard
                String idProdotto = request.getParameter("idProdotto");
                String seriale = request.getParameter("seriale");
                String marchio = request.getParameter("marchio");
                String nome = request.getParameter("nome");
                String prezzoStr = request.getParameter("prezzo");
                String tipo = request.getParameter("tipo");
                String descrizione = request.getParameter("descrizione");
                
                // Conversione dei dati numerici (se lo stock non c'è, di base mettiamo 1)
                double prezzo = Double.parseDouble(prezzoStr);
                String stockStr = request.getParameter("stock");
                int stock = (stockStr != null && !stockStr.isEmpty()) ? Integer.parseInt(stockStr) : 1;

                // Creazione del bean e salvataggio
                Prodotto nuovoProdotto = new Prodotto();
                nuovoProdotto.setId(idProdotto);
                nuovoProdotto.setSeriale(seriale);
                nuovoProdotto.setMarchio(marchio);
                nuovoProdotto.setNome(nome);
                nuovoProdotto.setPrezzo(prezzo);
                nuovoProdotto.setTipo(tipo);
                nuovoProdotto.setDescrizione(descrizione);
                nuovoProdotto.setInStock(stock);
                
                // Assicurati che nel tuo ProdottoDAO esista un metodo chiamato doSave (o modificalo col nome corretto)
                dao.insertProdotto(nuovoProdotto); 
                
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?success=aggiunto");
                
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?errore=Dati non validi");
            }
            return;
        }

        // Se arriva un'azione non riconosciuta, rimandiamo alla dashboard
        response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet");
    }
}