package com.example.glstock.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private ApplicationContext applicationContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        System.out.println("🔐 JwtAuthenticationFilter ACTIVADO"); // 👈

        if (applicationContext == null) {
            applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String correo;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ No se encontró header Authorization válido");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        correo = jwtService.extractUsername(jwt);

        System.out.println("📨 Token recibido para correo: " + correo); // 👈

        if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = applicationContext.getBean(UserDetailsService.class)
                    .loadUserByUsername(correo);

            if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                System.out.println("✅ Token válido. Estableciendo autenticación para: " + correo); // 👈

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("⛔ Token inválido para: " + correo); // 👈
            }
        } else {
            System.out.println("⚠️ Ya existe autenticación o correo es null"); // 👈
        }

        filterChain.doFilter(request, response);
    }
}

