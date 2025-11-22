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

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/evaluador")
public class EvaluadorMaterialController {

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

    // Ver stands disponibles para evaluar en un evento
    @GetMapping("/stands-evaluar")
    public String standsParaEvaluar(@RequestParam(required = false) String eventoId, 
                                     Model model, HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        if (!"EVALUADOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "Acceso denegado: Solo evaluadores");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/acceso-denegado";
        }
        
        List<Evento> eventosActivos = eventoRepository.findByEstado(Evento.EstadoEvento.ACTIVO);
        model.addAttribute("eventos", eventosActivos);
        
        if (eventoId != null && !eventoId.isEmpty()) {
            Optional<Evento> evento = eventoRepository.findById(eventoId);
            if (evento.isPresent()) {
                List<Stand> stands = standRepository.findByEventoId(eventoId);
                
                // Solo stands ocupados (con expositor asignado)
                List<Stand> standsOcupados = stands.stream()
                    .filter(s -> "OCUPADO".equals(s.getEstado()))
                    .toList();
                
                model.addAttribute("evento", evento.get());
                model.addAttribute("stands", standsOcupados);
            }
        }
        
        return "evaluador/standsEvaluar";
    }

    // Ver material comercial de un stand
    @GetMapping("/material-comercial/{standId}")
    public String verMaterialComercial(@PathVariable String standId, 
                                        Model model, HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        if (!"EVALUADOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "Acceso denegado: Solo evaluadores");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/acceso-denegado";
        }
        
        Optional<Stand> stand = standRepository.findById(standId);
        if (stand.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "Stand no encontrado");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/evaluador/stands-evaluar";
        }
        
        // Buscar el expositor del stand
        List<SolicitudStand> solicitudes = solicitudRepository.findByStandId(standId);
        Usuario expositor = null;
        
        for (SolicitudStand sol : solicitudes) {
            if (sol.getEstado() == SolicitudStand.EstadoSolicitud.APROBADA) {
                expositor = usuarioRepository.findById(sol.getExpositorId()).orElse(null);
                break;
            }
        }
        
        Optional<MaterialComercial> material = materialService.obtenerPorStand(standId);
        Optional<Evento> evento = eventoRepository.findById(stand.get().getEventoId());
        
        model.addAttribute("stand", stand.get());
        model.addAttribute("expositor", expositor);
        model.addAttribute("material", material.orElse(null));
        model.addAttribute("evento", evento.orElse(null));
        
        return "evaluador/materialComercial";
    }
}
