// Assicurati di avere la funzione showToast disponibile in questo file o in uno globale
function showToast(message) {
    let toast = document.querySelector('.toast-notification');
    if (!toast) {
        toast = document.createElement('div');
        toast.className = 'toast-notification';
        document.body.appendChild(toast);
    }
    toast.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;
    toast.classList.add('show');
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

function aggiornaQuantitaAJAX(idProdotto, nuovaQuantita) {
    const formData = new URLSearchParams();
    formData.append('action', 'update');
    formData.append('id', idProdotto);
    formData.append('quantita', nuovaQuantita);
    formData.append('ajax', 'true');

    fetch(contestoReFrame + '/Carrello', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    })
    .then(response => response.json())
    .then(data => {
        if(data.status === 'success') {
            document.getElementById('totale-riga-' + idProdotto).innerText = '€ ' + data.totaleRiga.toFixed(2);
            document.getElementById('txt-subtotale').innerText = '€ ' + data.subtotale.toFixed(2);
            document.getElementById('txt-spedizione').innerText = '€ ' + data.spedizione.toFixed(2);
            document.getElementById('txt-totale-complessivo').innerText = '€ ' + data.totaleCarrello.toFixed(2);
            if(typeof aggiornaBadgeCarrello === 'function') aggiornaBadgeCarrello(data.quantitaTotale);
        } else if (data.status === 'error') {
            // Sostituito il vecchio alert con il nuovo toast
            showToast(data.message);
            // Opzionale: ricarica la pagina dopo 2 secondi se c'è un errore grave di disallineamento
            setTimeout(() => window.location.reload(), 2000); 
        }
    }).catch(error => console.error('Errore:', error));
}

// Variabile "parcheggio" per ricordare quale ID stiamo eliminando
let prodottoDaRimuovere = null;

// Questa funzione ora apre SOLO la modale, non fa più l'AJAX
function rimuoviProdottoAJAX(idProdotto) {
    prodottoDaRimuovere = idProdotto; // Salviamo l'id
    
    const deleteModal = document.getElementById('delete-confirm-modal');
    const deleteMessage = document.getElementById('delete-confirm-message');
    
    if (deleteModal && deleteMessage) {
        deleteMessage.innerHTML = "Sei sicuro di voler rimuovere questo articolo dal carrello?";
        deleteModal.classList.add('active'); // O 'show' in base al tuo CSS
    }
}

// Questa è la nuova funzione che esegue effettivamente la chiamata al server
function eseguiFetchRimozione(idProdotto) {
    const formData = new URLSearchParams();
    formData.append('action', 'remove');
    formData.append('id', idProdotto);
    formData.append('ajax', 'true');
    
    fetch(contestoReFrame + '/Carrello', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    })
    .then(response => response.json())
    .then(data => {
        if(data.status === 'success') {
            if(data.totaleCarrello === 0) window.location.reload();
            document.getElementById('riga-' + idProdotto).remove();
            document.getElementById('txt-subtotale').innerText = '€ ' + data.subtotale.toFixed(2);
            document.getElementById('txt-spedizione').innerText = '€ ' + data.spedizione.toFixed(2);
            document.getElementById('txt-totale-complessivo').innerText = '€ ' + data.totaleCarrello.toFixed(2);
            if(typeof aggiornaBadgeCarrello === 'function') aggiornaBadgeCarrello(data.quantitaTotale);
        }
    }).catch(error => console.error('Errore:', error));
}

// Inizializzazione degli eventi della Modale
document.addEventListener('DOMContentLoaded', () => {
    const deleteModal = document.getElementById('delete-confirm-modal');
    const btnConfirmDelete = document.getElementById('btn-confirm-delete');
    const btnCancelDelete = document.getElementById('btn-cancel-delete');

    // Funzione helper per chiudere e resettare
    const chiudiModale = () => {
        if(deleteModal) deleteModal.classList.remove('active'); // O 'show'
        prodottoDaRimuovere = null;
    };

    // Click su "Annulla"
    if (btnCancelDelete) {
        btnCancelDelete.addEventListener('click', chiudiModale);
    }

    // Click sullo sfondo scuro per chiudere
    if (deleteModal) {
        deleteModal.addEventListener('click', (e) => {
            if (e.target === deleteModal) {
                chiudiModale();
            }
        });
    }

    // Click su "Procedi"
    if (btnConfirmDelete) {
        btnConfirmDelete.addEventListener('click', () => {
            // Se c'è un prodotto parcheggiato in memoria, spariamo la vera chiamata AJAX!
            if (prodottoDaRimuovere) {
                eseguiFetchRimozione(prodottoDaRimuovere);
                chiudiModale();
            }
        });
    }
});

function gestisciClickMeno(idProdotto) {
    const inputField = document.getElementById('qty-' + idProdotto);
    parseInt(inputField.value) === 1 ? rimuoviProdottoAJAX(idProdotto) : modificaQuantita(idProdotto, -1);
}

function modificaQuantita(idProdotto, delta) {
    const inputField = document.getElementById('qty-' + idProdotto);
    let currentQty = parseInt(inputField.value, 10);
    
    // Recupero il limite di stock dal data-attribute
    let maxStock = parseInt(inputField.getAttribute('data-stock'), 10);
    
    // Controllo preventivo: se stiamo aggiungendo (delta > 0) e abbiamo raggiunto lo stock
    if (delta > 0 && currentQty >= maxStock) {
        showToast("Stock esaurito! Hai raggiunto la quantità massima disponibile.");
        return; // Interrompe la funzione e non invia la richiesta AJAX
    }

    let nuovaQuantita = currentQty + delta;
    if (nuovaQuantita >= 1) {
        inputField.value = nuovaQuantita;
        const iconaMeno = document.getElementById('icon-minus-' + idProdotto);
        if (iconaMeno) {
            iconaMeno.className = (nuovaQuantita === 1) ? 'fas fa-trash-alt' : 'fas fa-minus';
        }
        aggiornaQuantitaAJAX(idProdotto, nuovaQuantita);
    }
}