package com.example.glstock.service;

import com.example.glstock.model.Categoria;
import com.example.glstock.model.Producto;
import com.example.glstock.repository.CategoriaRepository;
import com.example.glstock.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> buscarPorCategoria(Categoria categoria) {
        return productoRepository.findByCategoria(categoria);
    }
    //Busqueda de productos menor a la cantidad que ponga el usuario
    public List<Producto> productosMenorStock(int limite) {
        return productoRepository.findByCantidadLessThan(limite);
    }

    //Busqueda de los 5 productos con menor estock
    public List<Producto> productosConMenorStock() {
        return productoRepository.findTop5ByOrderByCantidadAsc();
    }
    public List<Producto> productosRecientes() {
        return productoRepository.findTop5ByOrderByFechaIngresoDesc();
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
    public List<Producto> buscarPorNombreCategoria(String nombreCategoria) {
        return productoRepository.findByCategoriaNombreContainingIgnoreCase(nombreCategoria);
    }

    public Producto actualizar(Long id, Producto productoActualizado) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        // Actualiza los campos
        productoExistente.setNombre(productoActualizado.getNombre());
        productoExistente.setDescripcion(productoActualizado.getDescripcion());
        productoExistente.setCantidad(productoActualizado.getCantidad());
        productoExistente.setPrecio(productoActualizado.getPrecio());
        productoExistente.setFechaIngreso(productoActualizado.getFechaIngreso());
        productoExistente.setUrlImagen(productoActualizado.getUrlImagen());

        prepararProducto(productoActualizado, productoExistente);

        return productoRepository.save(productoExistente);
    }
    public Producto crearNuevoProducto(Producto producto) {
        prepararProducto(producto, producto);
        return productoRepository.save(producto);
    }



    private void prepararProducto(Producto origen, Producto destino) {
        // Set fechaIngreso
        if (origen.getFechaIngreso() == null) {
            destino.setFechaIngreso(LocalDate.now());
        } else {
            destino.setFechaIngreso(origen.getFechaIngreso());
        }

        // Set categoría con validación
        if (origen.getCategoria() != null && origen.getCategoria().getId() != null) {
            Categoria categoria = categoriaRepository.findById(origen.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no válida con ID: " + origen.getCategoria().getId()));
            destino.setCategoria(categoria);
        }
    }
}
