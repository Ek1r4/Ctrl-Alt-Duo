document.addEventListener('DOMContentLoaded', function() {
    const mobileMenuBtn = document.getElementById('mobileMenuBtn');
    const siteHeader = document.querySelector('.site-header');
    
    if (mobileMenuBtn && siteHeader) {
        mobileMenuBtn.addEventListener('click', function() {
            // Aggiunge o toglie la classe "menu-open" all'header
            siteHeader.classList.toggle('menu-open');
        });
    }
});

// Funzione globale che aggiorna il pallino del carrello da altre pagine
function aggiornaBadgeCarrello(quantitaTotale) {
    const badge = document.querySelector('.cart-badge');
    if (!badge) return;

    if (quantitaTotale > 0) {
        badge.textContent = quantitaTotale;
        badge.style.display = 'flex'; 
    } else {
        badge.style.display = 'none'; 
    }
}

// Logica per rimuovere un prodotto dalla tendina della preview
function rimuoviDaMiniCart(idProdotto) {
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
            // Ricarica per mantenere la preview e la pagina sincronizzate
            window.location.reload(); 
        }
    })
    .catch(error => console.error('Errore:', error));
}
// Aggiunta prodotto dalla griglia senza ricaricare la pagina
function aggiungiVeloceAJAX(idProdotto) {
    const formData = new URLSearchParams();
    formData.append('action', 'add');
    formData.append('id', idProdotto);
    formData.append('quantita', 1);
    formData.append('ajax', 'true');

    fetch(contestoReFrame + '/Carrello', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    })
    .then(response => response.json())
    .then(data => {
        if(data.status === 'success') {
            // 1. Aggiorna il pallino rosso
            aggiornaBadgeCarrello(data.quantitaTotale);

            // 2. Aggiorna l'HTML dentro la tendina del mini-cart
            aggiornaDOMMiniCart(idProdotto, data);

            // 3. Spalanca la tendina per 3 secondi
            mostraTendinaCarrello();
			} else if (data.status === 'error') {
			            // Sostituito il vecchio alert con la nostra notifica personalizzata (false = errore/rosso)
			            window.mostraToastNotifica(data.message, false); 
			        }
    })
    .catch(error => console.error('Errore Aggiunta Carrello:', error));
}

// Ricostruisce dinamicamente i dati nella tendina preview
function aggiornaDOMMiniCart(idProdotto, data) {
    const miniCartItems = document.querySelector('.mini-cart-items');
    if(!miniCartItems) return;

    // Rimuovi il messaggio "Nessun articolo presente" se il carrello era vuoto
    const emptyMsg = miniCartItems.querySelector('.empty-mc');
    if (emptyMsg) emptyMsg.remove();

    // Cerca se la riga esiste già (aggiorniamo solo le quantità)
    let rigaEsistente = document.getElementById('mc-item-' + idProdotto);

    if (rigaEsistente) {
        rigaEsistente.querySelector('.mc-qty').innerText = 'Quantità: ' + data.quantitaRiga;
        rigaEsistente.querySelector('.mc-price').innerText = '€ ' + data.totaleRiga.toFixed(2);
    } else {
        // Al altrimenti, crea il blocco HTML del nuovo prodotto da zero
        const nuovaRiga = document.createElement('div');
        nuovaRiga.className = 'mc-item';
        nuovaRiga.id = 'mc-item-' + idProdotto;
        nuovaRiga.innerHTML = `
            <div class="mc-item-info">
                <span class="mc-name">${data.nomeProdottoRiga}</span>
                <span class="mc-qty">Quantità: ${data.quantitaRiga}</span>
            </div>
            <div class="mc-item-actions">
                <span class="mc-price">€ ${data.totaleRiga.toFixed(2)}</span>
                <button type="button" class="mc-remove-btn" onclick="rimuoviDaMiniCart('${idProdotto}')" title="Rimuovi">
                    <i class="fas fa-trash-alt"></i>
                </button>
            </div>
        `;
        miniCartItems.appendChild(nuovaRiga);
    }

    // Gestione del Footer (il totale e il bottone Checkout)
    let miniCartFooter = document.querySelector('.mini-cart-footer');
    
    // Se non esisteva (perché il carrello era vuoto prima), lo creiamo e lo appendiamo
    if (!miniCartFooter) {
        const preview = document.querySelector('.mini-cart-preview');
        miniCartFooter = document.createElement('div');
        miniCartFooter.className = 'mini-cart-footer';
        miniCartFooter.innerHTML = `
            <div class="mc-total">
                <span>TOTALE:</span>
                <span id="mc-totale-complessivo">€ ${data.totaleCarrello.toFixed(2)}</span>
            </div>
            <a href="${contestoReFrame}/common/checkout.jsp" class="btn-cta mc-checkout-btn">VAI AL CHECKOUT</a>
        `;
        preview.appendChild(miniCartFooter);
    } else {
        // Se esisteva già, aggiorniamo solo il prezzo
        const totaleSpan = document.getElementById('mc-totale-complessivo');
        if(totaleSpan) totaleSpan.innerText = '€ ' + data.totaleCarrello.toFixed(2);
    }
}

// Timer di 3 secondi per aprire/chiudere l'animazione
function mostraTendinaCarrello() {
    const cartWrapper = document.querySelector('.cart-wrapper');
    if(cartWrapper) {
        cartWrapper.classList.add('force-open');
        
        // Se l'utente clicca più volte velocemente, resettiamo il timer
        if (window.cartTimeout) clearTimeout(window.cartTimeout);
        
        window.cartTimeout = setTimeout(() => {
            cartWrapper.classList.remove('force-open');
        }, 3000);
    }
}