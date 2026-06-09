package reframe.utils;

import java.io.IOException;
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

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        Utente utenteInSessione = (Utente) httpServletRequest.getSession().getAttribute("utente");
        
        String path = httpServletRequest.getServletPath();

        boolean isAutenticato = (utenteInSessione != null);

        boolean isAdmin = false;
        if (isAutenticato) {
            String email = utenteInSessione.getEmail();
            
            // Credenziale del DB da verificare
            if (email.equals("alfredo@reframe.it") || email.equals("erika@reframe.it")) {
                isAdmin = true;
            }
        }

        if (path.contains("/common/") && !isAutenticato) {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login.jsp");
            return;
        } 
        else if (path.contains("/admin/") && !isAdmin) {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/accessoNegato.jsp");
            return; 
        }

        chain.doFilter(request, response);
    }
}