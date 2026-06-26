package reframe.controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import reframe.model.beans.Prodotto;
import reframe.model.dao.ProdottoDAO;
import reframe.model.dao.RecensioniDAO;

@WebServlet("/DettaglioProdottoServlet")
public class DettaglioProdottoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Recuperiamo l'ID del prodotto passato nell'URL (es. ?idProdotto=NEW-001)
        String idProdotto = request.getParameter("idProdotto");

        if (idProdotto != null && !idProdotto.trim().isEmpty()) {
            ProdottoDAO dao = new ProdottoDAO();
            try {
                // 2. Cerchiamo il prodotto nel database
                Prodotto prodotto = dao.fetchProdottoById(idProdotto);
                
                // 3. Lo salviamo nella request per farlo leggere alla JSP
                request.setAttribute("prodotto", prodotto);
                
            } catch (SQLException e) {
                e.printStackTrace();
                // In caso di errore SQL, il prodotto rimarrà null e la JSP mostrerà il messaggio "Prodotto non trovato"
            }
        }
        
        RecensioniDAO recDao = new RecensioniDAO();
     // Passa le recensioni alla request usando il nome esatto che il fragment JSP si aspetta di leggere
        try {
			request.setAttribute("recensioniProdotto", recDao.doRetrieveByProdotto(idProdotto));
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        // 4. Inoltriamo alla pagina del dettaglio
        request.getRequestDispatcher("/dettaglioProdotto.jsp").forward(request, response);
    }
}