package com.example.glstock.service;

import com.example.glstock.model.Categoria;
import com.example.glstock.model.Producto;
import com.example.glstock.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> buscarTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> buscarPorCategoria(Categoria categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public List<Producto> productosMenorStock(int limite) {
        return productoRepository.findByCantidadLessThan(limite);
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
}
