package auth.repositories;
import auth.dto.UserRedisDTO;
import auth.entities.User;
import auth.services.JwtService;
import auth.services.TokenService;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@AllArgsConstructor
@Repository
public class UserRedisRepository {

    private static final String REDIS_KEY_PREFIX = "user:";

    @Autowired
    private final TokenService tokenService;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;

    public void addUser(User user, PasswordEncoder passwordEncoder) {
        String key = REDIS_KEY_PREFIX + user.getUsername();

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String token = jwtService.generateJWTKey(user);

        redisTemplate.opsForValue().set(key, token, 1, TimeUnit.DAYS);
    }

    public String findTokenByUsername(String userName) {
        String key = REDIS_KEY_PREFIX + userName;
        return redisTemplate.opsForValue().get(key);
    }

    public void updateUser(User user, PasswordEncoder passwordEncoder) {
        String key = REDIS_KEY_PREFIX + user.getUsername();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            String newToken = jwtService.generateJWTKey(user);

            redisTemplate.opsForValue().set(key, newToken, 1, TimeUnit.DAYS);
        } else {
            throw new RuntimeException("User not found: " + user.getUsername());
        }
    }

    public boolean deleteUserByUsername(String username) {
        String key = REDIS_KEY_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public List<UserRedisDTO> getAllUsers() {
        return redisTemplate.keys(REDIS_KEY_PREFIX + "*")
                .stream()
                .map(key -> {
                    String jwt = redisTemplate.opsForValue().get(key);
                    Long idUser = tokenService.extractUserId(jwt);

                    UserRedisDTO userRedisDTO = new UserRedisDTO();
                    userRedisDTO.setIdUser(idUser);
                    userRedisDTO.setUserName(key.replace(REDIS_KEY_PREFIX, ""));
                    userRedisDTO.setJWT(jwt);

                    return userRedisDTO;
                })
                .collect(Collectors.toList());
    }
}