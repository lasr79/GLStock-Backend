package com.example.glstock.controller;

import com.example.glstock.model.Categoria;
import com.example.glstock.model.Producto;
import com.example.glstock.service.CategoriaService;
import com.example.glstock.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    //Buscar un producto por id
    @GetMapping("/buscar-id/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    //Buscar todos los productos
    @GetMapping("/todos")
    public ResponseEntity<List<Producto>> obtenerTodosLosProductos() {
        return ResponseEntity.ok(productoService.obtenerTodosLosProductos());
    }
    //Buscar por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre, Authentication authentication) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }
    //Buscar por id de la categoria
    @PostMapping("/categoria")
    public ResponseEntity<List<Producto>> buscarPorCategoria(@RequestBody Map<String, Long> datos) {
        Long idCategoria = datos.get("idCategoria");
        Optional<Categoria> categoria = categoriaService.buscarPorId(idCategoria);
        return categoria.map(c -> ResponseEntity.ok(productoService.buscarPorCategoria(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    //Buscar los 5 productos con menor cantidad (stock)
    @GetMapping("/stock-menor")
    public ResponseEntity<List<Producto>> obtenerProductosConMenorStock() {
        return ResponseEntity.ok(productoService.productosConMenorStock());
    }
    //Buscar los 5 productos agregados recientemente por fecha de ingreso
    @GetMapping("/recientes")
    public ResponseEntity<List<Producto>> productosRecientes() {
        return ResponseEntity.ok(productoService.productosRecientes());
    }
    //Buscar por el nombre de la categoria
    @GetMapping("/buscar-categoria")
    public ResponseEntity<List<Producto>> productosPorCategoria(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombreCategoria(nombre));
    }
    //Actualizar el producto
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        Producto actualizado = productoService.actualizar(id, producto);
        return ResponseEntity.ok(actualizado);
    }
    //Eliminar el producto
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    //Crear producto
    @PostMapping("/crear")
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto creado = productoService.crearNuevoProducto(producto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }
}
