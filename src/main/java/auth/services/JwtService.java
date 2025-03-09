package auth.services;

import auth.daos.RoleDAO;
import auth.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.*;

@Getter
@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(SecretKey secretKey, long expirationMs) {
        this.secretKey = secretKey;
        this.expirationMs = expirationMs;
    }

    public String generateJWTKey(User user) {
        return Jwts.builder()
                .setSubject(user.getIdUser().toString())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim("password", user.getPassword())
                .claim("roles", user.getRoles())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }
}
