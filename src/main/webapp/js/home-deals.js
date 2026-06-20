document.addEventListener("DOMContentLoaded", () => {
    const stickySection = document.querySelector('.sticky-section-values');
    if (!stickySection) return;

    // 1. Inizializzazione GSAP e Lenis
    gsap.registerPlugin(ScrollTrigger);

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

    // 2. Creazione dell'animazione
    let mm = gsap.matchMedia();

    mm.add("(min-width: 1025px)", () => {
        
        // 3. SETUP: Immagine gigante unita
        gsap.set(".card-container-values", { 
            width: "70vw", 
            scale: 1.2,     
            gap: "0px",
            y: 0 // Posizione verticale di partenza (alta)
        });
        
        gsap.set(".val-card", { 
            borderRadius: "0px",
            marginLeft: "-1px", 
            marginRight: "-1px" 
        });

        // 4. TIMELINE SEQUENZIALE
        let tl = gsap.timeline({
            scrollTrigger: {
                trigger: ".sticky-section-values",
                pin: true,        
                scrub: 1,         
                start: "top top", 
                end: "+=350%",
            }
        });

        // ==========================================
        // FASE 1: Rimpicciolisce, scende e appare il titolo
        // ==========================================
        tl.to(".card-container-values", { 
              scale: 1, 
              y: 60, // Trasla verso il basso di 60px per far respirare il titolo
              duration: 2 
          })
          .to(".sticky-header-values", { 
              y: 0, 
              opacity: 1, 
              duration: 2 
          }, "<") // Il "<" fa apparire la scritta ESATTAMENTE mentre il blocco scende

        // ==========================================
        // FASE 2: Lo split delle carte
        // (Parte in automatico SOLO DOPO che la Fase 1 è conclusa)
        // ==========================================
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

        // ==========================================
        // FASE 3: Flip e ventaglio
        // ==========================================
          .to(".val-card", { rotationY: 180, stagger: 0.2, duration: 2 }) 
          .to("#val-card-1", { y: 30, rotationZ: -6, duration: 2 }, "<")
          .to("#val-card-3", { y: 30, rotationZ: 6, duration: 2 }, "<");
    });

    // Reset totale per il Mobile e Tablet
    mm.add("(max-width: 1024px)", () => {
        gsap.set([".card-container-values", ".sticky-header-values", ".val-card", "#val-card-1", "#val-card-3"], { clearProps: "all" });
    });
});