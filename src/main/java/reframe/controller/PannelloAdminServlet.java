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

@WebServlet("/PannelloAdminServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB prima di scrivere su disco temporaneo
    maxFileSize = 1024 * 1024 * 50,       // Max 50MB per singolo file (i modelli 3D pesano)
    maxRequestSize = 1024 * 1024 * 100    // Max 100MB per l'intera richiesta POST
)
public class PannelloAdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente adminLoggato = (Utente) session.getAttribute("utente");

        if (adminLoggato == null || adminLoggato.getIsAdmin() == 0) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // 1. RECUPERO PRODOTTI
            ProdottoDAO prodottoDAO = new ProdottoDAO();
            request.setAttribute("listaProdotti", prodottoDAO.fetchAllProdotti());

            // 2. RECUPERO ADMIN (se SuperAdmin)
            if (adminLoggato.getIsAdmin() == 2) {
                UtenteDAO utenteDAO = new UtenteDAO();
                request.setAttribute("listaAdmins", utenteDAO.doRetrieveAllAdmins());
            }

            // 3. RECUPERO ORDINI CON FILTRI
            String cliente = request.getParameter("cliente");
            String dataInizioStr = request.getParameter("dataInizio");
            String dataFineStr = request.getParameter("dataFine");

            java.sql.Date dataInizio = (dataInizioStr != null && !dataInizioStr.isEmpty()) ? java.sql.Date.valueOf(dataInizioStr) : null;
            java.sql.Date dataFine = (dataFineStr != null && !dataFineStr.isEmpty()) ? java.sql.Date.valueOf(dataFineStr) : null;

            OrdineDAO ordineDAO = new OrdineDAO();
            // Assicurati che il metodo nel DAO sia quello che abbiamo discusso
            request.setAttribute("listaOrdini", ordineDAO.getOrdiniFiltrati(cliente, dataInizio, dataFine));

            // 4. FORZA IL TAB "ORDINI" SE SONO STATI USATI I FILTRI
            if (cliente != null || dataInizioStr != null || dataFineStr != null || "ordini".equals(request.getParameter("tab"))) {
                request.setAttribute("targetTab", "ordini");
            }

            request.getRequestDispatcher("/admin/pannelloAdmin.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/500.jsp");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String action = request.getParameter("action");

        // =======================================================
        // 1. BIVIO: AGGIUNTA NUOVO PRODOTTO
        // =======================================================
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

     // =======================================================
        // 2. BIVIO: AGGIORNA STATO ORDINE (RICARICAMENTO CLASSICO)
        // =======================================================
        if ("aggiornaStatoOrdine".equals(action)) {
            String idOrdine = request.getParameter("idOrdine");
            String nuovoStato = request.getParameter("nuovoStato");
            
            try {
                OrdineDAO ordineDAO = new OrdineDAO();
                ordineDAO.updateStato(idOrdine, nuovoStato);
                
                // Redirect classico con il parametro per riaprire direttamente la scheda Ordini
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?tab=ordini&success=statoAggiornato");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?tab=ordini&errore=updateFallito");
            }
            return;
        }

        // =======================================================
        // 3. BIVIO: REVOCA ACCESSO (ELIMINAZIONE ADMIN)
        // =======================================================
        String userAdminDaEliminare = request.getParameter("userAdmin");
        if (userAdminDaEliminare != null && !userAdminDaEliminare.trim().isEmpty()) {
            UtenteDAO dao = new UtenteDAO();
            try {
                dao.doDelete(userAdminDaEliminare); 
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?success=adminEliminato");
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?errore=Impossibile_revocare_accesso");
            }
            return;
        }
        
        // =======================================================
        // 4. BIVIO: CREAZIONE NUOVO ADMIN
        // =======================================================
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
                nuovoAdmin.setPassword(password.trim());
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