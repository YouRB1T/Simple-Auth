package auth.services;
import auth.entities.Role;
import auth.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@Service
public class TokenService {

    private final SecretKey secretKey;
    private final long expirationMs;

    /**
     * Проверка валидности подписи токена
     */
    private boolean isTokenSignatureValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Проверка, истек ли срок действия токена
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлечение срока действия токена
     */
    private Date extractExpiration(String token) {
        return verifyToken(token).getExpiration();
    }

    /**
     * Проверка и извлечение данных из токена
     */
    public Claims verifyToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or empty.");
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new SignatureException("Invalid token signature.", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token: " + e.getMessage(), e);
        }
    }

    /**
     * Извлечение username из токена
     */
    public String extractUsername(String token) {
        Claims claims = verifyToken(token);
        return claims.get("username", String.class);
    }

    /**
     * Извлечение email из токена
     */
    public String extractEmail(String token) {
        Claims claims = verifyToken(token);
        return claims.get("email", String.class);
    }

    /**
     * Извлечение пароля из токена
     */
    public String extractPassword(String token) {
        Claims claims = verifyToken(token);
        return claims.get("password", String.class);
    }

    /**
     * Извлечение ролей из токена
     */
    public Set<Role> extractRoles(String token) {
        Claims claims = verifyToken(token);
        return claims.get("roles", Set.class);
    }

    /**
     * Извлечение ID пользователя из токена
     */
    public Long extractUserId(String token) {
        Claims claims = verifyToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Создание пользователя на основе данных из токена
     */
    public User createUserFromToken(String token) {
        if (!isTokenSignatureValid(token)) {
            throw new IllegalArgumentException("Invalid token signature");
        }

        if (isTokenExpired(token)) {
            throw new IllegalArgumentException("Token has expired");
        }

        Claims claims = verifyToken(token);

        User user = new User();
        user.setIdUser(extractUserId(token));
        user.setUsername(extractUsername(token));
        user.setEmail(extractEmail(token));
        user.setPassword(extractPassword(token));
        user.setRoles(extractRoles(token));

        return user;
    }
}
