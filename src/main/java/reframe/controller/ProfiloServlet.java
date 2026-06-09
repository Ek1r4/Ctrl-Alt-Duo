package reframe.controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import reframe.model.beans.Utente;
import reframe.model.beans.Pagamento;
import reframe.model.beans.Spedizione;
import reframe.model.dao.UtenteDAO;
import reframe.model.dao.PagamentoDAO;
import reframe.model.dao.SpedizioneDAO;
import reframe.utils.HashingPassword;

@WebServlet("/ProfiloServlet")
public class ProfiloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private UtenteDAO utenteDAO;
    private PagamentoDAO pagamentoDAO;
    private SpedizioneDAO spedizioneDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.utenteDAO = new UtenteDAO();
        this.pagamentoDAO = new PagamentoDAO();
        this.spedizioneDAO = new SpedizioneDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Se si accede con GET, si viene indirizzati alla pagina del profilo
        HttpSession session = request.getSession();
        if (session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        request.getRequestDispatcher("/common/profilo.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        // Controllo di sicurezza
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");
        
        if (utenteLoggato == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Recupero dell'input nascosto inviato dal form specifico
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/common/profilo.jsp");
            return;
        }

        // Smistamento dell'operazione basato sull'input nascosto
        switch (action) {
            case "aggiornaAnagrafica":
                gestisciAnagrafica(request, response, utenteLoggato, session);
                break;
                
            case "cambioPassword":
                gestisciPassword(request, response, utenteLoggato);
                break;
                
            case "salvaSpedizione":
                gestisciSpedizione(request, response, utenteLoggato);
                break;
                
            case "salvaPagamento":
                gestisciPagamento(request, response, utenteLoggato);
                break;
                
            default:
                response.sendRedirect(request.getContextPath() + "/common/profilo.jsp");
                break;
        }
    }

       // METODI PRIVATI PER LA GESTIONE DELLE SINGOLE AZIONI

    private void gestisciAnagrafica(HttpServletRequest request, HttpServletResponse response, Utente utenteLoggato, HttpSession session) throws ServletException, IOException {
        String telefono = request.getParameter("telefono");
        String bio = request.getParameter("bio");

        if (telefono == null || telefono.trim().isEmpty() || !telefono.matches("^[0-9]{10}$")) {
            response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=telefonoObbligatorio");
            return;
        }

        utenteLoggato.setTelefono(telefono.trim());
        utenteLoggato.setBio(bio != null ? bio.trim() : "");

        try {
            boolean successo = utenteDAO.doUpdate(utenteLoggato);
            if (successo) {
                session.setAttribute("utenteLoggato", utenteLoggato);
                response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?success=anagrafica");
            } else {
                response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=updateFallito");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=db");
        }
    }

    private void gestisciPassword(HttpServletRequest request, HttpServletResponse response, Utente utenteLoggato) throws ServletException, IOException 
    {
        String vecchiaPassword = request.getParameter("vecchiaPassword");
        String nuovaPassword = request.getParameter("nuovaPassword");
        String confermaPassword = request.getParameter("confermaPassword");

        // Controlli di corrispondenza e robustezza della nuova password
        if (nuovaPassword == null || nuovaPassword.length() < 8 || !nuovaPassword.equals(confermaPassword)) {
            response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=passwordInvalida");
            return;
        }
        
        String vecchiaPasswordCriptata = HashingPassword.hashPassword(vecchiaPassword);

        // Controllo di sicurezza: verifica della password attuale dell'utente
        if (vecchiaPassword == null || !utenteLoggato.getPassword().equals(vecchiaPasswordCriptata)) {
            response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=vecchiaPasswordErrata");
            return;
        }
        
        String nuovaPasswordCriptata = HashingPassword.hashPassword(nuovaPassword);

        try {
            boolean successo = utenteDAO.updatePassword(utenteLoggato.getEmail(), nuovaPasswordCriptata);
            if (successo) {
                utenteLoggato.setPassword(nuovaPasswordCriptata);
                response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?success=passwordModificata");
            } else {
                response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=cambioPasswordFallito");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=db");
        }
    }

    private void gestisciSpedizione(HttpServletRequest request, HttpServletResponse response, Utente utenteLoggato) throws ServletException, IOException 
    {
        String citta = request.getParameter("citta");
        String provincia = request.getParameter("provincia");
        String paese = request.getParameter("paese");
        String cap = request.getParameter("cap");
        String via = request.getParameter("via");
        String civico = request.getParameter("civico");
        String note = request.getParameter("note");

        Spedizione nuovaSpedizione = new Spedizione();
        nuovaSpedizione.setIdUtente(utenteLoggato.getUsername()); // Legame di Foreign Key sicuro
        nuovaSpedizione.setCitta(citta);
        nuovaSpedizione.setProvincia(provincia);
        nuovaSpedizione.setPaese(paese);
        nuovaSpedizione.setCap(cap);
        nuovaSpedizione.setVia(via);
        nuovaSpedizione.setCivico(civico);
        nuovaSpedizione.setNote(note);

        try {
            boolean successo = spedizioneDAO.doSave(nuovaSpedizione);
            if (successo) {
                response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?success=spedizioneSalvata");
            } else {
                response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=salvataggioSpedizioneFallito");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=db");
        }
    }

    private void gestisciPagamento(HttpServletRequest request, HttpServletResponse response, Utente utenteLoggato) throws ServletException, IOException 
    {
        String nomeIntestatario = request.getParameter("nomeIntestatario");
        String circuito = request.getParameter("circuito");
        String numeroCarta = request.getParameter("numeroCarta");
        String dataScadenza = request.getParameter("dataScadenza");
        String cvv = request.getParameter("cvv");

        Pagamento nuovoPagamento = new Pagamento();
        nuovoPagamento.setIdUtente(utenteLoggato.getUsername()); // Legame di Foreign Key sicuro
        nuovoPagamento.setNomeIntestatario(nomeIntestatario);
        nuovoPagamento.setCircuito(circuito);
        nuovoPagamento.setNumeroCarta(numeroCarta);
        nuovoPagamento.setDataScadenza(dataScadenza);
        nuovoPagamento.setCvv(cvv);

        try {
            boolean successo = pagamentoDAO.doSave(nuovoPagamento);
            if (successo) {
                response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?success=pagamentoSalvato");
            } else {
                response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=salvataggioPagamentoFallito");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gestione dei vincoli CHECK impostati su MySQL
            response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=datiCartaIncoerenti");
        }
    }
}