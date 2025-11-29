package com.expogest.expogest.controlador;

import com.expogest.expogest.repository.EvaluacionRepository;
import com.expogest.expogest.entidades.Evaluacion;
import com.expogest.expogest.entidades.Usuario;
import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.entidades.Stand;
import com.expogest.expogest.repository.EventoRepository;
import com.expogest.expogest.repository.StandRepository;
import com.expogest.expogest.repository.UsuarioRepository;
import com.expogest.expogest.servicios.ReporteServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.security.Principal;
import java.util.Optional;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

@Controller
@RequestMapping("/evaluador")
public class EvaluadorController {

    @GetMapping("/reporte/{eventoId}")
    public void descargarReporte(@PathVariable String eventoId, HttpSession session, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        Usuario user = (Usuario) session.getAttribute("usuario");
        System.out.println("User in session: " + (user != null ? user.getCorreo() : "null"));
        System.out.println("User role: " + (user != null ? user.getRol() : "null"));
        if (user == null || user.getRol() != Usuario.Rol.EVALUADOR) {
            try {
                response.sendRedirect("/acceso-denegado");
            } catch (Exception e) {
                // Ignorar
            }
            return;
        }
        try {
            byte[] datos = reporteService.generarReporteEvaluacionesPdf(eventoId).readAllBytes();
            String nombreArchivo = "reporte_" + eventoId + ".pdf";

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivo);
            IOUtils.copy(new ByteArrayInputStream(datos), response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            System.err.println("Error al generar o descargar el reporte: " + e.getMessage());
            try {
                response.sendRedirect("/acceso-denegado");
            } catch (Exception ex) {
                // Ignorar
            }
        }
    }

    @GetMapping("/evaluador/stands-calificados")
    public String verStandsCalificados(Model model, Principal principal) {
        String username = principal.getName();
        Optional<Usuario> evaluadorOpt = usuarioRepository.findByCorreo(username);
        if (evaluadorOpt.isEmpty()) {
            model.addAttribute("error", "No se encontró el usuario evaluador actual.");
            model.addAttribute("standsCalificados", List.of());
            return "evaluador/standsCalificados";
        }
        Usuario evaluador = evaluadorOpt.get();
        List<Evaluacion> evaluaciones = evaluacionRepository.findByEvaluadorId(evaluador.getId());
        List<Stand> stands = standRepository.findAllById(
            evaluaciones.stream().map(Evaluacion::getStandId).collect(Collectors.toList())
        );
        model.addAttribute("standsCalificados", stands);
        return "evaluador/standsCalificados";
    }

    private final EventoRepository eventoRepository;
    private final StandRepository standRepository;
    private final ReporteServiceImpl reporteService;
    private final EvaluacionRepository evaluacionRepository;
    private final UsuarioRepository usuarioRepository;

    public EvaluadorController(EventoRepository eventoRepository, StandRepository standRepository, EvaluacionRepository evaluacionRepository, UsuarioRepository usuarioRepository, ReporteServiceImpl reporteService) {
        this.eventoRepository = eventoRepository;
        this.standRepository = standRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.reporteService = reporteService;
    }
    @PostMapping("/eventos/{eventoId:.+}/stands/{standId:.+}/evaluar")
    public String evaluarStand(@PathVariable String eventoId,
                               @PathVariable String standId,
                               @RequestParam Double nota,
                               @RequestParam(required = false) String comentario,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Usuario user = (Usuario) session.getAttribute("usuario");
        if (user == null) {
            return "redirect:/login";
        }

        Evento evento = eventoRepository.findById(eventoId).orElse(null);
        if (evento == null || evento.getEstado() == Evento.EstadoEvento.FINALIZADO) {
            redirectAttributes.addFlashAttribute("error", "Solo los evaluadores pueden calificar stands.");
            return "redirect:/evaluador/eventos/" + eventoId + "/stands";
        }

        // Una evaluación por stand y evento
        boolean yaEvaluado = !evaluacionRepository.findByEvaluadorIdAndStandIdAndEventoId(user.getId(), standId, eventoId).isEmpty();
        if (yaEvaluado) {
            redirectAttributes.addFlashAttribute("error", "Ya has evaluado este stand en este evento.");
            return "redirect:/evaluador/eventos/" + eventoId + "/stands";
        }

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setEvaluadorId(user.getId());
        evaluacion.setStandId(standId);
        evaluacion.setEventoId(eventoId);
        evaluacion.setNota(nota);
        evaluacion.setComentario(comentario);
        evaluacion.setFechaHora(LocalDateTime.now());
        evaluacionRepository.save(evaluacion);

        redirectAttributes.addFlashAttribute("success", "Evaluación registrada correctamente.");
        return "redirect:/evaluador/eventos/" + eventoId + "/stands";
    }

    @GetMapping("/panel")
    public String panelEvaluador(Model model) {
        List<Evento> eventos = eventoRepository.findAll();
        model.addAttribute("eventos", eventos);
        return "evaluador/panelEvaluador";
    }

    @GetMapping("/eventos/{eventoId:.+}/stands")
    public String verStandsParaEvaluar(@PathVariable String eventoId, Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuario");
        if (user == null) {
            return "redirect:/login";
        }

        Evento evento = eventoRepository.findById(eventoId).orElse(null);
        if (evento == null) {
            return "redirect:/"; // O a una página de error
        }

        List<String> standIds = evento.getStandIds();
        List<Stand> stands = (standIds != null) ? standRepository.findAllById(standIds) : Collections.emptyList();

        List<Evaluacion> evaluaciones = evaluacionRepository.findByEvaluadorIdAndEventoId(user.getId(), eventoId);
        Map<String, Evaluacion> evaluacionesMap = evaluaciones.stream()
                .collect(Collectors.toMap(
                        Evaluacion::getStandId,
                        e -> e,
                        (e1, e2) -> e1.getFechaHora().isAfter(e2.getFechaHora()) ? e1 : e2
                ));

        model.addAttribute("evento", evento);
        model.addAttribute("stands", stands);
        model.addAttribute("evaluacionesMap", evaluacionesMap);

        return "evaluador/standsEvaluar";
    }
}