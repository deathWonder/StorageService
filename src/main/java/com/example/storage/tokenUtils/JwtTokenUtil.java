package com.example.storage.tokenUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenUtil {
    private static final String SECRET_KEY = "netology"; // ключ
    private static final long EXPIRATION_TIME = 86400000; // Токен действует 24 часа
    private final List<String> blacklist = new ArrayList<>();

    public String generateToken(String login) {
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        return Jwts.builder()
                .setSubject(login)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String getUsername(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // Удаляем префикс "Bearer "
        }

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return !blacklist.contains(token); // Проверка наличия токена в черном списке
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    public void invalidateToken(String token) {
        blacklist.add(token);
    }
}
