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
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public UserDetailsService userDetailsService() {
        return correo -> {
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            // Aquí armamos manualmente las Authorities
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name());

            return new org.springframework.security.core.userdetails.User(
                    usuario.getCorreo(),
                    usuario.getContrasena(),
                    List.of(authority) // Aquí pasamos la lista de authorities
            );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()

                        // Productos
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("ADMIN", "GESTOR")
                        .requestMatchers(HttpMethod.POST, "/api/productos/menor-stock").hasAnyRole("ADMIN", "GESTOR")
                        .requestMatchers(HttpMethod.POST, "/api/productos/categoria").hasAnyRole("ADMIN", "GESTOR")
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")

                        // Categorías, movimientos, reportes
                        .requestMatchers("/api/categorias/**").hasAnyRole("ADMIN", "GESTOR")
                        .requestMatchers("/api/movimientos/**").hasAnyRole("ADMIN", "GESTOR")
                        .requestMatchers("/api/reportes/**").hasAnyRole("ADMIN", "GESTOR")

                        // Usuarios
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

