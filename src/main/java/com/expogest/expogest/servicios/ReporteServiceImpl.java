package com.expogest.expogest.servicios;

import com.expogest.expogest.entidades.Evaluacion;
import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.repository.EvaluacionRepository;
import com.expogest.expogest.repository.EventoRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import com.expogest.expogest.entidades.Stand;
import com.expogest.expogest.entidades.Usuario;
import com.expogest.expogest.repository.StandRepository;
import com.expogest.expogest.repository.UsuarioRepository;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final EventoRepository eventoRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final StandRepository standRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteServiceImpl(EventoRepository eventoRepository, EvaluacionRepository evaluacionRepository, StandRepository standRepository, UsuarioRepository usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.standRepository = standRepository;
		this.usuarioRepository = usuarioRepository;
        }


    @Override
    public ByteArrayInputStream generarReporteEvaluacionesPdf(String eventoId) throws Exception {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new Exception("Evento no encontrado."));

        if (evento.getFechaFin().isAfter(LocalDate.now())) {
            throw new Exception("El evento aún no ha finalizado. No se puede generar el reporte.");
        }

        List<Evaluacion> evaluaciones = evaluacionRepository.findByEventoId(eventoId);
        double promedio = evaluaciones.stream().mapToDouble(Evaluacion::getNota).average().orElse(0.0);

        Map<String, String> standNombres = standRepository.findAllById(evaluaciones.stream().map(Evaluacion::getStandId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Stand::getId, Stand::getNombre));

        Map<String, String> evaluadorNombres = usuarioRepository.findAllById(evaluaciones.stream().map(Evaluacion::getEvaluadorId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Usuario::getId, Usuario::getNombre));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Reporte de Evaluaciones del Evento: " + evento.getNombre()).setBold().setFontSize(16));
        document.add(new Paragraph("Promedio General de Notas: " + String.format("%.2f", promedio)).setFontSize(12));
        document.add(new Paragraph("\n"));

        Table table = new Table(4);
        table.addHeaderCell("Stand");
        table.addHeaderCell("Evaluador");
        table.addHeaderCell("Nota");
        table.addHeaderCell("Comentario");

        for (Evaluacion e : evaluaciones) {
            table.addCell(standNombres.getOrDefault(e.getStandId(), "N/A"));
            table.addCell(evaluadorNombres.getOrDefault(e.getEvaluadorId(), "N/A"));
            table.addCell(String.valueOf(e.getNota()));
            table.addCell(e.getComentario() != null ? e.getComentario() : "");
        }

        document.add(table);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ByteArrayInputStream generarReporteEvaluacionesExcel(String eventoId) throws Exception {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new Exception("Evento no encontrado."));

        if (evento.getFechaFin().isAfter(LocalDateTime.now().toLocalDate())) {
            throw new Exception("El evento aún no ha finalizado. No se puede generar el reporte.");
        }

        List<Evaluacion> evaluaciones = evaluacionRepository.findByEventoId(eventoId);
        double promedio = evaluaciones.stream().mapToDouble(Evaluacion::getNota).average().orElse(0.0);
        
        Map<String, String> standNombres = standRepository.findAllById(evaluaciones.stream().map(Evaluacion::getStandId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Stand::getId, Stand::getNombre));

        Map<String, String> evaluadorNombres = usuarioRepository.findAllById(evaluaciones.stream().map(Evaluacion::getEvaluadorId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Usuario::getId, Usuario::getNombre));

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte de Evaluaciones");

            Row headerRow = sheet.createRow(0);
            Cell cell = headerRow.createCell(0);
            cell.setCellValue("Reporte de Evaluaciones del Evento: " + evento.getNombre());

            Row subHeaderRow = sheet.createRow(1);
            cell = subHeaderRow.createCell(0);
            cell.setCellValue("Promedio General de Notas: " + String.format("%.2f", promedio));

            String[] columns = {"Stand", "Evaluador", "Nota", "Comentario"};
            Row tableHeaderRow = sheet.createRow(3);
            for (int i = 0; i < columns.length; i++) {
                Cell c = tableHeaderRow.createCell(i);
                c.setCellValue(columns[i]);
            }

            int rowNum = 4;
            for (Evaluacion e : evaluaciones) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(standNombres.getOrDefault(e.getStandId(), "N/A"));
                row.createCell(1).setCellValue(evaluadorNombres.getOrDefault(e.getEvaluadorId(), "N/A"));
                row.createCell(2).setCellValue(e.getNota());
                row.createCell(3).setCellValue(e.getComentario() != null ? e.getComentario() : "");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo Excel: " + e.getMessage());
        }
    }
}