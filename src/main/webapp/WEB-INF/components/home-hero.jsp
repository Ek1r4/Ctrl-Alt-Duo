<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<section class="hero-section">
    <div class="hero-overlay"></div>
    
    <div class="hero-container">
        <div class="hero-content">
            <h1 class="hero-title">
                SCOPRI LA TUA<br>PROSSIMA STORIA
            </h1>
            
            <p class="hero-subtitle">
                Esplora la nostra collezione curata di fotocamere<br>
                nuove, ricondizionate con precisione e rari articoli<br>
                da collezionismo.<br>
                ReFrame: la qualità ha una storia.
            </p>
            
            <div class="hero-buttons">
                <a href="${pageContext.request.contextPath}/ProdottoServlet?tipo=Usato" class="btn btn-cta">ESPLORA LE RICONDIZIONATE</a>
                <a href="${pageContext.request.contextPath}/ProdottoServlet?tipo=Nuovo" class="btn btn-cta">SCOPRI IL NUOVO</a>
            </div>
        </div>
        
    </div>
</section>