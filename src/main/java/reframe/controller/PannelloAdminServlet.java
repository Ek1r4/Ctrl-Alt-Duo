package reframe.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import reframe.model.beans.Prodotto;
import reframe.model.beans.Utente;
import reframe.model.dao.ProdottoDAO; 
import reframe.model.dao.UtenteDAO;
import reframe.model.beans.Ordine;
import reframe.model.dao.OrdineDAO;
import reframe.utils.HashingPassword;
import reframe.utils.EmailManager;

/* CONFIGURAZIONE CLASSE E ANNOTAZIONI */
// @MultipartConfig permette alla Servlet di gestire flussi di dati multipli, necessari per l'upload di immagini e modelli 3D
@WebServlet("/PannelloAdminServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 50,
    maxRequestSize = 1024 * 1024 * 100
)
public class PannelloAdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /* GESTIONE RICHIESTE GET (CARICAMENTO PANNELLO E FILTRI) */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utente adminLoggato = (Utente) session.getAttribute("utente");

        // Controllo accessi: impedisce l'accesso al pannello agli utenti senza privilegi amministrativi
        if (adminLoggato == null || adminLoggato.getIsAdmin() == 0) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            /* RECUPERO PRODOTTI */
            ProdottoDAO prodottoDAO = new ProdottoDAO();
            String ricercaProdotto = request.getParameter("ricercaProdotto");

            if (ricercaProdotto != null && !ricercaProdotto.trim().isEmpty()) {
                request.setAttribute("listaProdotti", prodottoDAO.fetchProdottiPerAdmin(ricercaProdotto.trim()));
            } else {
                request.setAttribute("listaProdotti", prodottoDAO.fetchAllProdottiAdmin());
            }

            /* RECUPERO UTENTI ADMIN */
            // L'esposizione della lista amministratori è ristretta unicamente al SuperAdmin (Livello 2)
            if (adminLoggato.getIsAdmin() == 2) {
                UtenteDAO utenteDAO = new UtenteDAO();
                request.setAttribute("listaAdmins", utenteDAO.doRetrieveAllAdmins());
            }

            /* RECUPERO E FILTRAGGIO ORDINI */
            String cliente = request.getParameter("cliente");
            String dataInizioStr = request.getParameter("dataInizio");
            String dataFineStr = request.getParameter("dataFine");

            java.sql.Date dataInizio = (dataInizioStr != null && !dataInizioStr.isEmpty()) ? java.sql.Date.valueOf(dataInizioStr) : null;
            java.sql.Date dataFine = (dataFineStr != null && !dataFineStr.isEmpty()) ? java.sql.Date.valueOf(dataFineStr) : null;

            OrdineDAO ordineDAO = new OrdineDAO();
            request.setAttribute("listaOrdini", ordineDAO.getOrdiniFiltrati(cliente, dataInizio, dataFine));

            /* GESTIONE STATO UI (TABS) */
            // Gestisce la persistenza del tab attivo sul frontend in caso di refresh derivato da filtraggio o ricerca
            if (cliente != null || dataInizioStr != null || dataFineStr != null || "ordini".equals(request.getParameter("tab"))) {
                request.setAttribute("targetTab", "ordini");
            } else if (ricercaProdotto != null || "prodotti".equals(request.getParameter("tab"))) {
                request.setAttribute("targetTab", "prodotti");
            }

            request.getRequestDispatcher("/admin/pannelloAdmin.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/500.jsp");
        }
    }

    /* GESTIONE RICHIESTE POST (AZIONI DI AMMINISTRAZIONE) */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String action = request.getParameter("action");

        /* AZIONE: AGGIUNTA NUOVO PRODOTTO E GESTIONE UPLOAD */
        if ("aggiungiProdotto".equals(action)) {
            String idProdottoStr = request.getParameter("idProdotto");
            String seriale = request.getParameter("seriale");
            String marchio = request.getParameter("marchio");
            String nome = request.getParameter("nome");
            double prezzo = Double.parseDouble(request.getParameter("prezzo"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            String tipo = request.getParameter("tipo");
            String stato = request.getParameter("stato");
            
            String scattiStr = request.getParameter("numeroScatti");
            int numeroScatti = (scattiStr != null && !scattiStr.trim().isEmpty()) ? Integer.parseInt(scattiStr) : 0;
            
            String condizione = request.getParameter("condizioneCollezionistica");
            String descrizione = request.getParameter("descrizione");

            // Risoluzione dei percorsi fisici dinamici per il salvataggio dei file multimediali all'interno del context server
            String basePath = request.getServletContext().getRealPath("");
            String uploadPathImg = basePath + File.separator + "assets" + File.separator + "copertina";
            String uploadPath3D = basePath + File.separator + "assets" + File.separator + "modelli3D";

            new File(uploadPathImg).mkdirs();
            new File(uploadPath3D).mkdirs();

            Part imgPart = request.getPart("immagineCopertina");
            String fileNameImg = "";
            if (imgPart != null && imgPart.getSize() > 0) {
                fileNameImg = imgPart.getSubmittedFileName();
                imgPart.write(uploadPathImg + File.separator + fileNameImg);
            }

            Part modelPart = request.getPart("modello3D");
            String fileName3D = "";
            if (modelPart != null && modelPart.getSize() > 0) {
                fileName3D = modelPart.getSubmittedFileName();
                modelPart.write(uploadPath3D + File.separator + fileName3D);
            }

            // Sanitizzazione dei path relativi da salvare a DB (previene null o stringhe vuote)
            String dbPathImg = fileNameImg.isEmpty() ? null : "/assets/copertina/" + fileNameImg;
            String dbPath3D = fileName3D.isEmpty() ? null : "/assets/modelli3D/" + fileName3D;

            Prodotto p = new Prodotto();
            p.setId(idProdottoStr);
            p.setSeriale(seriale);
            p.setMarchio(marchio);
            p.setNome(nome);
            p.setPrezzo(prezzo);
            p.setInStock(stock);
            p.setTipo(tipo);
            p.setStato(stato);
            p.setNumeroScatti(numeroScatti);
            p.setCondizioneCollezionistica(condizione);
            p.setDescrizione(descrizione);
            p.setImageUrl(dbPathImg);
            p.setModelUrl(dbPath3D);

            ProdottoDAO prodottoDAO = new ProdottoDAO();
            try {
                prodottoDAO.insertProdotto(p);
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?success=prodottoCreato");
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?errore=salvataggio_fallito");
            }
            return;
        }

        /* AZIONE: AGGIORNAMENTO STATO ORDINE */
        if ("aggiornaStatoOrdine".equals(action)) {
            String idOrdine = request.getParameter("idOrdine");
            String nuovoStato = request.getParameter("nuovoStato");
            
            try {
                OrdineDAO ordineDAO = new OrdineDAO();
                ordineDAO.updateStato(idOrdine, nuovoStato);
                
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?tab=ordini&success=statoAggiornato");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?tab=ordini&errore=updateFallito");
            }
            return;
        }

        /* AZIONE: ELIMINAZIONE AMMINISTRATORE */
        String userAdminDaEliminare = request.getParameter("userAdmin");
        if (userAdminDaEliminare != null && !userAdminDaEliminare.trim().isEmpty()) {
            UtenteDAO dao = new UtenteDAO();
            try {
                Utente adminTarget = dao.doRetrieveByKey(userAdminDaEliminare);
                dao.doDelete(userAdminDaEliminare); 
                
                //mail di notifica
                if (adminTarget != null && adminTarget.getEmail() != null) {
                    String oggetto = "Reframe - Revoca accessi amministrativi";
                    String testo = "Gentile " + adminTarget.getNome() + ",\n\n"
                                 + "Ti informiamo che i tuoi privilegi di amministratore sulla piattaforma Reframe sono stati revocati.\n"
                                 + "Il tuo account gestionale è stato disabilitato ed eliminato dai nostri sistemi in modo permanente.\n\n"
                                 + "Saluti,\nIl Team Reframe.";

                    EmailManager.inviaEmail(adminTarget.getEmail(), oggetto, testo);
                }
                
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?success=adminEliminato");
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?errore=Impossibile_revocare_accesso");
            }
            return;
        }
        
        /* AZIONE: CREAZIONE NUOVO AMMINISTRATORE */
        String username = request.getParameter("username");
        if (username != null) {
            String nome = request.getParameter("nome");
            String cognome = request.getParameter("cognome");
            String email = request.getParameter("adminEmail");
            String password = request.getParameter("adminPassword");
            
            List<String> errors = new ArrayList<>();
            
            if (username.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty() || nome.trim().isEmpty() || cognome.trim().isEmpty()) {
                errors.add("Tutti i campi sono obbligatori.");
            } else if (password.length() < 8) {
                errors.add("La password deve essere di almeno 8 caratteri.");
            }
            
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.getRequestDispatcher("/admin/pannelloAdmin.jsp").forward(request, response);
                return; 
            }
            
            UtenteDAO dao = new UtenteDAO();
            try {
                if (dao.VerificaEmail(email)) {
                    errors.add("Questa email è già registrata nel sistema.");
                    request.setAttribute("errors", errors);
                    request.getRequestDispatcher("/admin/pannelloAdmin.jsp").forward(request, response);
                    return;
                }
            
                Utente nuovoAdmin = new Utente();
                nuovoAdmin.setUsername(username.trim());
                nuovoAdmin.setEmail(email.trim());
                
                // Sicurezza: esegue l'hashing della password prima dell'inserimento per evitare la memorizzazione in chiaro a DB
                String passwordHashata = HashingPassword.hashPassword(password.trim());
                nuovoAdmin.setPassword(passwordHashata);
                
                nuovoAdmin.setNome(nome.trim());
                nuovoAdmin.setCognome(cognome.trim());
                
                dao.doSaveAdmin(nuovoAdmin);
                
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?success=adminCreato");
                return;
            } catch (SQLException e) {
                e.printStackTrace();
                errors.add("Errore interno. Riprova più tardi.");
                request.setAttribute("errors", errors);
                request.getRequestDispatcher("/admin/pannelloAdmin.jsp").forward(request, response);
                return;
            }
        }
    }
}