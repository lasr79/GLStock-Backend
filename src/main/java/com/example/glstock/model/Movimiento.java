package com.example.glstock.model;

import com.example.glstock.model.enums.TipoMovimiento;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipo; // Ahora usamos el enum TipoMovimiento desde la carpeta enums

    @Column(nullable = false)
    private Integer cantidad;

    private String motivo;

    private LocalDateTime fecha;

    @PrePersist
    public void prePersist() {
        fecha = LocalDateTime.now();
    }
}
