// 1. Trigger automatico della stampa (Si attiva SOLO se siamo fisicamente nella pagina del PDF)
window.addEventListener('DOMContentLoaded', () => {
    if (document.querySelector('.invoice-box')) {
        window.print();
    }
});

// 2. Funzione per il bottone (Invia la richiesta all'Iframe nascosto)
function stampaFatturaNascosta(idOrdine, contextPath) {
    const iframe = document.getElementById('iframeFattura');
    if (iframe) {
        iframe.src = contextPath + '/Fattura?id=' + idOrdine;
    }
}