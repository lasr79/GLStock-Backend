package com.example.glstock.controller;

import com.example.glstock.model.Categoria;
import com.example.glstock.model.Producto;
import com.example.glstock.service.CategoriaService;
import com.example.glstock.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<Producto>> buscarPorCategoria(@PathVariable Long idCategoria) {
        Optional<Categoria> categoria = categoriaService.buscarPorId(idCategoria);
        return categoria.map(c -> ResponseEntity.ok(productoService.buscarPorCategoria(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/menor-stock")
    public ResponseEntity<List<Producto>> productosMenorStock() {
        return ResponseEntity.ok(productoService.productosMenorStock());
    }

    @GetMapping("/recientes")
    public ResponseEntity<List<Producto>> productosRecientes() {
        return ResponseEntity.ok(productoService.productosRecientes());
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.guardar(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto productoActualizado) {
        Optional<Producto> productoOpt = productoService.buscarPorId(id);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setNombre(productoActualizado.getNombre());
            producto.setDescripcion(productoActualizado.getDescripcion());
            producto.setCategoria(productoActualizado.getCategoria());
            producto.setPrecio(productoActualizado.getPrecio());
            producto.setCantidad(productoActualizado.getCantidad());
            producto.setFechaIngreso(productoActualizado.getFechaIngreso());
            producto.setUrlImagen(productoActualizado.getUrlImagen());
            return ResponseEntity.ok(productoService.guardar(producto));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
