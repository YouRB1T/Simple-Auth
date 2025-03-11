package auth.services;

import auth.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Getter
@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(SecretKey secretKey, long expirationMs) {
        this.secretKey = secretKey;
        this.expirationMs = expirationMs;
    }

    public String generateJWTKey(User user) {
        try {
            String jwt = Jwts.builder()
                    .setSubject(user.getIdUser().toString())
                    .claim("username", user.getUsername())
                    .claim("email", user.getEmail())
                    .claim("password", user.getPassword())
                    .claim("roles", user.getRoles())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();

            logger.info("JWT generated successfully for user: {}", user.getUsername());
            return jwt;
        } catch (Exception e) {
            logger.error("Error while generating JWT for user: {}", user.getUsername(), e);
            throw e;
        }
    }
}