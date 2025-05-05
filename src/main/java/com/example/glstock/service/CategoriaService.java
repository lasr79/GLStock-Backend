package com.example.glstock.service;

import com.example.glstock.model.Categoria;
import com.example.glstock.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    // Método añadido para guardar una categoría
    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }
}