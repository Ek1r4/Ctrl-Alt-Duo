/* INIZIALIZZAZIONE E GESTIONE MENU MOBILE */

// Listener globale per l'apertura/chiusura del menu mobile.
document.addEventListener('DOMContentLoaded', function() {
    const mobileMenuBtn = document.getElementById('mobileMenuBtn');
    const siteHeader = document.querySelector('.site-header');
    
    if (mobileMenuBtn && siteHeader) {
        mobileMenuBtn.addEventListener('click', function() {
            siteHeader.classList.toggle('menu-open');
        });
    }
});

/* AGGIORNAMENTO GLOBALE BADGE CARRELLO */

// Funzione globale invocabile cross-pagina per l'update del contatore articoli visibile sull'icona del carrello header.
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

/* AZIONI MINICART (RIMOZIONE E AGGIUNTA AJAX) */

// Rimuove l'articolo selezionato interagendo asincronamente con la Servlet.
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
            window.location.reload(); 
        }
    })
    .catch(error => console.error('Errore:', error));
}

// Implementa il pattern "Add to Cart" asincrono: aggiorna il backend, il badge header e innesca la ricostruzione dell'UI del minicart senza interrompere l'UX corrente.
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
            aggiornaBadgeCarrello(data.quantitaTotale);
            aggiornaDOMMiniCart(idProdotto, data);
            mostraTendinaCarrello();
        } else if (data.status === 'error') {
            window.mostraToastNotifica(data.message, false); 
        }
    })
    .catch(error => console.error('Errore Aggiunta Carrello:', error));
}

/* MANIPOLAZIONE DINAMICA DEL DOM MINICART */

// Funzione core di re-rendering parziale: analizza la presenza del nodo DOM dell'articolo e ne aggiorna i valori quantitativi, o inietta una nuova riga HTML se l'articolo è nuovo.
function aggiornaDOMMiniCart(idProdotto, data) {
    const miniCartItems = document.querySelector('.mini-cart-items');
    if(!miniCartItems) return;

    const emptyMsg = miniCartItems.querySelector('.empty-mc');
    if (emptyMsg) emptyMsg.remove();

    let rigaEsistente = document.getElementById('mc-item-' + idProdotto);

    if (rigaEsistente) {
        rigaEsistente.querySelector('.mc-qty').innerText = 'Quantità: ' + data.quantitaRiga;
        rigaEsistente.querySelector('.mc-price').innerText = '€ ' + data.totaleRiga.toFixed(2);
    } else {
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

    let miniCartFooter = document.querySelector('.mini-cart-footer');
    
    // Gestione condizionale del footer: se il carrello cambia da stato vuoto a popolato, inietta la struttura HTML del totale e del bottone checkout, altrimenti aggiorna solo l'innerText monetario.
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
        const totaleSpan = document.getElementById('mc-totale-complessivo');
        if(totaleSpan) totaleSpan.innerText = '€ ' + data.totaleCarrello.toFixed(2);
    }
}

/* GESTIONE ANIMAZIONI E TIMEOUT */

// Forza l'apertura visiva del minicart tramite l'iniezione della classe 'force-open'.
function mostraTendinaCarrello() {
    const cartWrapper = document.querySelector('.cart-wrapper');
    if(cartWrapper) {
        cartWrapper.classList.add('force-open');
        
        if (window.cartTimeout) clearTimeout(window.cartTimeout);
        
        window.cartTimeout = setTimeout(() => {
            cartWrapper.classList.remove('force-open');
        }, 3000);
    }
}