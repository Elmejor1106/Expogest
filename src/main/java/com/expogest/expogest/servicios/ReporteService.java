package com.expogest.expogest.servicios;

import com.expogest.expogest.entidades.Evento;

import java.io.ByteArrayInputStream;

public interface ReporteService {

    ByteArrayInputStream generarReporteEvaluacionesPdf(String eventoId) throws Exception;

    ByteArrayInputStream generarReporteEvaluacionesExcel(String eventoId) throws Exception;
}
