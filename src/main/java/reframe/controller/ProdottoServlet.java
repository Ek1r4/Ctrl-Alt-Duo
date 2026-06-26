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
        // AZIONE: MODIFICA PRODOTTO
        // ==========================================
        if ("edit".equals(action)) {
            try {
                // 1. Recupero i dati base
                String idProdotto = request.getParameter("idProdotto");
                String nome = request.getParameter("nome");
                String prezzoStr = request.getParameter("prezzo");
                String stockStr = request.getParameter("stock");
                String tipo = request.getParameter("tipo");
                String descrizione = request.getParameter("descrizione");
                
                // Recupero i nuovi campi specifici
                String stato = request.getParameter("stato");
                String scattiStr = request.getParameter("numeroScatti");
                String condizione = request.getParameter("condizioneCollezionistica");
                
                double prezzo = Double.parseDouble(prezzoStr);
                int stock = Integer.parseInt(stockStr);

                // 2. Recupero il prodotto originale
                Prodotto prodottoDaModificare = dao.fetchProdottoById(idProdotto); 
                
                if (prodottoDaModificare != null) {
                    // 3. Sovrascrivo i dati di base
                    prodottoDaModificare.setNome(nome);
                    prodottoDaModificare.setPrezzo(prezzo);
                    prodottoDaModificare.setInStock(stock);
                    prodottoDaModificare.setTipo(tipo);
                    prodottoDaModificare.setDescrizione(descrizione);
                    
                    // 4. Reset dei campi specifici per tenere il DB pulito
                    prodottoDaModificare.setStato(null);
                    prodottoDaModificare.setNumeroScatti(Integer.valueOf(0)); 
                    prodottoDaModificare.setCondizioneCollezionistica(null);
                    
                    // 5. Assegnazione condizionale in base al Tipo
                    if ("Usato".equalsIgnoreCase(tipo)) {
                        prodottoDaModificare.setStato(stato);
                        if (scattiStr != null && !scattiStr.trim().isEmpty()) {
                            prodottoDaModificare.setNumeroScatti(Integer.parseInt(scattiStr));
                        }
                    } else if ("Collezione".equalsIgnoreCase(tipo)) {
                        prodottoDaModificare.setCondizioneCollezionistica(condizione);
                    }
                    
                    // 6. Salvo su DB
                    dao.updateProdotto(prodottoDaModificare); 
                }
                
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?success=modificato");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?errore=Modifica fallita");
            }
            return;
        }
        // Se arriva un'azione non riconosciuta, rimandiamo alla dashboard
        response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet");
    }
}