// INIZIALIZZAZIONE E ROUTING
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

    // Sfrutta l'History API per rimuovere il parametro RMA dall'URL corrente senza innescare un refresh della pagina, prevenendo la riapertura automatica del modale ai successivi reload.
    window.history.replaceState({}, document.title, window.location.pathname);
    
    document.getElementById('chatHistory').innerHTML = '<div class="testo-tecnico loading-text">CARICAMENTO MESSAGGI...</div>';
    
    fetch('../DettaglioPraticaServlet?rma=' + rma)
        .then(response => {
            if (!response.ok) throw new Error("Errore nel recupero della pratica");
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
}

// POPOLAMENTO OVERLAY E CHAT
function popolaOverlay(data) {
    document.getElementById('dettaglioRma').innerText = data.rma;
    document.getElementById('dettaglioData').innerText = data.dataApertura;
    
    if (document.getElementById('dettaglioTitolo')) document.getElementById('dettaglioTitolo').innerText = data.titolo;
    if (document.getElementById('dettaglioCategoria')) document.getElementById('dettaglioCategoria').innerText = data.categoria.toUpperCase();

    const chatBox = document.getElementById('chatHistory');
    chatBox.innerHTML = '';
    let htmlPills = '';

    // Estrazione dei metadati di contesto (Ordini/Prodotti) incapsulati nella stringa di descrizione al momento della creazione del ticket, separati tramite un delimitatore posizionale.
    let testoDescrizione = data.descrizione;
    const separator = "----------------------------------------";
    
    if (testoDescrizione && testoDescrizione.includes(separator)) {
        const parts = testoDescrizione.split(separator);
        const intestazione = parts[0];
        
        testoDescrizione = parts.slice(1).join(separator).trim();
        
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

    if (testoDescrizione) {
        testoDescrizione = testoDescrizione.replace(/\n/g, "<br>");
        
        const classeApertura = data.isProprietario ? 'msg-mine' : 'msg-other';
        
        chatBox.innerHTML += `
            <div class="msg ${classeApertura}">
                <span class="msg-author">Apertura Pratica</span>
                <p>${testoDescrizione}</p>
                <small>${data.dataApertura}</small>
            </div>
        `;
    }

    if (data.messaggi && data.messaggi.length > 0) {
        data.messaggi.forEach(msg => {
            
            // Parsing condizionale dei messaggi generati dal server per la renderizzazione inline dei separatori di sistema.
            if (msg.testo.startsWith('[NOTIFICA DI SISTEMA]')) {
                const testoPulito = msg.testo.replace('[NOTIFICA DI SISTEMA] - ', '').replace('[NOTIFICA DI SISTEMA]', '');
                
                chatBox.innerHTML += `
                    <div class="chat-system-divider">
                        <span><i class="ri-information-line" style="margin-right: 4px;"></i>${testoPulito}</span>
                    </div>
                `;
            } else {
                let testoDaMostrare = msg.testo.replace(/\n/g, "<br>");
                
                let classeMessaggio = 'msg-other'; 
                
                /*
                 * Inversione dinamica delle classi CSS della chat (destra/sinistra) 
                 * basata sul ruolo di chi interroga l'endpoint (isProprietario: true per il Cliente, false per gli Admin),
                 * garantendo che il mittente corrente percepisca sempre i propri messaggi sulla destra.
                 */
                if (data.isProprietario) {
                    classeMessaggio = (msg.tipo === 'User') ? 'msg-mine' : 'msg-other';
                } else {
                    classeMessaggio = (msg.tipo === 'Admin') ? 'msg-mine' : 'msg-other';
                }
                
                chatBox.innerHTML += `
                    <div class="msg ${classeMessaggio}">
                        <span class="msg-author">${msg.autore}</span>
                        <p>${testoDaMostrare}</p>
                        <small>${msg.data}</small>
                    </div>
                `;
            }
        });
    } else if (!testoDescrizione) {
        chatBox.innerHTML = '<div class="testo-tecnico">Nessun dettaglio presente.</div>';
    }
    
    chatBox.scrollTop = chatBox.scrollHeight;

    const selezioniBox = document.getElementById('dettaglioSelezioni');
    if (selezioniBox) selezioniBox.innerHTML = htmlPills;

    if (document.getElementById('chatRma')) document.getElementById('chatRma').value = data.rma;
    if (document.getElementById('dettaglioStato')) document.getElementById('dettaglioStato').innerText = data.stato;
    if (document.getElementById('dettaglioStatoSuper')) document.getElementById('dettaglioStatoSuper').innerText = data.stato;
    if (document.getElementById('adminInCarico')) document.getElementById('adminInCarico').value = data.adminAssegnato;
    
    // Gestione permessi UI in base allo stato del ticket
    const formChat = document.getElementById('formChat');
    
    if (data.stato === 'Chiusa') {
        if (document.getElementById('utenteTicketChiuso')) document.getElementById('utenteTicketChiuso').style.display = 'flex';
        if (formChat) formChat.style.display = 'none'; 
    } else {
        if (document.getElementById('utenteTicketChiuso')) document.getElementById('utenteTicketChiuso').style.display = 'none';
        if (formChat) formChat.style.display = 'block'; 
    }

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
    switchMobileTab('chat');
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

// AZIONI AJAX (MESSAGGI E STATO)

function inviaMessaggio(e) {
    e.preventDefault();
    
    const inputTesto = document.getElementById('nuovoMessaggio');
    const testo = inputTesto.value.trim();
    const rma = document.getElementById('dettaglioRma').innerText; 
    const btnInvia = e.target.querySelector('button[type="submit"]');

    if (!testo) return;

    btnInvia.disabled = true;
    inputTesto.disabled = true;

    fetch('../ChatServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ rma: rma, testo: testo })
    })
    .then(response => {
        if(response.ok) {
            inputTesto.value = '';
            inputTesto.disabled = false;
            btnInvia.disabled = false;
            
            fetch('../DettaglioPraticaServlet?rma=' + rma)
                .then(res => res.json())
                .then(data => popolaOverlay(data));
                
        } else if (response.status === 403) {
            showToast("La pratica è chiusa. Non è possibile inviare messaggi.");
            chiudiOverlay();
        } else {
            throw new Error();
        }
    })
    .catch(() => {
        showToast("Errore durante l'invio del messaggio.");
        inputTesto.disabled = false;
        btnInvia.disabled = false;
    });
}

function aggiornaStato(e) {
    e.preventDefault();
    
    const toggleStato = document.getElementById('toggleStatoAdmin');
    const rma = document.getElementById('dettaglioRma').innerText; 
    
    const nuovoStato = toggleStato.checked ? 'Chiusa' : 'In carico';
    
    const btnSubmit = e.target.querySelector('button[type="submit"]');
    if (btnSubmit) {
        btnSubmit.disabled = true;
        btnSubmit.innerText = "AGGIORNAMENTO...";
    }

    fetch('../AggiornaPraticaServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ action: 'aggiornaStato', rma: rma, stato: nuovoStato }) 
    })
    .then(response => {
        if(response.ok) {
            if(nuovoStato === 'Chiusa') {
                showToast("Pratica CHIUSA. Email di notifica inviata al cliente!");
                setTimeout(() => chiudiOverlay(), 1500);
                setTimeout(() => window.location.reload(), 1600);
            } else {
                showToast("Pratica RIAPERTA. Email di avviso inviata al cliente!");
                const warningMsg = document.getElementById('adminStatusWarning');
                if (warningMsg) warningMsg.classList.remove('show');
                
                setTimeout(() => window.location.reload(), 1600); 
            }
        } else {
            showToast("Errore durante l'aggiornamento dello stato.");
            if (btnSubmit) {
                btnSubmit.disabled = false;
                btnSubmit.innerText = "AGGIORNA STATO";
            }
        }
    })
    .catch(() => {
        showToast("Errore di connessione al server.");
        if (btnSubmit) {
            btnSubmit.disabled = false;
            btnSubmit.innerText = "AGGIORNA STATO";
        }
    });
}

function assegnaAdmin(e) {
    e.preventDefault();
    const nuovoAdmin = document.getElementById('adminInCarico').value;
    const rma = document.getElementById('dettaglioRma').innerText;
    
    const btnSubmit = e.target.querySelector('button[type="submit"]');
    if (btnSubmit) {
        btnSubmit.disabled = true;
        btnSubmit.innerText = "ASSEGNAZIONE...";
    }
    
    fetch('../AggiornaPraticaServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ action: 'assegnaAdmin', rma: rma, nuovoAdmin: nuovoAdmin })
    })
    .then(response => {
        if(response.ok) {
            showToast(`Admin ${nuovoAdmin} assegnato. Email automatica inviata!`);
            
            setTimeout(() => chiudiOverlay(), 1500);
            setTimeout(() => window.location.reload(), 1600);
        } else {
            showToast("Errore durante l'assegnazione.");
            if (btnSubmit) {
                btnSubmit.disabled = false;
                btnSubmit.innerText = "ASSEGNA E NOTIFICA";
            }
        }
    })
    .catch(() => {
        showToast("Errore di connessione.");
        if (btnSubmit) {
            btnSubmit.disabled = false;
            btnSubmit.innerText = "ASSEGNA E NOTIFICA";
        }
    });
}

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

// CREAZIONE NUOVO TICKET (STEP 1 & 2)

document.addEventListener("DOMContentLoaded", function() {
    const formSelezioneOrdine = document.getElementById("formSelezioneOrdine");
    
    // Gestione interdipendenza checkbox (Step 1): sincronizza la selezione automatica degli item child se il parent (ordine intero) viene flaggato, abilitando proceduralmente la Call to Action.
    if (formSelezioneOrdine) {
        const floatingSubmit = document.getElementById("floatingSubmit");
        const ordiniItems = document.querySelectorAll(".ordine-item");

        function checkVisibility() {
            const anyChecked = formSelezioneOrdine.querySelectorAll('input[type="checkbox"]:checked').length > 0;
            if (anyChecked) {
                floatingSubmit.classList.add("visible");
            } else {
                floatingSubmit.classList.remove("visible");
            }
        }

        ordiniItems.forEach(item => {
            const cbOrdine = item.querySelector(".cb-ordine");
            const cbProdotti = item.querySelectorAll(".cb-prodotto");

            if (cbOrdine) {
                cbOrdine.addEventListener("change", function() {
                    cbProdotti.forEach(cb => cb.checked = this.checked);
                    checkVisibility();
                });
            }

            if (cbProdotti.length > 0) {
                cbProdotti.forEach(cb => {
                    cb.addEventListener("change", function() {
                        const anyProductChecked = Array.from(cbProdotti).some(p => p.checked);
                        if (cbOrdine) cbOrdine.checked = anyProductChecked;
                        checkVisibility();
                    });
                });
            }
        });
    }
});

const formTicketFinale = document.getElementById('formTicketFinale');

if (formTicketFinale) {
    const inputTitolo = document.getElementById('titoloTicket');
    const inputCategoria = document.getElementById('categoriaTicket');
    const inputDescrizione = document.getElementById('descrizioneTicket');

    // Funzione di validazione regex associata all'impiego della classe 'input-error' per il visual feedback dell'utente in caso di non aderenza ai boundary (es. lunghezza stringa).
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

    formTicketFinale.addEventListener('submit', function(e) {
        e.preventDefault();

        const isTitoloValid = validaCampo(inputTitolo, regexTitolo, 'errorTitolo');
        const isDescrizioneValid = validaCampo(inputDescrizione, regexDescrizione, 'errorDescrizione');
        let isCategoriaValid = inputCategoria.value !== "";
        
        if(!isCategoriaValid) {
            inputCategoria.classList.add('input-error');
            document.getElementById('errorCategoria').classList.add('show');
        }

        if (!isTitoloValid || !isDescrizioneValid || !isCategoriaValid) {
            return; 
        }

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
        
        // Composizione del payload finale.
        const descrizioneFormattata = selezioniText + inputDescrizione.value.trim();

        const submitBtn = formTicketFinale.querySelector('button[type="submit"]');
        submitBtn.disabled = true;
        submitBtn.innerHTML = "INVIO IN CORSO...";

        fetch('../CreaPraticaServlet', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ 
                titolo: inputTitolo.value.trim(), 
                categoria: inputCategoria.value, 
                descrizione: descrizioneFormattata 
            })
        })
        .then(response => {
            if(response.ok) {
                mostraSuccessoERedirect();
            } else {
                showToast("Errore durante la creazione della pratica.");
                riabilitaBottone(submitBtn);
            }
        })
        .catch(() => {
            showToast("Errore di connessione al server.");
            riabilitaBottone(submitBtn);
        });
    });
    
    function riabilitaBottone(btn) {
        btn.disabled = false;
        btn.innerHTML = "INVIA TICKET";
    }

    function mostraSuccessoERedirect() {
        const popup = document.getElementById("popupConferma");
        popup.classList.add("active");
        
        setTimeout(() => {
            window.location.href = "centroAssistenza.jsp";
        }, 2000);
    }
}

// LIVE SEARCH E GRIGLIA
document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.querySelector('.assistenza-search-box input[name="query"]');
    const searchForm = document.querySelector('.assistenza-search-box');
    const clearIcon = document.querySelector('.clear-icon');
    
    if (searchInput) {
        
        if(searchForm) {
            searchForm.addEventListener('submit', (e) => e.preventDefault());
        }

        const caricaPratiche = (query = "") => {
            fetch('../ListaPraticheServlet?q=' + encodeURIComponent(query))
                .then(response => {
                    if (!response.ok) throw new Error("Errore nel recupero lista");
                    return response.json();
                })
                .then(data => {
                    gestisciVisibilita(data.ruolo, query);
                    renderizzaGriglia(data.risultati, data.ruolo);
                })
                .catch(err => console.error("Errore fetch dashboard:", err));
        };

        function gestisciVisibilita(ruolo, query) {
            const resultsContainer = document.querySelector('.pratiche-results-container');
            const quickButtons = document.querySelector('.quick-search-buttons');

            // Logica di routing visuale condizionato dai ruoli di sessione (0 = Cliente): mostra pulsanti di scorciatoia solo ai clienti quando la stringa di ricerca è vuota, altrimenti esegue dump della griglia.
            if (ruolo === 0) {
                if (query === '') {
                    if (resultsContainer) resultsContainer.style.display = 'none';
                    if (quickButtons) quickButtons.style.display = 'flex'; 
                } else {
                    if (resultsContainer) resultsContainer.style.display = 'block';
                    if (quickButtons) quickButtons.style.display = 'none';
                }
            } else {
                if (resultsContainer) resultsContainer.style.display = 'block';
                if (quickButtons) quickButtons.style.display = 'none';
            }
            
            if (clearIcon) {
                clearIcon.style.display = query === '' ? 'none' : 'block';
            }
        }

        // Listener reattivo all'input per abilitare l'aggiornamento automatico asincrono della DOM, senza forzare il reload.
        searchInput.addEventListener('input', (e) => {
            const query = e.target.value.trim();
            caricaPratiche(query);
        });

        if(clearIcon) {
            clearIcon.addEventListener('click', () => {
                searchInput.value = '';
                caricaPratiche(''); 
                window.history.replaceState({}, document.title, window.location.pathname);
            });
        }

        const quickFilters = document.querySelectorAll('.quick-filter');
        if (quickFilters.length > 0) {
            quickFilters.forEach(btn => {
                btn.addEventListener('click', () => {
                    const termine = btn.getAttribute('data-filter');
                    searchInput.value = termine; 
                    caricaPratiche(termine);     
                });
            });
        }
        
        caricaPratiche(searchInput.value.trim());
    }
});

function renderizzaGriglia(pratiche, ruolo) {
    const gridContainer = document.querySelector('.pratiche-grid');
    if (!gridContainer) return;

    gridContainer.innerHTML = ''; 

    if (pratiche.length === 0) {
        gridContainer.innerHTML = '<div class="testo-tecnico" style="text-align:center; padding: 20px;">Nessun ticket trovato.</div>';
        return;
    }

    // Costruzione differenziata dei badge all'interno delle righe tabella, alterando le metriche mostrate a seconda del livello di autorizzazione.
    pratiche.forEach(pratica => {
        
        let htmlRiga = `<a href="?rma=${pratica.rma}" class="pratica-grid-row">
                            <div class="pratica-titolo">${pratica.titolo}</div>`;
        
        if (ruolo === 0) {
            const cssStato = pratica.stato.toLowerCase().replace(' ', '-');
            htmlRiga += `   <div class="pratica-col-center">${pratica.data}</div>
                            <div class="pratica-col-right status-${cssStato}">
                                ${pratica.stato.toUpperCase()}
                            </div>`;
        } else {
            htmlRiga += `   <div class="pratica-col-center">
                                <i class="ri-user-line" style="margin-right: 5px;"></i>${pratica.utente}
                            </div>`;
                            
            if (ruolo === 2) {
                const isUrgent = (pratica.admin === 'Da assegnare') ? 'badge-urgent-assign' : '';
                const warningIcon = (pratica.admin === 'Da assegnare') ? '<i class="ri-error-warning-line" style="margin-right: 4px;"></i>' : '';
                
                htmlRiga += `<div class="pratica-col-right admin-badge ${isUrgent}">
                                ${warningIcon}${pratica.admin.toUpperCase()}
                             </div>`;
            } else {
                const cssStato = pratica.stato.toLowerCase().replace(' ', '-');
                htmlRiga += `<div class="pratica-col-right status-${cssStato}">
                                ${pratica.stato.toUpperCase()}
                             </div>`;
            }
        }
        
        htmlRiga += `</a>`;
        gridContainer.innerHTML += htmlRiga;
    });
}

// GESTIONE UI MOBILE

function switchMobileTab(tabName) {
    const content = document.querySelector('.ticket-dialog-content');
    const btns = document.querySelectorAll('.mobile-ticket-tabs .tab-btn');

    if (!content || btns.length < 2) return;

    if (tabName === 'dettagli') {
        content.classList.add('show-dettagli');
        btns[0].classList.remove('active'); 
        btns[1].classList.add('active');    
    } else {
        content.classList.remove('show-dettagli');
        btns[0].classList.add('active');    
        btns[1].classList.remove('active'); 
    }
}