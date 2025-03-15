package auth.repositories;

import auth.dto.UserRedisDTO;
import auth.entities.User;
import auth.services.JwtService;
import auth.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class UserRedisRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRedisRepository.class);
    private static final String REDIS_KEY_PREFIX = "user:";

    @Autowired
    private final TokenService tokenService;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;

    public UserRedisRepository(TokenService tokenService, RedisTemplate<String, String> redisTemplate, JwtService jwtService) {
        this.tokenService = tokenService;
        this.redisTemplate = redisTemplate;
        this.jwtService = jwtService;
    }

    public void addUser(User user, PasswordEncoder passwordEncoder) {
        String key = REDIS_KEY_PREFIX + user.getUsername();
        logger.debug("Attempting to add user to Redis with key: {}", key);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String token = jwtService.generateJWTKey(user);
        logger.debug("Generated JWT token for user: {}", user.getUsername());

        redisTemplate.opsForValue().set(key, token, 1, TimeUnit.DAYS);
        logger.info("User {} added successfully to Redis with token expiration set to 1 day", user.getUsername());
    }

    @Transactional
    public String findTokenByUsername(String userName) {
        String key = REDIS_KEY_PREFIX + userName;
        logger.debug("Attempting to find token for user: {}", userName);

        String token = redisTemplate.opsForValue().get(key);

        if (token != null) {
            logger.info("Token found for user: {}", userName);
        } else {
            logger.warn("No token found for user: {}", userName);
        }

        return token;
    }

    public void updateUser(User user, PasswordEncoder passwordEncoder) {
        String key = REDIS_KEY_PREFIX + user.getUsername();
        logger.debug("Attempting to update user in Redis with key: {}", key);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            String newToken = jwtService.generateJWTKey(user);
            logger.debug("Generated new JWT token for user: {}", user.getUsername());

            redisTemplate.opsForValue().set(key, newToken, 1, TimeUnit.DAYS);
            logger.info("User {} updated successfully in Redis with new token", user.getUsername());
        } else {
            logger.error("User not found in Redis: {}", user.getUsername());
            throw new RuntimeException("User not found: " + user.getUsername());
        }
    }

    public boolean deleteUserByUsername(String username) {
        String key = REDIS_KEY_PREFIX + username;
        logger.debug("Attempting to delete user from Redis with key: {}", key);

        boolean isDeleted = Boolean.TRUE.equals(redisTemplate.delete(key));

        if (isDeleted) {
            logger.info("User {} deleted successfully from Redis", username);
        } else {
            logger.warn("No user found to delete with key: {}", key);
        }

        return isDeleted;
    }

    public List<UserRedisDTO> getAllUsers() {
        logger.debug("Attempting to fetch all users from Redis");

        List<UserRedisDTO> users = redisTemplate.keys(REDIS_KEY_PREFIX + "*")
                .stream()
                .map(key -> {
                    String jwt = redisTemplate.opsForValue().get(key);
                    Long idUser = tokenService.extractUserId(jwt);

                    UserRedisDTO userRedisDTO = new UserRedisDTO();
                    userRedisDTO.setIdUser(idUser);
                    userRedisDTO.setUserName(key.replace(REDIS_KEY_PREFIX, ""));
                    userRedisDTO.setJWT(jwt);

                    logger.debug("Fetched user from Redis: {}", userRedisDTO.getUserName());

                    return userRedisDTO;
                })
                .collect(Collectors.toList());

        logger.info("Fetched {} users from Redis", users.size());

        return users;
    }
}