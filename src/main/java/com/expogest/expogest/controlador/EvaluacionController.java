package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Evaluacion;
import com.expogest.expogest.repository.EvaluacionRepository;
import com.expogest.expogest.repository.StandRepository;
import com.expogest.expogest.repository.UsuarioRepository;
import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.entidades.Stand;
import com.expogest.expogest.entidades.Usuario;
import com.expogest.expogest.repository.EventoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/evaluaciones")
public class EvaluacionController {

    @Autowired
    private EvaluacionRepository evaluacionRepository;
    @Autowired
    private StandRepository standRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EventoRepository eventoRepository;

    @GetMapping
    public String listarEvaluaciones(Model model) {
        List<Evaluacion> evaluaciones = evaluacionRepository.findAll();
        model.addAttribute("evaluaciones", evaluaciones);
        return "evaluador/evaluacion";
    }

    @GetMapping("/editar/{id:.+}")
    public String editarEvaluacion(@PathVariable String id, Model model) {
        model.addAttribute("evaluacion", evaluacionRepository.findById(id).orElse(null));
        model.addAttribute("stands", standRepository.findAll());
        model.addAttribute("evaluadores", usuarioRepository.findAll());
        return "evaluador/nuevaEvaluacion";
    }

    @GetMapping("/eliminar/{id:.+}")
    public String eliminarEvaluacion(@PathVariable String id) {
        evaluacionRepository.deleteById(id);
        return "redirect:/evaluaciones";
    }

    @GetMapping("/nueva/{eventoId:.+}/{standId:.+}")
    public String mostrarFormularioNueva(@PathVariable String eventoId, @PathVariable String standId, Model model, HttpSession session) {
        Usuario evaluador = (Usuario) session.getAttribute("usuario");
        if (evaluador == null) {
            return "redirect:/login";
        }

        Evento evento = eventoRepository.findById(eventoId).orElse(null);
        Stand stand = standRepository.findById(standId).orElse(null);

        if (evento == null || stand == null) {
            return "redirect:/evaluador/panel";
        }

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setEventoId(eventoId);
        evaluacion.setStandId(standId);
        evaluacion.setEvaluadorId(evaluador.getId());

        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("evento", evento);
        model.addAttribute("stand", stand);

        return "evaluador/nuevaEvaluacion";
    }

    @PostMapping("/guardar")
    public String guardarEvaluacion(@ModelAttribute Evaluacion evaluacion, HttpSession session) {
        Usuario evaluador = (Usuario) session.getAttribute("usuario");
        if (evaluador == null) {
            return "redirect:/login";
        }
        evaluacion.setEvaluadorId(evaluador.getId());
        evaluacion.setFechaHora(LocalDateTime.now());
        evaluacionRepository.save(evaluacion);
        return "redirect:/evaluador/eventos/" + evaluacion.getEventoId() + "/stands";
    }
}