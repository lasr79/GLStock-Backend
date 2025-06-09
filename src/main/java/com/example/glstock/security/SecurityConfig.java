package com.example.glstock.security;

import com.example.glstock.model.Usuario;
import com.example.glstock.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;
    // Filtro personalizado que verifica y valida el JWT en cada petición
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    // Carga los detalles del usuario segun el nombre de usuario (correo)
    @Bean
    public UserDetailsService userDetailsService() {
        return correo -> {
            // Busca el usuario en base al correo, lanza excepción si no existe
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            // Crea una autoridad (rol) basada en el rol del usuario (por ejemplo: ROLE_ADMIN)
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name());

            // Devuelve un objeto UserDetails con el correo, la contraseña y los roles
            return new org.springframework.security.core.userdetails.User(
                    usuario.getCorreo(),
                    usuario.getContrasena(),
                    List.of(authority)
            );
        };
    }

    // Define el codificador de contraseñas, usando BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura el sistema de seguridad HTTP
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                // Define las reglas de acceso para las rutas
                .authorizeHttpRequests(auth -> auth
                        // Permite el acceso sin autenticacion al endpoint de login
                        .requestMatchers("/api/auth/login").permitAll()

                        // Endpoints de productos: acceso segun el rol y metodo HTTP
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("ADMIN", "GESTOR")
                        .requestMatchers(HttpMethod.POST, "/api/productos/menor-stock").hasAnyRole("ADMIN", "GESTOR")
                        .requestMatchers(HttpMethod.POST, "/api/productos/categoria").hasAnyRole("ADMIN", "GESTOR")
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")

                        // Categorias, movimientos y reportes accesibles para los roles ADMIN o GESTOR (todos los usuarios)
                        .requestMatchers("/api/categorias/**").hasAnyRole("ADMIN", "GESTOR")
                        .requestMatchers("/api/movimientos/**").hasAnyRole("ADMIN", "GESTOR")
                        .requestMatchers("/api/reportes/**").hasAnyRole("ADMIN", "GESTOR")

                        // Permiso específico para GESTOR y ADMIN (consultar usuario por correo)
                        .requestMatchers("/api/usuarios/buscar-correo").hasAnyRole("ADMIN", "GESTOR")

                        // Solo ADMIN puede crear, actualizar y eliminar usuarios
                        .requestMatchers("/api/usuarios/crear").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/actualizar/**").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/eliminar/**").hasRole("ADMIN")

                        // Solo ADMIN puede buscar por nombre
                        .requestMatchers("/api/usuarios/buscar-nombre").hasRole("ADMIN")
                        // Cualquier otro endpoint requiere autenticación
                        .anyRequest().authenticated()
                )


                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )


                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Devuelve el objeto configurado de seguridad
        return http.build();
    }
}
