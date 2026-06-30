package reframe.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import reframe.model.beans.Ticket;
import reframe.model.beans.Utente;
import reframe.model.dao.TicketDAO;

@WebServlet("/AssistenzaServlet")
public class AssistenzaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if ("cerca".equals(action)) {
            String query = request.getParameter("query");      // Per la barra di ricerca
            String categoria = request.getParameter("categoria"); // Per i bottoni rapidi
            TicketDAO dao = new TicketDAO();
            
            try {
                List<Ticket> listaTicket;
                if (categoria != null && !categoria.isEmpty()) {
                    listaTicket = dao.doRetrieveByCategoria(categoria);
                } else {
                    listaTicket = dao.doRetrieveByTitolo(query);
                }
                
                request.setAttribute("pratiche", listaTicket);
                request.setAttribute("ricercaEffettuata", true);
                request.getRequestDispatcher("/common/centroAssistenza.jsp").forward(request, response);
            }
                catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/500.jsp");
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("creaTicket".equals(action)) {
            // Esempio logica salvataggio
            Ticket nuovoTicket = new Ticket();
            nuovoTicket.setIdTicket("T-" + System.currentTimeMillis()); // Generatore semplice
            nuovoTicket.setTestoMessaggio(request.getParameter("descrizione"));
            nuovoTicket.setDataTicket(new Timestamp(System.currentTimeMillis()));
            nuovoTicket.setRmaPratica(request.getParameter("rma"));
            nuovoTicket.setAutoreMessaggio(((Utente)request.getSession().getAttribute("utente")).getUsername());
            
            TicketDAO dao = new TicketDAO();
            try {
                dao.doSave(nuovoTicket);
                response.sendRedirect(request.getContextPath() + "/common/centroAssistenza.jsp?success=ticketCreato");
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/500.jsp");
            }
        }
    }
}