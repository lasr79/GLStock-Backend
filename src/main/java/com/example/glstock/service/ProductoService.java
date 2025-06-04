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
    //Busca por id
    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }
    //Busca por nombre sin tener que poner el nombre del producto entero
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }
    //Busca todos los productos
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }
    //Busca por categoria
    public List<Producto> buscarPorCategoria(Categoria categoria) {
        return productoRepository.findByCategoria(categoria);
    }
    //Busqueda de los 5 productos con menor estock
    public List<Producto> productosConMenorStock() {
        return productoRepository.findTop5ByOrderByCantidadAsc();
    }
    //Busqueda de los 5 ultimo productos agregados por fecha de ingreso
    public List<Producto> productosRecientes() {
        return productoRepository.findTop5ByOrderByFechaIngresoDesc();
    }
    //Guarda el producto
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }
    //Elimina el producto
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
    //Busca por nombre sin tener que poner el nombre de la categoria entera
    public List<Producto> buscarPorNombreCategoria(String nombreCategoria) {
        return productoRepository.findByCategoriaNombreContainingIgnoreCase(nombreCategoria);
    }
    //Actualiza la informacion del producto
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
        // Si el producto origen no tiene fecha de ingreso, se asigna la fecha actual y si tiene la pone
        if (origen.getFechaIngreso() == null) {
            destino.setFechaIngreso(LocalDate.now());
        } else {
            destino.setFechaIngreso(origen.getFechaIngreso());
        }

        // si no hay ningua categoria asociada lanza una excepcion indicando que no es valida
        if (origen.getCategoria() != null && origen.getCategoria().getId() != null) {
            Categoria categoria = categoriaRepository.findById(origen.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoria no válida con ID: " + origen.getCategoria().getId()));
            destino.setCategoria(categoria);
        }
    }
}
