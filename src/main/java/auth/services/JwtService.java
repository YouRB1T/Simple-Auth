package auth.services;

import auth.daos.RoleDAO;
import auth.dto.UserRedisDTO;
import auth.entities.Role;
import auth.entities.User;
import auth.repositories.UserRedisRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.security.auth.message.callback.SecretKeyCallback;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Service
public class JwtService {

    @Autowired
    protected UserRedisDAO userRedisRepository;
    @Autowired
    protected RoleDAO roleDAO;
    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(SecretKey secretKey, long expirationMs) {
        this.secretKey = secretKey;
        this.expirationMs = expirationMs;
    }

    public boolean isTokenValid(String token) {
        try {
            if (token == null || !isTokenSignatureValid(token)) {
                return false;
            }

            Long userId = getUserID(token);
            Optional<UserRedisDTO> redisData = userRedisRepository.findById(userId);

            if (isTokenExpired(token)) {
                userRedisRepository.deleteById(userId);
                return false;
            }

            return redisData.isPresent() &&
                    token.equals(redisData.get().getJWT());

        } catch (Exception e) {
            return false;
        }
    }

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

    protected String generateJWTKey(User user) {
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

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return verifyToken(token).getExpiration();
    }

    public Claims verifyToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getUserID(String token) {
        return Long.parseLong(verifyToken(token).getSubject());
    }

    public Set<Role> getUserRoles(String token) {
        Set<Role> roles = new HashSet<>();

        try {
            Claims claims = verifyToken(token);
            List rolesClaim = claims.get("roles", List.class);

            if (rolesClaim == null) {
                return Collections.emptySet();
            }

            Iterator<?> iterator = rolesClaim.getColumns().iterator();
            while (iterator.hasNext()) {
                Object roleObj = iterator.next();
                String roleName = roleObj.toString();

                Optional<Role> roleOptional = convertToRole(roleName);
                if (roleOptional.isPresent()) {
                    roles.add(roleOptional.get());
                }
            }
            return roles;
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    private Optional<Role> convertToRole(String roleName) {
        try {
            return roleDAO.findByName(roleName);
        } catch (Exception e) {
            System.out.println("Invalid role name in token: {}" + roleName);
            return Optional.empty();
        }
    }
}
