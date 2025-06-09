package com.example.glstock.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    //Es la firma del firma que tendra el token para garantizar la seguridad
    private static final String SECRET_KEY = "746F707365637265746B657931323334353637383930313233343536373839303132";

    // Genera un token  con el correo como "subject"
    public String generateToken(String correo) {
        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5)) // Token válido por 5 horas
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extrae el correo (subject) del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Verifica si el token es valido
    public boolean isTokenValid(String token, String userDetailsUsername) {
        final String username = extractUsername(token);
        return (username.equals(userDetailsUsername)) && !isTokenExpired(token);
    }

    // Verifica si el token ha expirado
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //Extrae la fecha de expiracion del token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //Extrae un campo (claim) especifico del token usando una funcion lambda
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims); // Aplica la funcion al conjunto de claims
    }

    // Extrae todos los claims del token usando la clave secreta
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey()) // Establece la clave secreta para verificar firma
                .build()
                .parseClaimsJws(token) // Parsea y valida el token
                .getBody(); // Devuelve el cuerpo del token (los claims)
    }

    // Convierte la clave secreta codificada en Base64 a una Key válida para firmar/verificar
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}