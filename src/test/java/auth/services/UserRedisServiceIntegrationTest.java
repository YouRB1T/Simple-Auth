package auth.services;

import auth.Main;
import auth.dto.UserRedisDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Main.class)
class UserRedisServiceIntegrationTest {

    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private RedisTemplate<String, UserRedisDTO> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void testSaveAndRetrieveUser() {
        UserRedisDTO user = new UserRedisDTO(1L, "test-jwt");
        userRedisService.saveAuthUser(user);

        Optional<UserRedisDTO> result = userRedisService.findAuthUserById(1L);
        assertTrue(result.isPresent());
        assertEquals("test-jwt", result.get().getJWT());
    }
}