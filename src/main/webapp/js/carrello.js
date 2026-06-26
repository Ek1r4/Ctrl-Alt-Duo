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
            alert(data.message);
            window.location.reload();
        }
    }).catch(error => console.error('Errore:', error));
}

function rimuoviProdottoAJAX(idProdotto) {
    if(!confirm('Sicuro di voler rimuovere il prodotto?')) return;
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

function gestisciClickMeno(idProdotto) {
    const inputField = document.getElementById('qty-' + idProdotto);
    parseInt(inputField.value) === 1 ? rimuoviProdottoAJAX(idProdotto) : modificaQuantita(idProdotto, -1);
}

function modificaQuantita(idProdotto, delta) {
    const inputField = document.getElementById('qty-' + idProdotto);
    let nuovaQuantita = parseInt(inputField.value) + delta;
    if (nuovaQuantita >= 1) {
        inputField.value = nuovaQuantita;
        const iconaMeno = document.getElementById('icon-minus-' + idProdotto);
        iconaMeno.className = (nuovaQuantita === 1) ? 'fas fa-trash-alt' : 'fas fa-minus';
        aggiornaQuantitaAJAX(idProdotto, nuovaQuantita);
    }
}