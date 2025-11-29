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
        System.out.println("AuthFilter: Request URI: " + requestURI);
        
        // Rutas públicas que no requieren autenticación
        if (isPublicRoute(requestURI)) {
            System.out.println("AuthFilter: Public route, skipping authentication.");
            chain.doFilter(request, response);
            return;
        }
        
        // Verificar si el usuario está logueado
        Usuario usuario = null;
        if (session != null) {
            usuario = (Usuario) session.getAttribute("usuario");
        }
        
        if (usuario != null) {
            System.out.println("AuthFilter: User logged in: " + usuario.getCorreo() + ", Role: " + usuario.getRol());
        } else {
            System.out.println("AuthFilter: User not logged in.");
        }

        // Si no hay usuario logueado, redirigir a login
        if (usuario == null) {
            httpResponse.sendRedirect("/login");
            return;
        }
        
        // Verificar permisos según el rol y la ruta
        String rol = usuario.getRol().toString();
        String metodo = httpRequest.getMethod();
        
        boolean hasPermission = tienePermiso(requestURI, rol, metodo);
        System.out.println("AuthFilter: URI: " + requestURI + ", Role: " + rol + ", Has Permission: " + hasPermission);

        if (!hasPermission) {
            httpResponse.sendRedirect("/acceso-denegado");
            return;
        }
                
        chain.doFilter(request, response);
    }
    
    private boolean isPublicRoute(String uri) {
        return uri.equals("/") || 
               uri.equals("/login") || 
               uri.equals("/registro") ||
               uri.equals("/acceso-denegado") ||
               uri.equals("/en-construccion") ||
               uri.startsWith("/css") || 
               uri.startsWith("/js") || 
               uri.startsWith("/images") ||
               uri.startsWith("/static") ||
               uri.startsWith("/uploads") ||  // Archivos subidos (públicos para evaluadores y visitantes)
               uri.startsWith("/eventos") ||  // Eventos públicos para todos
               uri.startsWith("/cronograma/ver");  // Cronograma público para todos
    }
    
    private boolean tienePermiso(String uri, String rol, String metodo) {
        // ADMIN tiene acceso a todo
        if ("ADMINISTRADOR".equals(rol)) {
            return uri.startsWith("/admin") ||
                   uri.startsWith("/eventos") ||
                   uri.startsWith("/usuarios") ||
                   uri.startsWith("/api") ||
                   true;  // Acceso total para el admin
        }
        
        // ORGANIZADOR
        if ("ORGANIZADOR".equals(rol)) {
            return uri.startsWith("/organizador") ||
                   uri.startsWith("/eventos") ||
                   uri.startsWith("/stands") ||
                   uri.startsWith("/cronograma") ||  // Gestionar cronograma
                   uri.startsWith("/cronogramas") ||
                   (uri.startsWith("/solicitudes") && 
                    !uri.contains("/mis-solicitudes") && 
                    !uri.contains("/nueva") &&
                    !uri.matches(".*/(\\w+)/cancelar$"));
        }
        
        // EXPOSITOR - Puede ver eventos activos y solicitar stands
        if ("EXPOSITOR".equals(rol)) {
            return uri.startsWith("/expositor") ||
                   uri.startsWith("/eventos") ||  // Puede ver eventos (ya es público)
                   uri.startsWith("/cronograma/ver") ||  // Puede ver cronograma público
                   uri.startsWith("/solicitudes/nueva") ||  // Ver formulario de solicitud
                   uri.startsWith("/solicitudes/mis-solicitudes") ||  // Ver sus solicitudes
                   uri.startsWith("/solicitudes/guardar") ||  // Guardar solicitud
                   uri.startsWith("/material-comercial") ||  // Gestionar su material comercial
                   uri.matches("^/solicitudes/[^/]+/cancelar$");  // Cancelar sus solicitudes
        }
        
        // EVALUADOR
        if ("EVALUADOR".equals(rol)) {
            return uri.startsWith("/evaluador") ||
                   uri.startsWith("/eventos") ||  // Puede ver eventos (ya es público)
                   uri.startsWith("/cronograma/ver") ||  // Puede ver cronograma público
                   uri.startsWith("/material-comercial/ver/") ||  // Ver material comercial de stands
                   uri.startsWith("/evaluaciones") ||
                   uri.startsWith("/evaluador/reporte") ||
                   uri.startsWith("/reportes/evaluaciones");
        }
        
        // VISITANTE
        if ("VISITANTE".equals(rol)) {
            return uri.startsWith("/visitante") ||
                   uri.startsWith("/eventos") ||  // Puede ver eventos (ya es público)
                   uri.startsWith("/cronograma/ver") ||  // Puede ver cronograma público
                   uri.startsWith("/material-comercial/ver/") ||  // Ver material comercial de stands
                   uri.startsWith("/participaciones");
        }
        
        return false;
    }
}
