package com.expogest.expogest.config;

import com.expogest.expogest.entidades.Usuario;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        
        // Rutas públicas que no requieren autenticación
        if (isPublicRoute(requestURI)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Verificar si el usuario está logueado
        Usuario usuario = null;
        if (session != null) {
            usuario = (Usuario) session.getAttribute("usuario");
        }
        
        // Si no hay usuario logueado, redirigir a login
        if (usuario == null) {
            httpResponse.sendRedirect("/login");
            return;
        }
        
        // Verificar permisos según el rol y la ruta
        String rol = usuario.getRol().toString();
        
        if (!tienePermiso(requestURI, rol)) {
            httpResponse.sendRedirect("/acceso-denegado");
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    private boolean isPublicRoute(String uri) {
        return uri.equals("/") || 
               uri.equals("/login") || 
               uri.equals("/registro") ||
               uri.equals("/en-construccion") ||
               uri.startsWith("/css") || 
               uri.startsWith("/js") || 
               uri.startsWith("/images") ||
               uri.startsWith("/static");
    }
    
    private boolean tienePermiso(String uri, String rol) {
        // ADMIN tiene acceso a todo
        if ("ADMINISTRADOR".equals(rol)) {
            return true;
        }
        
        // ORGANIZADOR
        if ("ORGANIZADOR".equals(rol)) {
            return uri.startsWith("/organizador") ||
                   uri.startsWith("/eventos") ||
                   uri.startsWith("/stands") ||
                   uri.startsWith("/cronogramas") ||
                   (uri.startsWith("/solicitudes") && 
                    !uri.contains("/mis-solicitudes") && 
                    !uri.contains("/nueva") &&
                    !uri.matches(".*/(\\w+)/cancelar$"));
        }
        
        // EXPOSITOR
        if ("EXPOSITOR".equals(rol)) {
            return uri.startsWith("/expositor") ||
                   uri.startsWith("/solicitudes/nueva") ||
                   uri.startsWith("/solicitudes/mis-solicitudes") ||
                   uri.startsWith("/solicitudes/guardar") ||
                   uri.matches("^/solicitudes/[^/]+/cancelar$");
        }
        
        // EVALUADOR
        if ("EVALUADOR".equals(rol)) {
            return uri.startsWith("/evaluador") ||
                   uri.startsWith("/evaluaciones");
        }
        
        // VISITANTE
        if ("VISITANTE".equals(rol)) {
            return uri.startsWith("/visitante") ||
                   uri.startsWith("/participaciones");
        }
        
        return false;
    }
}
