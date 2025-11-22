package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.*;
import com.expogest.expogest.repository.*;
import com.expogest.expogest.servicios.MaterialComercialService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/visitante")
public class VisitanteMaterialController {

    @Autowired
    private MaterialComercialService materialService;
    
    @Autowired
    private StandRepository standRepository;
    
    @Autowired
    private EventoRepository eventoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private SolicitudStandRepository solicitudRepository;

    // Ver lista de material comercial por evento
    @GetMapping("/material-comercial-lista")
    public String listarMaterialComercial(@RequestParam(required = false) String eventoId, 
                                          Model model, HttpSession session) {
        
        List<Evento> eventosActivos = eventoRepository.findByEstado(Evento.EstadoEvento.ACTIVO);
        model.addAttribute("eventos", eventosActivos);
        
        if (eventoId != null && !eventoId.isEmpty()) {
            Optional<Evento> evento = eventoRepository.findById(eventoId);
            if (evento.isPresent()) {
                List<Stand> stands = standRepository.findByEventoId(eventoId);
                
                // Crear lista de datos combinados
                List<MaterialComercialItem> items = new ArrayList<>();
                
                for (Stand stand : stands) {
                    if ("OCUPADO".equals(stand.getEstado())) {
                        // Buscar material del stand
                        Optional<MaterialComercial> material = materialService.obtenerPorStand(stand.getId());
                        
                        // Buscar expositor del stand
                        List<SolicitudStand> solicitudes = solicitudRepository.findByStandId(stand.getId());
                        Usuario expositor = null;
                        
                        for (SolicitudStand sol : solicitudes) {
                            if (sol.getEstado() == SolicitudStand.EstadoSolicitud.APROBADA) {
                                expositor = usuarioRepository.findById(sol.getExpositorId()).orElse(null);
                                break;
                            }
                        }
                        
                        items.add(new MaterialComercialItem(
                            stand, 
                            material.orElse(null), 
                            expositor
                        ));
                    }
                }
                
                model.addAttribute("evento", evento.get());
                model.addAttribute("materiales", items);
            }
        }
        
        return "visitante/materialComercialLista";
    }
    
    // Clase auxiliar para transportar datos
    public static class MaterialComercialItem {
        private Stand stand;
        private MaterialComercial material;
        private Usuario expositor;
        
        public MaterialComercialItem(Stand stand, MaterialComercial material, Usuario expositor) {
            this.stand = stand;
            this.material = material;
            this.expositor = expositor;
        }
        
        public Stand getStand() { return stand; }
        public MaterialComercial getMaterial() { return material; }
        public Usuario getExpositor() { return expositor; }
    }
}
