package com.example.glstock.service;

import com.example.glstock.model.Movimiento;
import com.example.glstock.model.Producto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteService {

    //Genera los pdf de los movimientos de forma generica para reutilizarla en otras consultas
    public byte[] generarReporteMovimientos(String titulo, List<Movimiento> movimientos) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        // Cabecera del Reporte
        PDPageContentStream content = new PDPageContentStream(document, page);
        content.setLeading(18f);
        content.setFont(PDType1Font.HELVETICA, 10);
        content.beginText();
        content.newLineAtOffset(50, 770);
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        // Divide el titulo por saltos de linea (\n)
        String[] partes = titulo.split("\n");
        for (String parte : partes) {
            content.showText(parte);
            content.newLine();
        }
        content.newLine();
        content.endText();
        content.close();
        // Inicializacion de la tabla
        int y = 730; // Posicion vertical inicial para la tabla
        int rowHeight = 20; // Altura de cada fila
        // Encabezados de las columnas
        String[] headers = {"Producto", "Categoría", "Stock", "Tipo", "Fecha", "Usuario"};
        // Ancho de cada columna
        int[] colWidths = {140, 90, 40, 60, 100, 130};
        int xMargin = 40;
        PDPageContentStream table = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
        table.setFont(PDType1Font.HELVETICA_BOLD, 9);
        int x = xMargin;

        //Cabecera de la tabla
        for (int i = 0; i < headers.length; i++) {
            table.beginText();
            table.newLineAtOffset(x, y);
            table.showText(headers[i]);
            table.endText();
            x += colWidths[i]; // Mueve a la siguiente columna
        }

        // Ajusta posicion vertical
        y -= rowHeight;
        table.setFont(PDType1Font.HELVETICA, 8);

        //Contenido de la tabla
        for (Movimiento mov : movimientos) {
            x = xMargin;

            // Si la posicion vertical es muy baja agrega una nueva pagina
            if (y < 100) {
                table.close();
                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                y = 750;
                table = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
                table.setFont(PDType1Font.HELVETICA, 8);
            }

            // Preparar los datos de una fila
            String correo = mov.getUsuario().getCorreo();
            String[] row = {
                    mov.getProducto().getNombre(),
                    mov.getProducto().getCategoria().getNombre(),
                    String.valueOf(mov.getCantidad()),
                    mov.getTipo().name(),
                    mov.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    correo
            };

            // Escribe cada celda de la fila
            for (int i = 0; i < row.length; i++) {
                table.beginText();
                table.newLineAtOffset(x, y);
                table.showText(row[i]);
                table.endText();
                x += colWidths[i];
            }

            y -= rowHeight;
        }


        table.close();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        document.close();

        // Retorna el PDF como un arreglo de bytes
        return out.toByteArray();
    }

    //Genera los pdf de los productos de forma generica para reutilizarla en otras consultas
    private byte[] generarReporteProductos(String titulo, List<Producto> productos) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream content = new PDPageContentStream(document, page);
        content.setLeading(20f);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 14);
        content.newLineAtOffset(50, 750);
        content.showText("Reporte: " + titulo);
        content.endText();
        content.close();
        // Posicion vertical inicial para los productos
        int yPosition = 700;

        // Itera sobre la lista de productos
        for (Producto producto : productos) {
            // Si no hay espacio suficiente crea una nueva pagina
            if (yPosition < 120) {
                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                yPosition = 750;
            }
            content = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
            // Inserta el producto (texto + imagen) y actualizar la posicion vertical
            yPosition = insertarProductoConImagen(document, content, producto, yPosition);
            // Cierra el stream despues de cada producto
            content.close();
        }
        // Guardar el contenido en un stream de bytes
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        document.close();
        // Retornar el PDF como array de bytes
        return out.toByteArray();
    }
    // Devuelve el producto y la nueva posicion vertical (yPosition) para seguir escribiendo debajo.
    private int insertarProductoConImagen(PDDocument document, PDPageContentStream content,
                                          Producto producto, int yPosition) throws IOException {
        // Inicia el bloque de texto para escribir el nombre y el stock del producto
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 12);
        content.newLineAtOffset(50, yPosition);
        content.showText("Nombre: " + producto.getNombre() + " | Stock: " + producto.getCantidad());
        content.endText();

        // Intenta cargar y dibujar la imagen del producto
        try {
            BufferedImage img = ImageIO.read(new URL(producto.getUrlImagen())); // Carga la imagen desde la URL
            if (img != null) {
                // Convierte la imagen en un objeto PDF y la dibuja en el documento
                PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, toByteArray(img), producto.getNombre());
                content.drawImage(pdImage, 50, yPosition - 60, 60, 60);
            }
        } catch (Exception e) {
            System.out.println("Error cargando imagen para producto: " + producto.getNombre());
        }
        // Devuelve la nueva posicion vertical para el siguiente producto
        return yPosition - 80;
    }
    // Convierte una imagen BufferedImage a un arreglo de bytes en formato PNG
    private byte[] toByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
    //Recibe los productos con bajo stock y los manda al generador de pdf con el titulo( del contenido del pdf) personalizado
    public byte[] generarReporteBajoStock(List<Producto> productos) throws IOException {
        return generarReporteProductos("Productos con Bajo Stock", productos);
    }
    //Recibe todos los productos y los manda al generador de pdf con el titulo( del contenido del pdf) personalizado
    public byte[] generarReporteTodos(List<Producto> productos) throws IOException {
        return generarReporteProductos("Todos los Productos", productos);
    }
    //Recibe los productos por la categoria indicada y los manda al generador de pdf con el titulo( del contenido del pdf) personalizado
    public byte[] generarReportePorCategoria(List<Producto> productos, String categoria) throws IOException {
        return generarReporteProductos("Productos de la categoría: " + categoria, productos);
    }
    //Recibe los productos agregados recientemente por fecha de ingreso  y los manda al generador de pdf con el titulo( del contenido del pdf) personalizado
    public byte[] generarReporteRecientes(List<Producto> productos) throws IOException {
        return generarReporteProductos("Productos Recientes", productos);
    }
    ////Recibe movimientos por rango de fechas y los manda al generador de pdf con el titulo( del contenido del pdf) personalizado
    public byte[] generarReporteMovimientosPorFechas(List<Movimiento> movimientos, LocalDateTime desde, LocalDateTime hasta) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String titulo = "Movimientos\nFechas: " +
                desde.format(formatter) + " - " +
                hasta.format(formatter);
        return generarReporteMovimientos(titulo, movimientos);
    }
    //Recibe los ultimos 10 movimientos agregados y los manda al generador de pdf con el titulo( del contenido del pdf) personalizado
    public byte[] generarReporteMovimientosRecientes(List<Movimiento> movimientos) throws IOException {
        String titulo = "Últimos movimientos registrados";
        return generarReporteMovimientos(titulo, movimientos);
    }
}