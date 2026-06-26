<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Recupera l'ID dell'ordine passato tramite <jsp:param>
    String idOrdine = request.getParameter("idOrdine");
    boolean isDisponibile = (idOrdine != null && !idOrdine.trim().isEmpty());
%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fattura.css">

<div class="btn-fattura-wrapper">
    <% if (isDisponibile) { %>
        <button onclick="stampaFatturaNascosta('<%= idOrdine %>', '${pageContext.request.contextPath}')" class="btn-cta btn-fattura-scarica">
            <svg class="btn-fattura-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                <polyline points="7 10 12 15 17 10"></polyline>
                <line x1="12" y1="15" x2="12" y2="3"></line>
            </svg>
            Scarica Fattura
        </button>
    <% } else { %>
        <button disabled class="btn-fattura-disabled">
            Fattura non disponibile
        </button>
    <% } %>
</div>

<iframe id="iframeFattura" class="iframe-fattura-hidden"></iframe>

<script src="${pageContext.request.contextPath}/js/fattura.js"></script>