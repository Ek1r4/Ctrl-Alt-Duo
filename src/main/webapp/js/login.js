document.addEventListener("DOMContentLoaded", function() {
    
    // SETUP E INIZIALIZZAZIONE
    const form = document.getElementById("loginForm");
    const btnSubmit = document.getElementById("btnSubmit");
    const email = document.getElementById("email");
    const password = document.getElementById("password");

    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get("success") === "registrazione") {
        mostraNotifica("Registrazione completata con successo! Ora puoi accedere.", "success-banner");
    }
    if (urlParams.get("success") === "login") {
        mostraNotifica("Login effettuato con successo!", "success-banner");
    }

    btnSubmit.disabled = true;

    // CONFIGURAZIONE VALIDATORI
    const validatori = {
        email: () => validaEmail(email, "emailError"),
        password: () => validaPassword(password, "passwordError")
    };

    const campi = [email, password];
    campi.forEach(campo => {
        campo.addEventListener("input", controllaFormInTempoReale);
        campo.addEventListener("blur", () => {
            campo.value = campo.value.trim();
            controllaFormInTempoReale();
        });
    });

    // FUNZIONI DI VALIDAZIONE
    function validaEmail(input, errorId) {
        const value = input.value.trim();
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        
        if (value === "") {
            nascondiErrore(errorId);
            return false;
        }
        if (!value.match(regex)) {
            mostraErrore(errorId, "Inserisci un indirizzo email valido.");
            return false;
        }
        nascondiErrore(errorId);
        return true;
    }

    function validaPassword(input, errorId) {
        const value = input.value.trim();
        if (value === "") {
            nascondiErrore(errorId);
            return false;
        }
        nascondiErrore(errorId);
        return true;
    }

    // GESTIONE STATO DEL FORM
    function controllaFormInTempoReale() {
        const formValido = Object.keys(validatori).every(key => validatori[key]());
        btnSubmit.disabled = !formValido;
    }

    // UTILITY GRAFICHE (ERRORI)
    function mostraErrore(id, messaggio) {
        const el = document.getElementById(id);
        if(el) {
            el.textContent = messaggio;
            el.classList.add("visible");
        }
    }

    function nascondiErrore(id) {
        const el = document.getElementById(id);
        if(el) { el.classList.remove("visible"); }
    }
});

// UTILITY GRAFICHE (NOTIFICHE GLOBALI)
function mostraNotifica(messaggio, classeCss) {
    const banner = document.createElement("div");
    banner.textContent = messaggio;
    banner.classList.add("notification-banner", classeCss);
    
    const container = document.querySelector('.film-container');
    if (container) {
        container.insertBefore(banner, container.firstChild);
    } else {
        document.body.insertBefore(banner, document.body.firstChild);
    }
    
    setTimeout(() => { banner.remove(); }, 4000);
}