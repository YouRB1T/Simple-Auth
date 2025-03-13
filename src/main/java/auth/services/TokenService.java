package auth.services;

import auth.entities.Role;
import auth.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Getter
@AllArgsConstructor
@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final SecretKey secretKey;
    private final long expirationMs;

    public boolean isTokenSignatureValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error("Invalid token signature: {}", token, e);
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        if (token == null || token.isBlank()) {
            logger.warn("Token is null or empty");
            return true;
        }

        try {
            Date expirationDate = extractExpiration(token);
            boolean isExpired = expirationDate.before(new Date());
            if (isExpired) {
                logger.warn("Token has expired: {}", token);
            }
            return isExpired;
        } catch (Exception e) {
            logger.error("Error while checking token expiration", e);
            return true;
        }
    }

    private Date extractExpiration(String token) {
        try {
            Claims claims = verifyToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            logger.error("Error while extracting expiration from token", e);
            return new Date(0); // Возвращаем нулевую дату при ошибке
        }
    }

    public Claims verifyToken(String token) {
        if (token == null || token.isBlank()) {
            logger.warn("Token cannot be null or empty");
            throw new IllegalArgumentException("Token cannot be null or empty.");
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            logger.info("Token verified successfully: {}", token);
            return claims;
        } catch (SignatureException e) {
            logger.error("Invalid token signature", e);
            throw new SignatureException("Invalid token signature.", e);
        } catch (Exception e) {
            logger.error("Invalid token", e);
            throw new IllegalArgumentException("Invalid token: " + e.getMessage(), e);
        }
    }

    public String extractUsername(String token) {
        try {
            Claims claims = verifyToken(token);
            String username = claims.get("username", String.class);
            logger.info("Username extracted from token: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Error while extracting username from token", e);
            throw new IllegalArgumentException("Error extracting username from token.", e);
        }
    }

    public String extractEmail(String token) {
        try {
            Claims claims = verifyToken(token);
            String email = claims.get("email", String.class);
            logger.info("Email extracted from token: {}", email);
            return email;
        } catch (Exception e) {
            logger.error("Error while extracting email from token", e);
            throw new IllegalArgumentException("Error extracting email from token.", e);
        }
    }

    public String extractPassword(String token) {
        try {
            Claims claims = verifyToken(token);
            String password = claims.get("password", String.class);
            logger.info("Password extracted from token for user: {}", extractUsername(token));
            return password;
        } catch (Exception e) {
            logger.error("Error while extracting password from token", e);
            throw new IllegalArgumentException("Error extracting password from token.", e);
        }
    }

    public Set<Role> extractRoles(String token) {
        try {
            Claims claims = verifyToken(token);
            Set<Role> roles = claims.get("roles", Set.class);
            logger.info("Roles extracted from token for user: {}", extractUsername(token));
            return roles;
        } catch (Exception e) {
            logger.error("Error while extracting roles from token", e);
            return Collections.emptySet();
        }
    }

    public Long extractUserId(String token) {
        try {
            Claims claims = verifyToken(token);
            Long userId = Long.parseLong(claims.getSubject());
            logger.info("User ID extracted from token: {}", userId);
            return userId;
        } catch (Exception e) {
            logger.error("Error while extracting user ID from token", e);
            throw new IllegalArgumentException("Error extracting user ID from token.", e);
        }
    }

    public User createUserFromToken(String token) {
        try {
            if (!isTokenSignatureValid(token)) {
                logger.warn("Invalid token signature for token: {}", token);
                throw new IllegalArgumentException("Invalid token signature");
            }

            if (isTokenExpired(token)) {
                logger.warn("Token has expired for token: {}", token);
                throw new IllegalArgumentException("Token has expired");
            }

            Claims claims = verifyToken(token);

            User user = new User();
            user.setIdUser(extractUserId(token));
            user.setUsername(extractUsername(token));
            user.setEmail(extractEmail(token));
            user.setPassword(extractPassword(token));
            user.setRoles(extractRoles(token));

            logger.info("User created from token successfully: {}", user.getUsername());
            return user;
        } catch (Exception e) {
            logger.error("Error while creating user from token", e);
            throw e;
        }
    }
}