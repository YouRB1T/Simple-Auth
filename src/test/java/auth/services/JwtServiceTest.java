package auth.services;
import auth.dao.RoleDAO;
import auth.entities.Role;
import auth.entities.User;
import auth.repositories.UserRedisRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.crypto.SecretKey;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final long EXPIRATION_MS = 3600000;

    @Mock
    private UserRedisRepository userRedisRepository;

    @Mock
    private RoleDAO roleDAO;

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        jwtService = new JwtService(SECRET_KEY, EXPIRATION_MS);

        jwtService.roleDAO = roleDAO;

        testUser = new User();
        testUser.setIdUser(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRoles(Set.of(new Role("USER")));
    }

    @Test
    void generatedTokenShouldContainCorrectClaims() {
        String token = jwtService.generateJWTKey(testUser);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(testUser.getIdUser().toString(), claims.getSubject());
        assertEquals(testUser.getUsername(), claims.get("username"));
        assertEquals(testUser.getEmail(), claims.get("email"));
        assertEquals(testUser.getPassword(), claims.get("password"));
        assertTrue(claims.get("roles") instanceof List);
    }

}
