document.addEventListener("DOMContentLoaded", () => {
    const stickySection = document.querySelector('.sticky-section-values');
    if (!stickySection) return;

    /* INIZIALIZZAZIONE LIBRERIE E SMOOTH SCROLL */

    gsap.registerPlugin(ScrollTrigger);

    /*
     * Sincronizzazione del ciclo di rendering di Lenis con il ticker interno di GSAP.
     * Previene artefatti visivi (jittering) delegando l'aggiornamento dello ScrollTrigger 
     * al RequestAnimationFrame unificato, garantendo assoluta fluidità nelle interpolazioni legate allo scroll.
     */
    const lenis = new Lenis({
        duration: 1.2,
        easing: (t) => Math.min(1, 1.001 - Math.pow(2, -10 * t)),
    });
    
    function raf(time) {
        lenis.raf(time);
        requestAnimationFrame(raf);
    }
    requestAnimationFrame(raf);

    lenis.on('scroll', ScrollTrigger.update);
    gsap.ticker.add((time) => { lenis.raf(time * 1000) });
    gsap.ticker.lagSmoothing(0);

    /* SETUP ANIMAZIONI DESKTOP (GSAP MATCHMEDIA) */

    let mm = gsap.matchMedia();

    mm.add("(min-width: 1025px)", () => {
        
        // Imposta lo stato architetturale iniziale per creare l'illusione ottica di una singola immagine contigua.
        gsap.set(".card-container-values", { 
            width: "70vw", 
            scale: 1.2,     
            gap: "0px",
            y: 0 
        });
        
        gsap.set(".val-card", { 
            borderRadius: "0px",
            marginLeft: "-1px", 
            marginRight: "-1px" 
        });

        /* TIMELINE SEQUENZIALE SCROLLTRIGGER */

        let tl = gsap.timeline({
            scrollTrigger: {
                trigger: ".sticky-section-values",
                pin: true,        
                scrub: 1,         
                start: "top top", 
                end: "+=350%",
            }
        });

        // Fase 1: Compressione struttura e reveal dell'header. L'operatore posizionale "<" impone l'esecuzione parallela dell'animazione testuale rispetto alla tween di traslazione in corso sulla timeline.
        tl.to(".card-container-values", { 
            scale: 1, 
            y: 60, 
            duration: 2 
        })
        .to(".sticky-header-values", { 
            y: 0, 
            opacity: 1, 
            duration: 2 
        }, "<") 

        // Fase 2: Transizione da immagine singola a partizionamento tridimensionale (Split Strutturale)
        .to(".card-container-values", { 
            width: "80vw", 
            gap: "30px",   
            duration: 2 
        })
        .to(".val-card", { 
            borderRadius: "20px", 
            marginLeft: "0px",
            marginRight: "0px",
            duration: 2 
        }, "<") 

        // Fase 3: Esecuzione trasformazioni 3D (Flip sull'asse Y) e distorsione prospettica a ventaglio tramite interpolazioni sfalsate sull'asse Z.
        .to(".val-card", { rotationY: 180, stagger: 0.2, duration: 2 }) 
        .to("#val-card-1", { y: 30, rotationZ: -6, duration: 2 }, "<")
        .to("#val-card-3", { y: 30, rotationZ: 6, duration: 2 }, "<");
    });

    /* RESET MOBILE/TABLET */

    // Rimozione procedurale totale delle inline-properties iniettate dal CSS Object Model di GSAP su breakpoint inferiori a 1024px, per ripristinare in modo incondizionato il flusso di layout CSS nativo della viewport.
    mm.add("(max-width: 1024px)", () => {
        gsap.set([".card-container-values", ".sticky-header-values", ".val-card", "#val-card-1", "#val-card-3"], { clearProps: "all" });
    });
});