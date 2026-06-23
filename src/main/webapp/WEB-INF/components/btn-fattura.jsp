<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Recupera l'ID dell'ordine passato tramite <jsp:param>
    String idOrdine = request.getParameter("idOrdine");
    boolean isDisponibile = (idOrdine != null && !idOrdine.trim().isEmpty());
%>

<div class="bottone-fattura-wrapper" style="margin-top: 1.5rem;">
    <% if (isDisponibile) { %>
        <button onclick="stampaFatturaNascosta('<%= idOrdine %>')" class="btn" style="background-color: var(--antracite-scuro, #2c2c2c); color: var(--panna-carta, #fdfbf7); padding: 12px 24px; display: inline-block; font-weight: bold; text-transform: uppercase; border: 2px solid var(--antracite-scuro, #2c2c2c); transition: all 0.3s; cursor: pointer;">
            <svg style="width: 18px; height: 18px; vertical-align: middle; margin-right: 8px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                <polyline points="7 10 12 15 17 10"></polyline>
                <line x1="12" y1="15" x2="12" y2="3"></line>
            </svg>
            Scarica Fattura
        </button>
    <% } else { %>
        <button disabled style="background-color: transparent; color: var(--grigio-taupe, #8b8680); cursor: not-allowed; padding: 12px 24px; font-weight: bold; text-transform: uppercase; border: 2px dashed var(--grigio-taupe, #8b8680);">
            Fattura non disponibile
        </button>
    <% } %>
</div>