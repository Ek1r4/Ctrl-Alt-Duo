<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // MOCKUP RUOLO: cambia questa stringa ("utente", "admin", "superadmin") per testare l'overlay
    String ruoloSimulato = "superadmin"; 
    request.setAttribute("ruolo", ruoloSimulato);
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
                            <legend>Motivo</legend>
                            <p id="dettaglioMotivo" class="testo-tecnico">--</p>
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
                                        <div class="select-wrapper-analog">
                                            <select id="selectStato" name="stato">
                                                <option value="Aperta">APERTA</option>
                                                <option value="In carico">IN CARICO</option>
                                                <option value="Chiusa">CHIUSA</option>
                                            </select>
                                            <i class="ri-arrow-down-s-line select-arrow-analog"></i>
                                        </div>
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
                                        <input type="text" id="adminInCarico" list="adminList" placeholder="Es. admin_erika">
                                        <datalist id="adminList">
                                            <option value="admin_Erika">
                                            <option value="admin_Mirko">
                                            <option value="admin_Marco">
                                            <option value="admin_Paolo">
                                        </datalist>
                                    </fieldset>
                                    <button type="submit" class="btn-cta btn-chat-compact" style="margin-top:5px;">ASSEGNA E NOTIFICA</button>
                                </form>

                                <form id="formNota" onsubmit="inviaNota(event)" style="margin-top: 15px;">
                                    <fieldset class="custom-input">
                                        <legend>Lascia una Nota Privata</legend>
                                        <textarea id="notaSuperadmin" placeholder="Verrà spedita via email..." required rows="2"></textarea>
                                    </fieldset>
                                    <button type="submit" class="btn-cta btn-chat-compact" style="margin-top:5px;">INVIA NOTA</button>
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