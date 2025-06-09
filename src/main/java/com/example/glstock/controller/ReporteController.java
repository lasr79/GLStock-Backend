package com.example.glstock.controller;
import com.example.glstock.model.Movimiento;
import com.example.glstock.model.Producto;
import com.example.glstock.repository.ProductoRepository;
import com.example.glstock.service.MovimientoService;
import com.example.glstock.service.ProductoService;
import com.example.glstock.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {
    private final MovimientoService movimientoService;
    private final ProductoService productoService;
    private final ReporteService reporteService;
    private final ProductoRepository productoRepository;

    //Reporte de los 5 productos con menor cantidad (stock)
    @GetMapping("/productos/bajo-stock")
    public ResponseEntity<byte[]> generarReporteBajoStock() throws IOException {
        List<Producto> productos = productoService.productosConMenorStock(); // Por ejemplo: stock ≤ 5

        byte[] pdf = reporteService.generarReporteBajoStock(productos);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bajo_stock.pdf")
                .body(pdf);
    }
    //Reporte de todos los productos
    @GetMapping("/productos/todos")
    public ResponseEntity<byte[]> generarReporteTodosProductos() throws IOException {
        List<Producto> productos = productoRepository.findAll(); // directo al repo
        byte[] pdf = reporteService.generarReporteTodos(productos);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos_todos.pdf")
                .body(pdf);
    }
    //Reporte por nombre de categoria
    @GetMapping("/productos/por-categoria")
    public ResponseEntity<byte[]> generarReportePorCategoria(@RequestParam String categoria) throws IOException {
        List<Producto> productos = productoService.buscarPorNombreCategoria(categoria);
        byte[] pdf = reporteService.generarReportePorCategoria(productos, categoria);

        String nombreArchivo = "productos_categoria_" + categoria + ".pdf";
        String encodedFileName = URLEncoder.encode(nombreArchivo, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .body(pdf);
    }
    //Reporte de los ultimos 5 productos agregador por fecha ingreso
    @GetMapping("/productos/recientes")
    public ResponseEntity<byte[]> generarReporteProductosRecientes() throws IOException {
        List<Producto> productos = productoService.productosRecientes();
        byte[] pdf = reporteService.generarReporteRecientes(productos);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos_recientes.pdf")
                .body(pdf);
    }

    // Reporte: Movimientos entre fechas
    @GetMapping("/movimientos/por-fechas")
    public ResponseEntity<byte[]> generarReporteMovimientosPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime fechaDesde = LocalDateTime.parse(desde, formatter);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta, formatter);
        String titulo_pdf=fechaDesde.format(formatter2)+" a "+fechaHasta.format(formatter2)+".pdf";
        List<Movimiento> movimientos = movimientoService.movimientosEntreFechas(fechaDesde, fechaHasta);

        byte[] pdf = reporteService.generarReporteMovimientosPorFechas(movimientos, fechaDesde, fechaHasta);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=movimientos_" +titulo_pdf)
                .body(pdf);
    }

    // Reporte: Últimos 10 movimientos
    @GetMapping("/movimientos/recientes")
    public ResponseEntity<byte[]> generarReporteMovimientosRecientes() throws IOException {
        List<Movimiento> movimientos = movimientoService.ultimos10Movimientos();
        byte[] pdf = reporteService.generarReporteMovimientosRecientes(movimientos);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movimientos_recientes.pdf")
                .body(pdf);
    }
}


