package reframe.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import reframe.model.beans.Utente;

@WebFilter(filterName = "AccessControlFilter", urlPatterns = "/*")
public class AccessControlFilter extends HttpFilter implements Filter {
    
    private static final long serialVersionUID = 1L;

    private static final List<String> COMMON_ROUTES = Arrays.asList(
        "/ProfiloServlet", 
        "/Carrello", 
        "/Checkout",
        "/ChatServlet",
        "/CreaPraticaServlet",
        "/DettaglioPraticaServlet",
        "/Fattura",
        "/ListaPraticheServlet",
        "/RecensioneServlet"
    );

    private static final List<String> ADMIN_ROUTES = Arrays.asList(
    	"/PannelloAdminServlet",
    	"/AggiornaPraticaServlet",
        "/ChatServlet",
        "/DettaglioPraticaServlet",
        "/InviaNotaServlet",
        "/ListaPraticheServlet",
        "/RecensioneServlet"
    );

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        Utente utenteInSessione = (Utente) httpServletRequest.getSession().getAttribute("utente");
        String path = httpServletRequest.getServletPath();

        boolean isAutenticato = (utenteInSessione != null);
        int adminLevel = 0;
        
        if (isAutenticato) {
            adminLevel = utenteInSessione.getIsAdmin();
        }

        // 3. Verifica se l'URL punta alla cartella fisica "/common/" OPPURE a una Servlet della lista
        boolean isCommonRoute = path.startsWith("/common/") || COMMON_ROUTES.contains(path);
        
        // 4. Verifica se l'URL punta alla cartella fisica "/admin/" OPPURE a una Servlet della lista
        boolean isAdminRoute = path.startsWith("/admin/") || ADMIN_ROUTES.contains(path);

        // --- REGOLE DI ACCESSO ---

        // Se un utente non loggato prova ad accedere ad aree o servlet private
        if ((isCommonRoute || isAdminRoute) && !isAutenticato) {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login.jsp");
            return;
        } 
        // Se un utente BASE (livello 0) prova ad accedere all'area o servlet Admin
        else if (isAdminRoute && !isCommonRoute && adminLevel == 0) {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/accessoNegato.jsp");
            return; 
        }
        
        // Se un ADMIN prova ad accedere esplicitamente alla pagina profilo dell'utente base
        else if (path.equals("/common/profilo.jsp") && adminLevel > 0) {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/admin/profiloAdmin.jsp");
            return;
        }
        
        //  Se un ADMIN prova ad accedere a una rotta Utente ESCLUSIVA 
        else if (isCommonRoute && !isAdminRoute && adminLevel > 0) {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/PannelloAdminServlet");
            return; 
        }

        chain.doFilter(request, response);
    }
}