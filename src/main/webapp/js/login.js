document.addEventListener("DOMContentLoaded", function() {
    
    // SETUP E INIZIALIZZAZIONE
    const form = document.getElementById("loginForm");
    const btnSubmit = document.getElementById("btnSubmit");
    const email = document.getElementById("email");
    const password = document.getElementById("password");

    // RICEZIONE PARAMETRI URL
    const urlParams = new URLSearchParams(window.location.search);
    let urlCleanNeeded = false;

    // TOAST: Solo per i successi
    if (urlParams.get("success") === "registrazione") {
        mostraNotifica("Registrazione completata con successo! Ora puoi accedere.");
        urlCleanNeeded = true;
    }
    if (urlParams.get("success") === "login") {
        mostraNotifica("Login effettuato con successo!");
        urlCleanNeeded = true;
    }

    // Pulizia dell'URL per nascondere i parametri all'utente
    if (urlCleanNeeded) {
        const cleanUrl = window.location.pathname;
        window.history.replaceState({}, document.title, cleanUrl);
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

    // UTILITY GRAFICHE (ERRORI INLINE ORIGINALI)
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

// UTILITY GRAFICHE (NOTIFICHE GLOBALI TOAST - SOLO SUCCESSO)
function mostraNotifica(messaggio) {
    const toast = document.createElement("div");
    toast.className = "toast-notification";
    
    // Stile fisso verde di successo
    toast.style.borderLeft = "5px solid #4CAF50";
    toast.innerHTML = `<i class="fas fa-check-circle" style="color: #4CAF50; font-size: 24px;"></i> <span>${messaggio}</span>`;
    
    document.body.appendChild(toast);

    setTimeout(() => toast.classList.add("show"), 10);

    setTimeout(() => {
        toast.classList.remove("show");
        setTimeout(() => toast.remove(), 400); 
    }, 3500);
}