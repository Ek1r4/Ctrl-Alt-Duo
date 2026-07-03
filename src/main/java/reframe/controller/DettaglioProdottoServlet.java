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
        
        /* RECUPERO DETTAGLI PRODOTTO */
        String idProdotto = request.getParameter("idProdotto");

        if (idProdotto != null && !idProdotto.trim().isEmpty()) {
            ProdottoDAO dao = new ProdottoDAO();
            try {
                Prodotto prodotto = dao.fetchProdottoById(idProdotto);
                request.setAttribute("prodotto", prodotto);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        /* RECUPERO RECENSIONI ASSOCIATE */
        RecensioniDAO recDao = new RecensioniDAO();
        try {
            request.setAttribute("recensioniProdotto", recDao.doRetrieveByProdotto(idProdotto));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        request.getRequestDispatcher("/dettaglioProdotto.jsp").forward(request, response);
    }
}