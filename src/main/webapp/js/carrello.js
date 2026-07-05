/* UTILITIES E NOTIFICHE */

// Inietta proceduralmente il componente toast nel DOM qualora non esista.
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

/* GESTIONE AJAX CARRELLO */

// Sincronizza lo stato del carrello lato server e aggiorna parzialmente il DOM (totali e badge) in base alla risposta JSON, senza ricaricare la pagina.
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
        if (data.status === 'success') {
            document.getElementById('totale-riga-' + idProdotto).innerText = '€ ' + data.totaleRiga.toFixed(2);
            document.getElementById('txt-subtotale').innerText = '€ ' + data.subtotale.toFixed(2);
            document.getElementById('txt-spedizione').innerText = '€ ' + data.spedizione.toFixed(2);
            document.getElementById('txt-totale-complessivo').innerText = '€ ' + data.totaleCarrello.toFixed(2);
            if (typeof aggiornaBadgeCarrello === 'function') aggiornaBadgeCarrello(data.quantitaTotale);
        } else if (data.status === 'error') {
            showToast(data.message);
            setTimeout(() => window.location.reload(), 2000); 
        }
    }).catch(error => console.error('Errore:', error));
}

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
        if (data.status === 'success') {
            if (data.totaleCarrello === 0) window.location.reload();
            document.getElementById('riga-' + idProdotto).remove();
            document.getElementById('txt-subtotale').innerText = '€ ' + data.subtotale.toFixed(2);
            document.getElementById('txt-spedizione').innerText = '€ ' + data.spedizione.toFixed(2);
            document.getElementById('txt-totale-complessivo').innerText = '€ ' + data.totaleCarrello.toFixed(2);
            if (typeof aggiornaBadgeCarrello === 'function') aggiornaBadgeCarrello(data.quantitaTotale);
        }
    }).catch(error => console.error('Errore:', error));
}

/* LOGICA RIMOZIONE E MODALE */

// Mantiene lo stato dell'ID prodotto durante il ciclo di vita della modale di conferma.
let prodottoDaRimuovere = null;

function rimuoviProdottoAJAX(idProdotto) {
    prodottoDaRimuovere = idProdotto; 
    
    const deleteModal = document.getElementById('delete-confirm-modal');
    const deleteMessage = document.getElementById('delete-confirm-message');
    
    if (deleteModal && deleteMessage) {
        deleteMessage.innerHTML = "Sei sicuro di voler rimuovere questo articolo dal carrello?";
        deleteModal.classList.add('active'); 
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const deleteModal = document.getElementById('delete-confirm-modal');
    const btnConfirmDelete = document.getElementById('btn-confirm-delete');
    const btnCancelDelete = document.getElementById('btn-cancel-delete');

    const chiudiModale = () => {
        if (deleteModal) deleteModal.classList.remove('active'); 
        prodottoDaRimuovere = null;
    };

    if (btnCancelDelete) {
        btnCancelDelete.addEventListener('click', chiudiModale);
    }

    if (deleteModal) {
        deleteModal.addEventListener('click', (e) => {
            if (e.target === deleteModal) {
                chiudiModale();
            }
        });
    }

    if (btnConfirmDelete) {
        btnConfirmDelete.addEventListener('click', () => {
            if (prodottoDaRimuovere) {
                eseguiFetchRimozione(prodottoDaRimuovere);
                chiudiModale();
            }
        });
    }
});

/* CONTROLLER QUANTITÀ UI */

function gestisciClickMeno(idProdotto) {
    const inputField = document.getElementById('qty-' + idProdotto);
    parseInt(inputField.value) === 1 ? rimuoviProdottoAJAX(idProdotto) : modificaQuantita(idProdotto, -1);
}

// Intercetta i boundary di stock tramite data-attribute HTML5 e gestisce dinamicamente la transizione visiva dell'icona decremento (minus/trash) in base alla quantità minima raggiungibile.
function modificaQuantita(idProdotto, delta) {
    const inputField = document.getElementById('qty-' + idProdotto);
    let currentQty = parseInt(inputField.value, 10);
    
    let maxStock = parseInt(inputField.getAttribute('data-stock'), 10);
    
    if (delta > 0 && currentQty >= maxStock) {
        showToast("Stock esaurito! Hai raggiunto la quantità massima disponibile.");
        return; 
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