package com.expogest.expogest.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EvaluadorController {

    @GetMapping("/evaluador/panelEvaluador")
    public String panelEvaluador() {
        return "evaluador/panelEvaluador";
    }
}