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
    // 1. MOCKUP TEMPORANEO (Per testare la grafica lato frontend)
    // =========================================================
    setTimeout(() => {
        const mockupData = {
            rma: rma,
            dataApertura: "27/06/2026 14:30",
            motivo: "Il prodotto acquistato ha la lente graffiata e richiede sostituzione immediata.",
            stato: "Aperta",
            adminAssegnato: "admin_Mirko",
            ordine: "ORD19512",
            prodotti: ["EOS R5", "Z8", "X-T5"],
            messaggi: [
                { autore: "Tu", tipo: "User", testo: "Ciao, volevo sapere lo stato del mio reso.", data: "27/06/2026 14:35" },
                { autore: "Admin Erika", tipo: "Admin", testo: "Salve, il corriere ritirerà il pacco lunedì mattina.", data: "27/06/2026 16:00" }
            ]
        };
        popolaOverlay(mockupData);
    }, 500); // Finto ritardo di rete di mezzo secondo

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
    // Info condivise
    document.getElementById('dettaglioRma').innerText = data.rma;
    document.getElementById('dettaglioData').innerText = data.dataApertura;
    document.getElementById('dettaglioMotivo').innerText = data.motivo;

    // Popolamento dei "pill" dell'ordine e prodotti
    const selezioniBox = document.getElementById('dettaglioSelezioni');
    selezioniBox.innerHTML = ''; 
    
    if (data.ordine || (data.prodotti && data.prodotti.length > 0)) {
        let htmlPills = `<div class="ticket-selections-analog">
                            <span class="selection-label-analog">ORDINI E PRODOTTI CITATI:</span>`;
        
        if (data.ordine) {
            htmlPills += `<span class="sel-tag-analog">Ordine #${data.ordine}</span>`;
        }
        
        if (data.prodotti && data.prodotti.length > 0) {
            data.prodotti.forEach(prod => {
                htmlPills += `<span class="sel-tag-analog">Prodotto: ${prod}</span>`;
            });
        }
        
        htmlPills += `</div>`;
        selezioniBox.innerHTML = htmlPills;
    }

    // Chat
    const chatBox = document.getElementById('chatHistory');
    chatBox.innerHTML = '';
    
    if (data.messaggi && data.messaggi.length > 0) {
        data.messaggi.forEach(msg => {
            // Controlla se l'autore del messaggio corrisponde al cliente che ha aperto la pratica
            const isUser = msg.tipo === 'User'; 
            chatBox.innerHTML += `
                <div class="msg ${isUser ? 'msg-user' : 'msg-admin'}">
                    <span class="msg-author">${msg.autore}</span>
                    <p>${msg.testo}</p>
                    <small>${msg.data}</small>
                </div>
            `;
        });
    } else {
        chatBox.innerHTML = '<div class="testo-tecnico">Nessun messaggio presente.</div>';
    }
    chatBox.scrollTop = chatBox.scrollHeight;

    // Riempi input in base ai ruoli
    if (document.getElementById('chatRma')) document.getElementById('chatRma').value = data.rma;
    if (document.getElementById('dettaglioStato')) document.getElementById('dettaglioStato').innerText = data.stato;
    if (document.getElementById('dettaglioStatoSuper')) document.getElementById('dettaglioStatoSuper').innerText = data.stato;
    
    if (document.getElementById('selectStato')) document.getElementById('selectStato').value = data.stato;
    if (document.getElementById('adminInCarico')) document.getElementById('adminInCarico').value = data.adminAssegnato;
    
    if (document.getElementById('utenteTicketChiuso') && data.stato === 'Chiusa') {
        document.getElementById('utenteTicketChiuso').style.display = 'flex';
    } else if (document.getElementById('utenteTicketChiuso')) {
        document.getElementById('utenteTicketChiuso').style.display = 'none';
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

// 2. MOCKUP: Aggiornamento Stato (Admin)
function aggiornaStato(e) {
    e.preventDefault();
    const nuovoStato = document.getElementById('selectStato').value;
    
    // Falsifichiamo la risposta del server
    setTimeout(() => {
        showToast(`Stato aggiornato a: ${nuovoStato}`);
        
        if(nuovoStato === 'Chiusa') {
            setTimeout(() => chiudiOverlay(), 1500);
            // Simula il refresh della griglia sottostante
            setTimeout(() => window.location.reload(), 1600);
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