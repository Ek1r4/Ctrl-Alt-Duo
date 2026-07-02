<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.dao.UtenteDAO" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // ==========================================
    // DETERMINAZIONE DINAMICA DEL RUOLO
    // ==========================================
    reframe.model.beans.Utente utenteLoggatoOverlay = (reframe.model.beans.Utente) session.getAttribute("utente");
    String ruoloReale = "utente"; // Fallback di default
    
    if (utenteLoggatoOverlay != null) {
        if (utenteLoggatoOverlay.getIsAdmin() == 2) {
            ruoloReale = "superadmin";
            
            // OTTIMIZZAZIONE: Recuperiamo gli admin dal DB SOLO se l'utente è un Superadmin
            UtenteDAO uDao = new UtenteDAO();
            try {
                List<reframe.model.beans.Utente> listaAdmins = uDao.doRetrieveAllAdmins();
                request.setAttribute("listaAdmins", listaAdmins);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } else if (utenteLoggatoOverlay.getIsAdmin() == 1) {
            ruoloReale = "admin";
        }
    }
    
    request.setAttribute("ruolo", ruoloReale);
%>
<link rel="stylesheet" href="../css/form.css">
<dialog id="ticketOverlay" class="ticket-dialog">
    <div class="ticket-dialog-wrapper">
        
        <button class="btn-close-dialog" onclick="chiudiOverlay()" aria-label="Chiudi ticket">
            <i class="ri-close-large-line"></i>
        </button>

        <div class="ticket-dialog-content">

            <!-- LATO SINISTRO: CHAT -->
            <div class="ticket-panel film-container panel-chat">
                <h2 class="form-title">CHAT <span>TICKET</span></h2>
                
                <!-- Area messaggi con scroll invisibile -->
                <div class="chat-history" id="chatHistory">
                    <div class="testo-tecnico loading-text">CARICAMENTO MESSAGGI...</div>
                </div>

                <!-- Input Chat dimezzato (Nascosto per superadmin) -->
                <c:if test="${ruolo != 'superadmin'}">
                    <form id="formChat" onsubmit="inviaMessaggio(event)" class="chat-form">
                        <input type="hidden" id="chatRma" name="rma">
                        <fieldset class="custom-input chat-input-compact">
                            <legend>Tuo Messaggio</legend>
                            <textarea id="nuovoMessaggio" placeholder="Scrivi una risposta..." required rows="1"></textarea>
                        </fieldset>
                        <button type="submit" class="btn-cta btn-chat-compact">INVIA</button>
                    </form>
                </c:if>

                <c:if test="${ruolo == 'superadmin'}">
                    <div class="readonly-alert">
                        <i class="ri-eye-off-line icon-rust"></i>
                        <p class="denied-text" style="margin-bottom:0;">Sola lettura. I superadmin non chattano.</p>
                    </div>
                </c:if>
            </div>

            <!-- LATO DESTRO: DETTAGLI & AZIONI -->
            <div class="ticket-panel film-container panel-details">
                <h2 class="form-title">DETTAGLI <span>PRATICA</span></h2>
                
                <!-- Area dettagli con scroll invisibile -->
                <div class="panel-details-scroll">
                    
                    <!-- Info base -->
                    <div class="ticket-info">
                        <fieldset class="custom-input read-only-box">
                            <legend>Ticket ID</legend>
                            <p id="dettaglioRma" class="testo-tecnico">--</p>
                        </fieldset>
                        <fieldset class="custom-input read-only-box">
                            <legend>Data Apertura</legend>
                            <p id="dettaglioData" class="testo-tecnico">--</p>
                        </fieldset>
                        <fieldset class="custom-input read-only-box">
                            <legend>Titolo & Categoria</legend>
                            <div class="titolo-categoria-wrapper">
                                <span id="dettaglioCategoria" class="badge-categoria-brutal">--</span>
                                <p id="dettaglioTitolo" class="testo-tecnico dettaglio-titolo-testo">--</p>
                            </div>
                        </fieldset>
                    </div>

                    <!-- BOX ORDINI E PRODOTTI (Popolato via JS) -->
                    <div id="dettaglioSelezioni"></div>

                    <!-- Azioni in base al ruolo -->
                    <div class="ticket-actions">
                        <%-- UTENTE --%>
                        <c:choose>
                            <c:when test="${ruolo == 'utente'}">
                                <fieldset class="custom-input read-only-box">
                                    <legend>Stato Attuale</legend>
                                    <p id="dettaglioStato" class="testo-tecnico">--</p>
                                </fieldset>
                                
                                <div id="utenteTicketChiuso" style="display: none;" class="custom-cb-wrapper">
                                    <input type="checkbox" checked disabled>
                                    <div class="custom-cb"></div>
                                    <span class="testo-tecnico">Pratica Chiusa Definitivamente</span>
                                </div>
                            </c:when>

                            <%-- ADMIN --%>
                            <c:when test="${ruolo == 'admin'}">
                                <form id="formStato" onsubmit="aggiornaStato(event)">
                                    <fieldset class="custom-input">
                                        <legend>Gestisci Stato</legend>
                                        
                                        <!-- Container del Toggle Stile "Segmented Control" -->
                                        <div class="status-toggle-wrapper">
                                            <input type="checkbox" id="toggleStatoAdmin" name="stato_chiusa" value="true">
                                            <label class="status-toggle-pill" for="toggleStatoAdmin">
                                                <span class="toggle-thumb"></span>
                                                <span class="toggle-text toggle-text-left">IN CARICO</span>
                                                <span class="toggle-text toggle-text-right">CHIUSA</span>
                                            </label>
                                        </div>

                                        <!-- Container per il messaggio di avviso -->
                                        <p id="adminStatusWarning" class="status-warning-msg"></p>

                                    </fieldset>
                                    <button type="submit" class="btn-cta btn-chat-compact">AGGIORNA STATO</button>
                                </form>
                            </c:when>
                            <%-- SUPERADMIN --%>
                            <c:when test="${ruolo == 'superadmin'}">
                                <fieldset class="custom-input read-only-box">
                                    <legend>Stato Attuale</legend>
                                    <p id="dettaglioStatoSuper" class="testo-tecnico">--</p>
                                </fieldset>
                                
                                <form id="formAssegna" onsubmit="assegnaAdmin(event)">
                                    <fieldset class="custom-input">
                                        <legend>Assegna ad Admin</legend>
                                        <input type="text" id="adminInCarico" list="adminList" placeholder="Es. admin_mario">
                                        
                                        <!-- LISTA DINAMICA DEGLI ADMIN DAL DATABASE -->
                                        <datalist id="adminList">
                                            <c:forEach var="admin" items="${listaAdmins}">
                                                <%-- FILTRO: Mostra come opzione SOLO gli admin di livello 1 (Esclude i Superadmin) --%>
                                                <c:if test="${admin.isAdmin == 1}">
                                                    <option value="${admin.username}">${admin.nome} ${admin.cognome}</option>
                                                </c:if>
                                            </c:forEach>
                                        </datalist>
                                        
                                    </fieldset>
                                    <button type="submit" class="btn-cta btn-chat-compact" style="margin-top:5px;">ASSEGNA E NOTIFICA</button>
                                </form>

                                <form id="formNota" onsubmit="inviaNota(event)" style="margin-top: 15px;">
                                    <fieldset class="custom-input">
                                        <legend>Lascia una Nota Privata</legend>
                                        <textarea id="notaSuperadmin" placeholder="Verrà spedita via email all'admin in carico..." required rows="2"></textarea>
                                    </fieldset>
                                    
                                    <!-- Avviso mostrato solo quando la pratica è Aperta -->
                                    <p id="notaSuperadminWarning" class="status-warning-msg" style="text-align: left; margin-bottom: 10px;"></p>
                                    
                                    <button type="submit" id="btnInviaNota" class="btn-cta btn-chat-compact">INVIA NOTA</button>
                                </form>
                            </c:when>
                        </c:choose>
                    </div>
                    
                </div> <!-- /panel-details-scroll -->
            </div>

        </div>
    </div>
    <div class="toast-notification"></div>
</dialog>