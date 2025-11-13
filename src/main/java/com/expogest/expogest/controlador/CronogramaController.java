package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Cronograma;
import com.expogest.expogest.repository.CronogramaRepository;
import com.expogest.expogest.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cronograma")
public class CronogramaController {

    @Autowired
    private CronogramaRepository cronogramaRepository;
    @Autowired
    private EventoRepository eventoRepository;

    @GetMapping
    public String listarCronograma(Model model) {
        // Redirigir a página en construcción
        return "redirect:/en-construccion";
    }

    @GetMapping("/nuevo")
    public String nuevoCronograma(Model model) {
        // Redirigir a página en construcción
        return "redirect:/en-construccion";
    }

    @PostMapping("/guardar")
    public String guardarCronograma(@ModelAttribute Cronograma cronograma) {
        // Redirigir a página en construcción
        return "redirect:/en-construccion";
    }

    @GetMapping("/editar/{id}")
    public String editarCronograma(@PathVariable String id, Model model) {
        // Redirigir a página en construcción
        return "redirect:/en-construccion";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCronograma(@PathVariable String id) {
        // Redirigir a página en construcción
        return "redirect:/en-construccion";
    }
}