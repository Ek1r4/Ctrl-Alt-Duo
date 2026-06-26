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