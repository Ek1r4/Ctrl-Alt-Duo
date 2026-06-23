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

@WebServlet("/ProdottoServlet")
public class ProdottoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProdottoDAO prodottoDAO = new ProdottoDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Prodotto> prodotti = prodottoDAO.fetchAllProdotti();
            request.setAttribute("listaProdotti", prodotti);
            request.getRequestDispatcher("/admin/gestione-prodotti.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Errore nel recupero dei prodotti: " + e.getMessage());
            request.getRequestDispatcher("/500.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("delete".equals(action)) {
                String id = request.getParameter("idProdotto");
                prodottoDAO.deleteProdotto(id);
                request.setAttribute("successMessage", "Prodotto eliminato con successo dal catalogo!");
                
            } else if ("insert".equals(action)) {
                Prodotto p = new Prodotto();
                
             // Genera 5 caratteri alfanumerici casuali univoci (es. "4F2A1")
                String uniquePart = java.util.UUID.randomUUID().toString().substring(0, 5).toUpperCase();

                // Risultato finale di esattamente 8 caratteri (es. "PRD4F2A1")
                p.setId("PRD" + uniquePart);
                
                p.setMarchio(request.getParameter("marchio"));
                p.setSeriale(request.getParameter("seriale"));
                p.setPrezzo(Double.parseDouble(request.getParameter("prezzo")));
                p.setNome(request.getParameter("nome"));
                p.setDescrizione(request.getParameter("descrizione"));
                p.setInStock(Integer.parseInt(request.getParameter("stock")));
                
                // Campi di default per il test d'inserimento rapido
                p.setTipo("Nuovo"); 
                p.setIva(Integer.parseInt(request.getParameter("iva")));
                
                prodottoDAO.insertProdotto(p);
                request.setAttribute("successMessage", "Nuovo prodotto inserito con successo!");
            }
            
            // Ricarica del catalogo aggiornato
            List<Prodotto> prodotti = prodottoDAO.fetchAllProdotti();
            request.setAttribute("listaProdotti", prodotti);
            request.getRequestDispatcher("/admin/gestione-prodotto.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Errore nell'operazione sul database: " + e.getMessage());
            request.getRequestDispatcher("/500.jsp").forward(request, response);
        }
    }
}