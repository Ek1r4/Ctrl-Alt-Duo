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

    /* GESTIONE RICHIESTE GET (VISUALIZZAZIONE E FILTRAGGIO CATALOGO) */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
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
            
            // Determinazione dinamica del dataset e del titolo della vetrina in base ai filtri applicati
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
            
            // Sanitizzazione lato server del catalogo: omette dinamicamente dalla visualizzazione i prodotti esauriti
            if (catalogo != null) {
                catalogo.removeIf(p -> p.getInStock() <= 0);
            }
            
            request.setAttribute("listaProdotti", catalogo);
            
            List<String> marcheDisponibili = dao.fetchDistinctMarche(); 
            request.setAttribute("marcheDisponibili", marcheDisponibili);
            
            // Gestione del rendering parziale (Pattern AJAX/Fragment) per aggiornare solo la griglia prodotti nel DOM senza ricaricare la pagina
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
    
    /* GESTIONE RICHIESTE POST (AMMINISTRAZIONE PRODOTTI) */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // RBAC (Role-Based Access Control): blocca le operazioni di scrittura a database per utenti non loggati o sprovvisti di privilegi amministrativi
        Utente utenteLoggato = (Utente) request.getSession().getAttribute("utente");
        if (utenteLoggato == null || utenteLoggato.getIsAdmin() == 0) {
            response.sendRedirect(request.getContextPath() + "/accessoNegato.jsp");
            return;
        }

        String action = request.getParameter("action");
        ProdottoDAO dao = new ProdottoDAO();
        
        /* AZIONE: ELIMINAZIONE PRODOTTO */
        if ("delete".equals(action)) {
            String idProdotto = request.getParameter("idProdotto");
            
            if (idProdotto != null && !idProdotto.trim().isEmpty()) {
                try {
                    dao.deleteProdotto(idProdotto);
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
        
        /* AZIONE: RIPRISTINO PRODOTTO */
        if ("ripristina".equals(action)) {
            String idProdotto = request.getParameter("idProdotto");
            
            try {
                dao.ripristinaProdotto(idProdotto);
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?tab=prodotti&success=prodottoRipristinato");
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?tab=prodotti&errore=ripristino_fallito");
            }
            return;
        }
    
        /* AZIONE: MODIFICA PRODOTTO */
        if ("edit".equals(action)) {
            try {
                String idProdotto = request.getParameter("idProdotto");
                String nome = request.getParameter("nome");
                String prezzoStr = request.getParameter("prezzo");
                String stockStr = request.getParameter("stock");
                String tipo = request.getParameter("tipo");
                String descrizione = request.getParameter("descrizione");
                
                String stato = request.getParameter("stato");
                String scattiStr = request.getParameter("numeroScatti");
                String condizione = request.getParameter("condizioneCollezionistica");
                
                double prezzo = Double.parseDouble(prezzoStr);
                int stock = Integer.parseInt(stockStr);

                Prodotto prodottoDaModificare = dao.fetchProdottoById(idProdotto); 
                
                if (prodottoDaModificare != null) {
                    prodottoDaModificare.setNome(nome);
                    prodottoDaModificare.setPrezzo(prezzo);
                    prodottoDaModificare.setInStock(stock);
                    prodottoDaModificare.setTipo(tipo);
                    prodottoDaModificare.setDescrizione(descrizione);
                    
                    // Normalizzazione dei metadati specifici: azzera i campi correlati alle sottocategorie ("Usato", "Collezione")
                    // per evitare la persistenza di dati incoerenti a database nel caso in cui la categoria principale venga modificata dall'admin.
                    prodottoDaModificare.setStato(null);
                    prodottoDaModificare.setNumeroScatti(Integer.valueOf(0)); 
                    prodottoDaModificare.setCondizioneCollezionistica(null);
                    
                    // Ripopolamento condizionale vincolato al tipo di prodotto confermato
                    if ("Usato".equalsIgnoreCase(tipo)) {
                        prodottoDaModificare.setStato(stato);
                        if (scattiStr != null && !scattiStr.trim().isEmpty()) {
                            prodottoDaModificare.setNumeroScatti(Integer.parseInt(scattiStr));
                        }
                    } else if ("Collezione".equalsIgnoreCase(tipo)) {
                        prodottoDaModificare.setCondizioneCollezionistica(condizione);
                    }
                    
                    dao.updateProdotto(prodottoDaModificare); 
                }
                
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?success=modificato");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?errore=Modifica fallita");
            }
            return;
        }
        
        // Fallback catch-all per manipolazioni errate del parametro action
        response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet");
    }
}