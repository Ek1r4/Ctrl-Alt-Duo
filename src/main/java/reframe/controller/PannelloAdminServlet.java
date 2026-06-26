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

        // 1. Controllo di sicurezza: se non sei loggato o non sei admin, fuori!
        if (adminLoggato == null || adminLoggato.getIsAdmin() == 0) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // 2. Recupero i Prodotti dal Database
            ProdottoDAO prodottoDAO = new ProdottoDAO();
            List<Prodotto> listaProdotti = prodottoDAO.fetchAllProdotti();
            request.setAttribute("listaProdotti", listaProdotti);

            // 3. Se è Super Admin, recupero anche la lista degli altri Admin
            if (adminLoggato.getIsAdmin() == 2) {
                UtenteDAO utenteDAO = new UtenteDAO();
                List<Utente> listaAdmins = utenteDAO.doRetrieveAllAdmins();
                request.setAttribute("listaAdmins", listaAdmins);
            }

            // 4. Passo tutti i dati alla JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/pannelloAdmin.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Gestione errore
            response.sendRedirect(request.getContextPath() + "/500.jsp");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String action = request.getParameter("action");

        // =======================================================
        // 1. BIVIO: AGGIUNTA NUOVO PROdotto (CON UPLOAD FILE)
        // =======================================================
        if ("aggiungiProdotto".equals(action)) {
            
            // Lettura dei campi di testo
            String idProdottoStr = request.getParameter("idProdotto");
            String seriale = request.getParameter("seriale");
            String marchio = request.getParameter("marchio");
            String nome = request.getParameter("nome");
            double prezzo = Double.parseDouble(request.getParameter("prezzo"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            String tipo = request.getParameter("tipo");
            String stato = request.getParameter("stato");
            
            // Gestione del numero scatti (potrebbe essere vuoto se il prodotto è nuovo)
            String scattiStr = request.getParameter("numeroScatti");
            int numeroScatti = (scattiStr != null && !scattiStr.trim().isEmpty()) ? Integer.parseInt(scattiStr) : 0;
            
            String condizione = request.getParameter("condizioneCollezionistica");
            String descrizione = request.getParameter("descrizione");

            // Preparazione dei percorsi fisici sul server
            String basePath = request.getServletContext().getRealPath("");
            String uploadPathImg = basePath + File.separator + "assets" + File.separator + "copertina";
            String uploadPath3D = basePath + File.separator + "assets" + File.separator + "modelli3D";

            // Creazione delle cartelle se non esistono
            new File(uploadPathImg).mkdirs();
            new File(uploadPath3D).mkdirs();

            // Estrazione e salvataggio dell'Immagine
            Part imgPart = request.getPart("immagineCopertina");
            String fileNameImg = "";
            if (imgPart != null && imgPart.getSize() > 0) {
                fileNameImg = imgPart.getSubmittedFileName();
                imgPart.write(uploadPathImg + File.separator + fileNameImg);
            }

            // Estrazione e salvataggio del Modello 3D
            Part modelPart = request.getPart("modello3D");
            String fileName3D = "";
            if (modelPart != null && modelPart.getSize() > 0) {
                fileName3D = modelPart.getSubmittedFileName();
                modelPart.write(uploadPath3D + File.separator + fileName3D);
            }

            // Stringhe relative da salvare nel DB
            String dbPathImg = fileNameImg.isEmpty() ? null : "/assets/copertina/" + fileNameImg;
            String dbPath3D = fileName3D.isEmpty() ? null : "/assets/modelli3D/" + fileName3D;

            // Creazione dell'oggetto Prodotto
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
                // Il redirect usa il parametro 'prodottoCreato' per far scattare la scheda del catalogo nella JSP
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?success=prodottoCreato");
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?errore=salvataggio_fallito");
            }
            return; // Ferma l'esecuzione qui per non far scattare gli altri if
        }

        // =======================================================
        // 2. BIVIO: REVOCA ACCESSO (ELIMINAZIONE ADMIN)
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
            return; // FONDAMENTALE: Ferma l'esecuzione qui
        }
        
        // =======================================================
        // 3. BIVIO: CREAZIONE NUOVO ADMIN
        // =======================================================
        String username = request.getParameter("username");
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("adminEmail");
        String password = request.getParameter("adminPassword");
        
        List<String> errors = new ArrayList<>();
        
        if (username == null || username.trim().isEmpty()) {
            errors.add("Tutti i campi sono obbligatori.");
        } else {
            username = username.trim();
        }
        
        if (email == null || email.trim().isEmpty()) {
            errors.add("Tutti i campi sono obbligatori.");
        } else {
            email = email.trim();
        }
        
        if (password == null || password.trim().isEmpty()) {
            errors.add("Tutti i campi sono obbligatori.");
        } else if (password.length() < 8) {
            errors.add("La password deve essere di almeno 8 caratteri.");
        } else {
            password = password.trim();
        }
        
        if (nome == null || nome.trim().isEmpty()) {
            errors.add("Tutti i campi sono obbligatori.");
        } else {
            nome = nome.trim();
        }
        
        if (cognome == null || cognome.trim().isEmpty()) {
            errors.add("Tutti i campi sono obbligatori.");
        } else {
            cognome = cognome.trim();
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
            nuovoAdmin.setUsername(username);
            nuovoAdmin.setEmail(email);
            nuovoAdmin.setPassword(password);
            nuovoAdmin.setNome(nome);
            nuovoAdmin.setCognome(cognome);
            
            dao.doSaveAdmin(nuovoAdmin);
            
            response.sendRedirect(request.getContextPath() + "/PannelloAdminServlet?success=adminCreato");
        } catch (SQLException e) {
            e.printStackTrace();
            errors.add("Errore interno del server durante la registrazione. Riprova più tardi.");
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/admin/pannelloAdmin.jsp").forward(request, response);
        }
    }
}