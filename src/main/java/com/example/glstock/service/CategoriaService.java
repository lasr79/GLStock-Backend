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
    //Busca todas las categorias
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }
    //Busca  por id la categoria
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }
    // Metodo añadido para guardar una categoria
    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }
}