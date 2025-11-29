package com.expogest.expogest.controlador;

import com.expogest.expogest.servicios.ReporteService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/evaluaciones/{eventoId}/excel")
    public ResponseEntity<InputStreamResource> generarReporteExcel(@PathVariable String eventoId) {
        try {
            ByteArrayInputStream in = reporteService.generarReporteEvaluacionesExcel(eventoId);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=reporte_evaluaciones.xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(in));
        } catch (Exception e) {
            // Manejo de errores (e.g., loggear y devolver un error HTTP apropiado)
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/evaluaciones/{eventoId}/pdf")
    public ResponseEntity<InputStreamResource> generarReportePdf(@PathVariable String eventoId) {
        try {
            ByteArrayInputStream in = reporteService.generarReporteEvaluacionesPdf(eventoId);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=reporte_evaluaciones.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(in));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
