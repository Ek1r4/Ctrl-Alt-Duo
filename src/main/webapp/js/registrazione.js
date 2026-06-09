document.addEventListener("DOMContentLoaded", function() {
    
    // SETUP E INIZIALIZZAZIONE
    const form = document.getElementById("registrazioneForm");
    const btnSubmit = document.getElementById("btnSubmit");
    
    const nome = document.getElementById("nome");
    const cognome = document.getElementById("cognome");
    const username = document.getElementById("username");
    const email = document.getElementById("email");
    const telefono = document.getElementById("telefono");
    const password = document.getElementById("password");
    const confermaPassword = document.getElementById("confermaPassword");

    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get("success") === "registrazione") {
        mostraNotifica("Registrazione completata con successo! Ora puoi accedere.", "success-banner");
    }

    btnSubmit.disabled = true;

    // CONFIGURAZIONE VALIDATORI
    const validatori = {
        nome: () => validaLunghezza(nome, "nomeError", "Il nome deve avere almeno 2 caratteri."),
        cognome: () => validaLunghezza(cognome, "cognomeError", "Il cognome deve avere almeno 2 caratteri."),
        username: () => validaRegex(username, "usernameError", /^[a-zA-Z0-9_]+$/, "Solo lettere, numeri e underscore (_) senza spazi."),
        email: () => validaRegex(email, "emailError", /^[^\s@]+@[^\s@]+\.[^\s@]+$/, "Email non valida (es. nome@dominio.it)."),
        telefono: () => validaRegex(telefono, "telefonoError", /^[0-9]{10}$/, "Il numero deve contenere esattamente 10 cifre (senza spazi o prefissi)."),
        password: () => validaPassword(password, "passwordError"),
        confermaPassword: () => validaConferma(password, confermaPassword, "confermaError")
    };

    const campi = [nome, cognome, username, email, telefono, password, confermaPassword];
    campi.forEach(campo => {
        campo.addEventListener("input", controllaFormInTempoReale);
        campo.addEventListener("blur", () => {
            campo.value = campo.value.trim();
            controllaFormInTempoReale();
        });
    });

    // FUNZIONI DI VALIDAZIONE
    function validaLunghezza(input, errorId, msg) {
        if (input.value.trim().length < 2) {
            mostraErrore(errorId, msg);
            return false;
        }
        nascondiErrore(errorId);
        return true;
    }

    function validaRegex(input, errorId, regex, msg) {
        const value = input.value.trim();
        if (!value.match(regex) || value === "") {
            mostraErrore(errorId, msg);
            return false;
        }
        nascondiErrore(errorId);
        return true;
    }

    function validaPassword(input, errorId) {
        const value = input.value; 
        const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        if (!value.match(regex)) {
            mostraErrore(errorId, "Minimo 8 caratteri: 1 Maiuscola, 1 minuscola, 1 numero, 1 carattere speciale.");
            return false;
        }
        nascondiErrore(errorId);
        return true;
    }

    function validaConferma(passInput, confInput, errorId) {
        if (confInput.value === "" || passInput.value !== confInput.value) {
            mostraErrore(errorId, "Le password non coincidono.");
            return false;
        }
        nascondiErrore(errorId);
        return true;
    }

	// GESTIONE STATO DEL FORM
	function controllaFormInTempoReale() {
		let formValido = true;
		
		for (let key in validatori) {

			let campoCorretto = validatori[key]();
	        // Se anche un solo campo restituisce false, il formValido globale diventa false
	        if (!campoCorretto) {
	            formValido = false;
	            }
	        }
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
    
    // Per la registrazione (che ha una classe container leggermente diversa)
    const container = document.querySelector('.film-container');
    if (container) {
        container.insertBefore(banner, container.firstChild);
    } else {
        document.body.insertBefore(banner, document.body.firstChild);
    }
    
    setTimeout(() => { banner.remove(); }, 4000);
}