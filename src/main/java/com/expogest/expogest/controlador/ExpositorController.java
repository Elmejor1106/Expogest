package com.expogest.expogest.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExpositorController {

    @GetMapping("/expositor/panelExpositor")
    public String panelExpositor() {
        return "expositor/panelExpositor";
    }

    @GetMapping("/expositor/solicitudStand")
    public String solicitudStand() {
        return "expositor/solicitudStand";
    }
}