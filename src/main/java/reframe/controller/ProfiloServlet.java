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
import reframe.model.dao.OrdineDAO;
import reframe.model.beans.Ordine;

@WebServlet("/ProfiloServlet")
public class ProfiloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private UtenteDAO utenteDAO;
    private PagamentoDAO pagamentoDAO;
    private SpedizioneDAO spedizioneDAO;
    private OrdineDAO ordineDAO;

    /* INIZIALIZZAZIONE RISORSE */
    @Override
    public void init() throws ServletException {
        super.init();
        this.utenteDAO = new UtenteDAO();
        this.pagamentoDAO = new PagamentoDAO();
        this.spedizioneDAO = new SpedizioneDAO();
        this.ordineDAO = new OrdineDAO();
    }

    /* GESTIONE RICHIESTE GET (CARICAMENTO PROFILO/DASHBOARD) */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");
        
        if (utenteLoggato == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            // Pre-caricamento dei dati dell'utente
            List<Spedizione> listaSpedizioni = spedizioneDAO.doRetrieveByUtente(utenteLoggato.getUsername()); 
            List<Pagamento> listaPagamenti = pagamentoDAO.doRetrieveByUtente(utenteLoggato.getUsername());
            List<Ordine> listaOrdini = ordineDAO.getOrdiniCompletiByUtente(utenteLoggato.getUsername());
            
            request.setAttribute("listaSpedizioni", listaSpedizioni);
            request.setAttribute("listaPagamenti", listaPagamenti);
            request.setAttribute("listaOrdini", listaOrdini);
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Fallback: instanziazione di liste vuote per prevenire NullPointerException sul rendering della View JSP
            request.setAttribute("listaSpedizioni", new java.util.ArrayList<>());
            request.setAttribute("listaPagamenti", new java.util.ArrayList<>());
            request.setAttribute("listaOrdini", new java.util.ArrayList<>());
        }
        
        // Routing UI (RBAC): indirizzamento condizionale alla dashboard di amministrazione o al profilo cliente
        if (utenteLoggato.getIsAdmin() > 0) {
            request.getRequestDispatcher("/admin/pannelloAdmin.jsp").forward(request, response);
            return;
        } else {
            request.getRequestDispatcher("/common/profilo.jsp").forward(request, response);
            return;
        }
    }

    /* GESTIONE RICHIESTE POST (DISPATCHING AZIONI) */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");
        
        if (utenteLoggato == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Design pattern Command/Dispatcher: smistamento logico basato sul parametro hidden "action"
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/common/profilo.jsp");
            return;
        }

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

    /* AZIONE: AGGIORNAMENTO DATI ANAGRAFICI */
    private void gestisciAnagrafica(HttpServletRequest request, HttpServletResponse response, Utente utenteLoggato, HttpSession session) throws ServletException, IOException {
        String telefono = request.getParameter("telefono");
        String bio = request.getParameter("bio");

        // Validazione server-side mediante regex per prevenire formattazioni errate a database
        if (telefono == null || telefono.trim().isEmpty() || !telefono.matches("^[0-9]{10}$")) {
            response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=telefonoObbligatorio");
            return;
        }
        
        if (bio != null && bio.trim().length() > 255) {
            response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=bioTroppoLunga");
            return;
        }

        utenteLoggato.setTelefono(telefono.trim());
        utenteLoggato.setBio(bio != null ? bio.trim() : "");

        try {
            boolean successo = utenteDAO.doUpdate(utenteLoggato);
            if (successo) {
                // Aggiornamento sincronizzato dell'oggetto Utente in sessione per riflettere immediatamente le modifiche nell'interfaccia
                session.setAttribute("utente", utenteLoggato);
                response.sendRedirect(request.getContextPath() + "/ProfiloServlet?success=anagrafica");
            } else {
                response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=updateFallito");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=db");
        }
    }

    /* AZIONE: CAMBIO PASSWORD */
    private void gestisciPassword(HttpServletRequest request, HttpServletResponse response, Utente utenteLoggato) throws ServletException, IOException {
        System.out.println("\n--- DEBUG INIZIO CAMBIO PASSWORD ---");
        
        String vecchiaPassword = request.getParameter("vecchiaPassword");
        String nuovaPassword = request.getParameter("nuovaPassword");
        String confermaPassword = request.getParameter("confermaPassword");

        System.out.println("1. Ricezione parametri dal form completata.");

        if (nuovaPassword == null || nuovaPassword.length() < 8 || !nuovaPassword.equals(confermaPassword)) {
            System.out.println("2. FALLITO: Nuova password non valida o non coincide con la conferma.");
            response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=passwordInvalida");
            return;
        }
        
        // Computazione dell'hash della password fornita per il confronto sicuro con il digest memorizzato in DB
        String vecchiaPasswordCriptata = HashingPassword.hashPassword(vecchiaPassword);
        System.out.println("3. Hash Vecchia Password (inserita dall'utente): " + vecchiaPasswordCriptata);
        System.out.println("4. Hash Password Corrente (salvata in Sessione/DB): " + utenteLoggato.getPassword());

        if (vecchiaPassword == null || !utenteLoggato.getPassword().equals(vecchiaPasswordCriptata)) {
            System.out.println("5. FALLITO: La vecchia password inserita è Sbagliata!");
            response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=vecchiaPasswordErrata");
            return;
        }
        
        System.out.println("5. OK: La vecchia password coincide.");
        String nuovaPasswordCriptata = HashingPassword.hashPassword(nuovaPassword);
        System.out.println("6. Hash Nuova Password (pronta per il DB): " + nuovaPasswordCriptata);

        try {
            System.out.println("7. Chiamata al DAO per l'update in corso...");
            boolean successo = utenteDAO.updatePassword(utenteLoggato.getEmail(), nuovaPasswordCriptata);
            
            if (successo) {
                utenteLoggato.setPassword(nuovaPasswordCriptata);
                System.out.println("8. SUCCESSO TOTALE: Il DAO ha restituito TRUE. Sessione aggiornata.");
                response.sendRedirect(request.getContextPath() + "/ProfiloServlet?success=passwordModificata");
            } else {
                System.out.println("8. FALLITO: Il DAO ha restituito FALSE. Errore query UPDATE.");
            	response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=cambioPasswordFallito");
            }
        } catch (SQLException e) {
            System.out.println("!!! ECCEZIONE SQL !!! " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=db");
        }
        System.out.println("--- DEBUG FINE CAMBIO PASSWORD ---\n");
    }

    /* AZIONE: SALVATAGGIO NUOVA SPEDIZIONE */
    private void gestisciSpedizione(HttpServletRequest request, HttpServletResponse response, Utente utenteLoggato) throws ServletException, IOException {
        String citta = request.getParameter("citta");
        String provincia = request.getParameter("provincia");
        String paese = request.getParameter("paese");
        String cap = request.getParameter("cap");
        String via = request.getParameter("via");
        String civico = request.getParameter("civico");
        String note = request.getParameter("note");

        Spedizione nuovaSpedizione = new Spedizione();
        nuovaSpedizione.setIdUtente(utenteLoggato.getUsername()); // Vincolo di integrità referenziale (FK) verso l'utente corrente
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
                response.sendRedirect(request.getContextPath() + "/ProfiloServlet?success=spedizioneSalvata");
            } else {
                response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=salvataggioSpedizioneFallito");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=db");
        }
    }

    /* AZIONE: SALVATAGGIO NUOVO METODO DI PAGAMENTO */
    private void gestisciPagamento(HttpServletRequest request, HttpServletResponse response, Utente utenteLoggato) throws ServletException, IOException {
        String nomeIntestatario = request.getParameter("nomeIntestatario");
        String circuito = request.getParameter("circuito");
        String numeroCarta = request.getParameter("numeroCarta");
        String dataScadenza = request.getParameter("dataScadenza");
        String cvv = request.getParameter("cvv");

        Pagamento nuovoPagamento = new Pagamento();
        nuovoPagamento.setIdUtente(utenteLoggato.getUsername()); // Vincolo di integrità referenziale (FK) verso l'utente corrente
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
    
    /* AZIONE: RIMOZIONE RISORSA (INDIRIZZO O PAGAMENTO) */
    private void gestisciEliminazione(HttpServletRequest request, HttpServletResponse response, Utente utenteLoggato) throws ServletException, IOException {
        String type = request.getParameter("type");
        String idStr = request.getParameter("id");

        if (type == null || idStr == null) {
            response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=parametriMancanti");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean successo = false;

            // Delegazione del blocco di esecuzione in base al target specificato
            if (type.equals("shipping")) {
                successo = spedizioneDAO.doDelete(id);
            } else if (type.equals("payment")) {
                successo = pagamentoDAO.doDelete(id);
            }

            if (successo) {
                response.sendRedirect(request.getContextPath() + "/ProfiloServlet?success=eliminazione");
            } else {
                response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=eliminazioneFallita");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=formatoIdErrato");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ProfiloServlet?error=db");
        }
    }
}