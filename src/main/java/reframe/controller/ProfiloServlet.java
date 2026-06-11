package reframe.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
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
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");
        
        // 1. Controllo sicurezza: se non è loggato va al login
        if (utenteLoggato == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            // 2. RECUPERIAMO I DATI DAL DB TRAMITE I TUOI DAO
            // Usa il metodo dei tuoi DAO che recupera i dati filtrando per l'username dell'utente
            List<Spedizione> listaSpedizioni = spedizioneDAO.doRetrieveByUtente(utenteLoggato.getUsername()); 
            List<Pagamento> listaPagamenti = pagamentoDAO.doRetrieveByUtente(utenteLoggato.getUsername());
            
            // 3. METTIAMO LE LISTE NELLA REQUEST
            // Questo è il passaggio chiave: diamo i dati in pasto alla JSP
            request.setAttribute("listaSpedizioni", listaSpedizioni);
            request.setAttribute("listaPagamenti", listaPagamenti);
            
        } catch (SQLException e) {
            e.printStackTrace();
            // In caso di errore passiamo comunque liste vuote per evitare crash
            request.setAttribute("listaSpedizioni", new java.util.ArrayList<>());
            request.setAttribute("listaPagamenti", new java.util.ArrayList<>());
        }
        
        // 4. INVIAMO TUTTO ALLA JSP
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
                
            case "eliminaRisorsa":
                gestisciEliminazione(request, response, utenteLoggato);
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
        
        if (bio != null && bio.trim().length() > 255) {
            response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=bioTroppoLunga");
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
                response.sendRedirect(request.getContextPath() + "/ProfiloServlet?success=passwordModificata");
            } else {
            	response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=cambioPasswordFallito");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=db");
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
                // PRIMA ERA: /common/profilo.jsp?success=...
                response.sendRedirect(request.getContextPath() + "/ProfiloServlet?success=spedizioneSalvata");
            } else {
                response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=salvataggioSpedizioneFallito");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=db");
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
                response.sendRedirect(request.getContextPath() + "/ProfiloServlet?success=pagamentoSalvato");
            } else {
                response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=salvataggioPagamentoFallito");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=datiCartaIncoerenti");
        }
    }
    
    private void gestisciEliminazione(HttpServletRequest request, HttpServletResponse response, Utente utenteLoggato) throws ServletException, IOException {
        String type = request.getParameter("type");
        String idStr = request.getParameter("id");

        // Controllo di sicurezza sui parametri
        if (type == null || idStr == null) {
            response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=parametriMancanti");
            return;
        }

        try {
            // Trasformiamo l'ID da stringa a numero intero
            int id = Integer.parseInt(idStr);
            boolean successo = false;

            // Capiamo se l'utente ha cliccato il cestino di un indirizzo o di una carta
            if (type.equals("shipping")) {
                // (Per ora no) Passiamo anche l'username per sicurezza: un utente può cancellare solo i SUOI indirizzi
                successo = spedizioneDAO.doDelete(id);
            } else if (type.equals("payment")) {
                successo = pagamentoDAO.doDelete(id);
            }

            // Risposta al client
            if (successo) {
                response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?success=eliminazione");
            } else {
                response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=eliminazioneFallita");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=formatoIdErrato");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/common/profilo.jsp?error=db");
        }
    }
}