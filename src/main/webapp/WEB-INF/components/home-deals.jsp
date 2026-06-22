<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<section class="sticky-section-values">
    <div class="sticky-header-values">
        <h2>LA NOSTRA VETRINA</h2>
        <h3 class="mobile-only-title">Clicca per scoprire di più</h3>
    </div>

<div class="card-container-values">
        <div class="val-card" id="val-card-1">
    <div class="val-card-front">
        <picture>
            <source media="(max-width: 1024px)" srcset="${pageContext.request.contextPath}/assets/mobile-slice1.jpg">
            <img src="${pageContext.request.contextPath}/assets/slice1.png" alt="Fetta 1">
        </picture>
    </div>
    
    <div class="val-card-back">
        <i class="ph ph-arrows-clockwise card-icon"></i>
        <span><a href="${pageContext.request.contextPath}/ProdottoServlet?tipo=Usato">SCOPRI I NOSTRI ARTICOLI RICONDIZIONATI</a></span>
    </div>
</div>

<div class="val-card" id="val-card-2">
    <div class="val-card-front">
        <picture>
            <source media="(max-width: 1024px)" srcset="${pageContext.request.contextPath}/assets/mobile-slice2.jpg">
            <img src="${pageContext.request.contextPath}/assets/slice2.png" alt="Fetta 2">
        </picture>
    </div>
    <div class="val-card-back">
        <i class="ph ph-sparkle card-icon"></i>
        <span><a href="${pageContext.request.contextPath}/ProdottoServlet?tipo=Nuovo">ESPLORA LE NUOVE TECNOLOGIE</a></span>
    </div>
</div>

<div class="val-card" id="val-card-3">
    <div class="val-card-front">
        <picture>
            <source media="(max-width: 1024px)" srcset="${pageContext.request.contextPath}/assets/mobile-slice3.jpg">
            <img src="${pageContext.request.contextPath}/assets/slice3.png" alt="Fetta 3">
        </picture>
    </div>
    <div class="val-card-back">
        <i class="ph ph-film-strip card-icon"></i>
        <span><a href="${pageContext.request.contextPath}/ProdottoServlet?tipo=Collezione">VIAGGIA NEL TEMPO CON LA NOSTRA LINEA COLLEZIONISMO</a></span>
    </div>
</div>
</div>
</section>