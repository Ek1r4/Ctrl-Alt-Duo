document.addEventListener("DOMContentLoaded", () => {
    const urlParams = new URLSearchParams(window.location.search);
    const targetRma = urlParams.get('rma');
    if (targetRma) {
        apriTicketOverlay(targetRma);
    }
});

function apriTicketOverlay(rma) {
    const dialog = document.getElementById('ticketOverlay');
    dialog.showModal();
    document.body.style.overflow = 'hidden';

    // Pulisce l'URL
    window.history.replaceState({}, document.title, window.location.pathname);
    
    // Mostra un testo di caricamento
    document.getElementById('chatHistory').innerHTML = '<div class="testo-tecnico loading-text">CARICAMENTO MESSAGGI...</div>';

	    // =========================================================
	    // 1. MOCKUP TEMPORANEO (Per testare lo spacchettamento)
	    // =========================================================
	    setTimeout(() => {
	        const mockupData = {
	            rma: rma,
	            dataApertura: "27/06/2026 14:30",
	            motivo: "Il prodotto acquistato ha la lente graffiata e richiede sostituzione immediata.",
	            stato: "Aperta",
	            adminAssegnato: "admin_Mirko",
	            messaggi: [
	                { 
	                    autore: "Tu", 
	                    tipo: "User", 
	                    testo: "Ordini Selezionati: ORD19512\nProdotti Selezionati: EOS R5, Z8, X-T5\n----------------------------------------\n\nCiao, volevo sapere lo stato del mio reso.", 
	                    data: "27/06/2026 14:35" 
	                },
	                { 
	                    autore: "Admin Erika", 
	                    tipo: "Admin", 
	                    testo: "Salve, il corriere ritirerà il pacco lunedì mattina.", 
	                    data: "27/06/2026 16:00" 
	                }
	            ]
	        };
	        popolaOverlay(mockupData);
	    }, 500);// Finto ritardo di rete di mezzo secondo

    // =========================================================
    // 2. CHIAMATA REALE (Da decommentare quando crei la Servlet)
    // =========================================================
    /*
    fetch('DettaglioTicketServlet?rma=' + rma)
        .then(response => {
            if (!response.ok) throw new Error("Errore nel recupero del ticket");
            return response.json();
        })
        .then(data => {
            popolaOverlay(data);
        })
        .catch(error => {
            console.error(error);
            showToast("Errore di connessione al server.");
            chiudiOverlay();
        });
    */
}

function popolaOverlay(data) {
    // Info condivise base
    document.getElementById('dettaglioRma').innerText = data.rma;
    document.getElementById('dettaglioData').innerText = data.dataApertura;
    document.getElementById('dettaglioMotivo').innerText = data.motivo;

    let htmlPills = '';
    const chatBox = document.getElementById('chatHistory');
    chatBox.innerHTML = '';
    
    if (data.messaggi && data.messaggi.length > 0) {
        data.messaggi.forEach(msg => {
            let testoDaMostrare = msg.testo;
            
            // --- DE-FORMATTAZIONE (Spacchettamento) ---
            const separator = "----------------------------------------";
            
            if (testoDaMostrare.includes(separator)) {
                const parts = testoDaMostrare.split(separator);
                const intestazione = parts[0];
                
                // Ricongiungiamo il vero messaggio e togliamo gli spazi bianchi
                testoDaMostrare = parts.slice(1).join(separator).trim();
                
                // Estraiamo i dati e costruiamo le pillole (solo per il messaggio che le contiene)
                if (htmlPills === '') {
                    let tags = '';
                    const righe = intestazione.split('\n');
                    
                    righe.forEach(riga => {
                        const rigaPulita = riga.trim();
                        if (rigaPulita.startsWith("Ordini Selezionati:")) {
                            const ordini = rigaPulita.replace("Ordini Selezionati:", "").split(",");
                            ordini.forEach(o => {
                                if (o.trim()) tags += `<span class="sel-tag-analog">Ordine #${o.trim()}</span>`;
                            });
                        } else if (rigaPulita.startsWith("Prodotti Selezionati:")) {
                            const prodotti = rigaPulita.replace("Prodotti Selezionati:", "").split(",");
                            prodotti.forEach(p => {
                                if (p.trim()) tags += `<span class="sel-tag-analog">Prodotto: ${p.trim()}</span>`;
                            });
                        }
                    });
                    
                    if (tags !== '') {
                        htmlPills = `<div class="ticket-selections-analog">
                                        <span class="selection-label-analog">ORDINI E PRODOTTI CITATI:</span>
                                        ${tags}
                                     </div>`;
                    }
                }
            }

            // Sostituiamo gli a capo classici con quelli HTML
            testoDaMostrare = testoDaMostrare.replace(/\n/g, "<br>");
            
            // Inseriamo in chat
            const isUser = msg.tipo === 'User'; 
            chatBox.innerHTML += `
                <div class="msg ${isUser ? 'msg-user' : 'msg-admin'}">
                    <span class="msg-author">${msg.autore}</span>
                    <p>${testoDaMostrare}</p>
                    <small>${msg.data}</small>
                </div>
            `;
        });
    } else {
        chatBox.innerHTML = '<div class="testo-tecnico">Nessun messaggio presente.</div>';
    }
    chatBox.scrollTop = chatBox.scrollHeight;

    // Popoliamo il pannello destro con le pillole ricavate
    const selezioniBox = document.getElementById('dettaglioSelezioni');
    if (selezioniBox) selezioniBox.innerHTML = htmlPills;

    // --- RIEMPIMENTO INPUT IN BASE AI RUOLI ---
    if (document.getElementById('chatRma')) document.getElementById('chatRma').value = data.rma;
    if (document.getElementById('dettaglioStato')) document.getElementById('dettaglioStato').innerText = data.stato;
    if (document.getElementById('dettaglioStatoSuper')) document.getElementById('dettaglioStatoSuper').innerText = data.stato;
    if (document.getElementById('adminInCarico')) document.getElementById('adminInCarico').value = data.adminAssegnato;
    
    if (document.getElementById('utenteTicketChiuso') && data.stato === 'Chiusa') {
        document.getElementById('utenteTicketChiuso').style.display = 'flex';
    } else if (document.getElementById('utenteTicketChiuso')) {
        document.getElementById('utenteTicketChiuso').style.display = 'none';
    }

    // --- TOGGLE ADMIN ---
    const toggleStato = document.getElementById('toggleStatoAdmin');
    const adminWarningMsg = document.getElementById('adminStatusWarning');
    
    if (toggleStato) {
        const isOriginallyClosed = (data.stato === 'Chiusa');
        toggleStato.checked = isOriginallyClosed;
        
        if (adminWarningMsg) {
            adminWarningMsg.textContent = "";
            adminWarningMsg.classList.remove('show');
        }

        toggleStato.onchange = function() {
            const isCurrentlyClosed = this.checked;
            if (isCurrentlyClosed === isOriginallyClosed) {
                adminWarningMsg.classList.remove('show');
            } else {
                if (isCurrentlyClosed) {
                    adminWarningMsg.innerHTML = '<i class="ri-error-warning-line"></i> Attenzione: impostando su <b>CHIUSA</b> l\'utente riceverà una notifica di risoluzione. Clicca AGGIORNA STATO per confermare.';
                } else {
                    adminWarningMsg.innerHTML = '<i class="ri-spam-line"></i> Attenzione: riaprendo la pratica tornerà <b>IN CARICO</b> e l\'utente verrà notificato. Clicca AGGIORNA STATO per confermare.';
                }
                adminWarningMsg.classList.add('show');
            }
        };
    }

    // --- NOTE SUPERADMIN ---
    const textareaNota = document.getElementById('notaSuperadmin');
    const btnNota = document.getElementById('btnInviaNota');
    const notaWarning = document.getElementById('notaSuperadminWarning');

    if (textareaNota && btnNota) {
        if (data.stato === 'Aperta') {
            textareaNota.disabled = true;
            btnNota.disabled = true;
            btnNota.style.opacity = '0.5';
            btnNota.style.cursor = 'not-allowed';
            textareaNota.style.backgroundColor = 'rgba(0,0,0,0.05)';
            textareaNota.placeholder = "Azione bloccata.";
            
            if (notaWarning) {
                notaWarning.innerHTML = '<i class="ri-information-line"></i> Assegna prima un admin per potergli inviare una nota.';
                notaWarning.classList.add('show');
            }
        } else {
            textareaNota.disabled = false;
            btnNota.disabled = false;
            btnNota.style.opacity = '1';
            btnNota.style.cursor = 'pointer';
            textareaNota.style.backgroundColor = 'transparent';
            textareaNota.placeholder = "Verrà spedita via email all'admin in carico...";
            
            if (notaWarning) {
                notaWarning.textContent = "";
                notaWarning.classList.remove('show');
            }
        }
    }
}

function chiudiOverlay() {
    document.getElementById('ticketOverlay').close();
    document.body.style.overflow = 'auto';
}

function showToast(message) {
    const toast = document.querySelector('.toast-notification');
    if(toast) {
        toast.innerHTML = `<i class="ri-check-line"></i> ${message}`;
        toast.classList.add('show');
        setTimeout(() => toast.classList.remove('show'), 3000);
    } else {
        alert(message);
    }
}

// === AZIONI FORM VIA AJAX COLLEGATE ALLE SERVLET ===

// 1. MOCKUP: Invio Messaggio Chat
function inviaMessaggio(e) {
    e.preventDefault();
    const testo = document.getElementById('nuovoMessaggio').value;
    
    // Falsifichiamo il caricamento del server con un timeout di mezzo secondo
    setTimeout(() => {
        showToast("Messaggio inviato correttamente!");
        document.getElementById('nuovoMessaggio').value = '';
        
        // Aggiungiamo il messaggio direttamente in UI per vedere la grafica
        const chatBox = document.getElementById('chatHistory');
        chatBox.innerHTML += `
            <div class="msg msg-user">
                <span class="msg-author">Tu</span>
                <p>${testo}</p>
                <small>Ora</small>
            </div>`;
        chatBox.scrollTop = chatBox.scrollHeight;
    }, 500);
}

// 2. MOCKUP: Aggiornamento Stato (Admin) con Toggle Custom
function aggiornaStato(e) {
    e.preventDefault();
    const toggleStato = document.getElementById('toggleStatoAdmin');
    
    // Se il checkbox è spuntato è "Chiusa", altrimenti è "In carico"
    const nuovoStato = toggleStato.checked ? 'Chiusa' : 'In carico';
    
    // Falsifichiamo la risposta del server
    setTimeout(() => {
        showToast(`Stato aggiornato a: ${nuovoStato}`);
        
        // Se è stata chiusa, ricarichiamo la pagina per toglierla dall'elenco "In carico"
        if(nuovoStato === 'Chiusa') {
            setTimeout(() => chiudiOverlay(), 1500);
            setTimeout(() => window.location.reload(), 1600);
        } else {
            // Se è stata solo riaperta, cancelliamo il warning
            const warningMsg = document.getElementById('adminStatusWarning');
            if (warningMsg) warningMsg.classList.remove('show');
        }
    }, 500);
}

// ---------------------------------------------------------
// CHIAMATE REALI: Queste puntano alle tue Servlet delle Email
// ---------------------------------------------------------

// 3. REALE: Assegnazione Admin (Superadmin)
function assegnaAdmin(e) {
    e.preventDefault();
    const nuovoAdmin = document.getElementById('adminInCarico').value;
    const rma = document.getElementById('dettaglioRma').innerText;
    
    fetch('../AssegnaAdminServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ rma: rma, nuovoAdmin: nuovoAdmin })
    })
    .then(response => {
        if(response.ok) {
            showToast(`Admin ${nuovoAdmin} assegnato. Email automatica inviata!`);
        } else {
            showToast("Errore durante l'assegnazione.");
        }
    })
    .catch(() => showToast("Errore di connessione."));
}

// 4. REALE: Invio Nota Privata (Superadmin)
function inviaNota(e) {
    e.preventDefault();
    const nota = document.getElementById('notaSuperadmin').value;
    const rma = document.getElementById('dettaglioRma').innerText;
    
    fetch('../InviaNotaServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ rma: rma, nota: nota })
    })
    .then(response => {
        if(response.ok) {
            showToast("Nota privata spedita via email all'Admin in carico.");
            document.getElementById('notaSuperadmin').value = '';
        } else {
            showToast("Errore nell'invio della nota.");
        }
    })
    .catch(() => showToast("Errore di connessione."));
}


// =========================================================
// GESTIONE CREAZIONE NUOVO TICKET (STEP 2)
// =========================================================

const formTicketFinale = document.getElementById('formTicketFinale');

if (formTicketFinale) {
    const inputTitolo = document.getElementById('titoloTicket');
    const inputCategoria = document.getElementById('categoriaTicket');
    const inputDescrizione = document.getElementById('descrizioneTicket');

    // Funzione di validazione Regex
    function validaCampo(campo, regex, errorId) {
        const isValid = regex.test(campo.value.trim());
        const errorMsg = document.getElementById(errorId);
        
        if (!isValid) {
            campo.classList.add('input-error');
            errorMsg.classList.add('show');
        } else {
            campo.classList.remove('input-error');
            errorMsg.classList.remove('show');
        }
        return isValid;
    }

    // Validazione "Live" all'uscita dal campo (blur) o input
    const regexTitolo = /^.{5,50}$/;
    const regexDescrizione = /^[\s\S]{20,1024}$/;
    
    inputTitolo.addEventListener('blur', () => validaCampo(inputTitolo, regexTitolo, 'errorTitolo'));
    inputTitolo.addEventListener('input', () => validaCampo(inputTitolo, regexTitolo, 'errorTitolo'));
    
    inputDescrizione.addEventListener('blur', () => validaCampo(inputDescrizione, regexDescrizione, 'errorDescrizione'));
    inputDescrizione.addEventListener('input', () => validaCampo(inputDescrizione, regexDescrizione, 'errorDescrizione'));

    inputCategoria.addEventListener('change', function() {
        if(this.value !== "") {
            this.classList.remove('input-error');
            document.getElementById('errorCategoria').classList.remove('show');
        }
    });

    // Intercetta il Submit per l'invio AJAX
    formTicketFinale.addEventListener('submit', function(e) {
        e.preventDefault();

        const isTitoloValid = validaCampo(inputTitolo, regexTitolo, 'errorTitolo');
        const isDescrizioneValid = validaCampo(inputDescrizione, regexDescrizione, 'errorDescrizione');
        let isCategoriaValid = inputCategoria.value !== "";
        
        if(!isCategoriaValid) {
            inputCategoria.classList.add('input-error');
            document.getElementById('errorCategoria').classList.add('show');
        }

        // Se un campo fallisce la validazione, blocchiamo l'invio
        if (!isTitoloValid || !isDescrizioneValid || !isCategoriaValid) {
            return; 
        }

        // 1. Formattazione: uniamo selezioni e descrizione
        let selezioniText = "";
        const ordiniNodes = document.querySelectorAll('.hidden-selezione-ordine');
        const prodottiNodes = document.querySelectorAll('.hidden-selezione-prodotto');
        
        const ordiniValues = Array.from(ordiniNodes).map(n => n.value);
        const prodottiValues = Array.from(prodottiNodes).map(n => n.value);

        if (ordiniValues.length > 0 || prodottiValues.length > 0) {
            if (ordiniValues.length > 0) selezioniText += "Ordini Selezionati: " + ordiniValues.join(", ") + "\n";
            if (prodottiValues.length > 0) selezioniText += "Prodotti Selezionati: " + prodottiValues.join(", ") + "\n";
            selezioniText += "----------------------------------------\n\n";
        }
        
        const descrizioneFormattata = selezioniText + inputDescrizione.value.trim();

        // 2. Disabilitiamo il bottone per mostrare il caricamento fittizio
        const submitBtn = formTicketFinale.querySelector('button[type="submit"]');
        submitBtn.disabled = true;
        submitBtn.innerHTML = "INVIO IN CORSO...";

        // 3. MOCKUP: Simuliamo una chiamata di rete che ci mette 800 millisecondi
        setTimeout(() => {
            console.log("Dati che verrebbero inviati alla Servlet:");
            console.log("Titolo:", inputTitolo.value.trim());
            console.log("Categoria:", inputCategoria.value);
            console.log("Descrizione Finale:\n", descrizioneFormattata);
            
            mostraSuccessoERedirect();
        }, 800);
    });

    // 4. Chiamata Reale (Mockata con un Timeout temporaneo per farti testare l'UX)
    /* DECOMMENTA QUESTO BLOCCO QUANDO LA SERVLET SARÀ PRONTA E RIMUOVI IL MOCKUP SOPRA
    fetch('../CreaTicketServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData
    })
    .then(response => {
        if(response.ok) {
            mostraSuccessoERedirect();
        } else {
            showToast("Errore durante la creazione del ticket.");
            riabilitaBottone(submitBtn);
        }
    })
    .catch(() => {
        showToast("Errore di connessione.");
        riabilitaBottone(submitBtn);
    });
    */

    function riabilitaBottone(btn) {
        btn.disabled = false;
        btn.innerHTML = "INVIA TICKET";
    }

    function mostraSuccessoERedirect() {
        const popup = document.getElementById("popupConferma");
        popup.classList.add("active");
        
        // Aspetta 2 secondi per far leggere il feedback, poi ricarica
        setTimeout(() => {
            window.location.href = "centroAssistenza.jsp";
        }, 2000);
    }
}