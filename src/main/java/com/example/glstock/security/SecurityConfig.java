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
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;

    // Define cómo se carga el usuario desde la base de datos
    @Bean
    public UserDetailsService userDetailsService() {
        return correo -> {
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            return User.builder()
                    .username(usuario.getCorreo())
                    .password(usuario.getContrasena()) // ya codificada
                    .roles(usuario.getRol().name())     // ADMIN o GESTOR
                    .build();
        };
    }

    // Codificador de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración de seguridad HTTP
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("ADMIN", "GESTOR")
                        .requestMatchers("/api/categorias/**").hasAnyRole("ADMIN", "GESTOR")
                        .requestMatchers("/api/movimientos/**").hasAnyRole("ADMIN", "GESTOR")
                        .requestMatchers("/api/reportes/**").hasAnyRole("ADMIN", "GESTOR")
                        .requestMatchers("/api/productos/**").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {}); // Se activa httpBasic con configuración por defecto

        return http.build();
    }
}