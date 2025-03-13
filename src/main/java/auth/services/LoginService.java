package auth.services;

import auth.entities.User;
import auth.repositories.UserLoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    /**
     * Аутентификация пользователя.
     *
     * @param username Имя пользователя.
     * @param password Пароль.
     * @return JWT-токен, если аутентификация успешна.
     * @throws RuntimeException Если аутентификация не удалась.
     */
    public String authenticateUser(String username, String password) {
        logger.debug("Attempting to authenticate user: {}", username);

        Optional<User> userOptional = userLoginRepository.findByUsernameOrEmail(username);

        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            User user = userOptional.get();

            // Проверяем, есть ли пользователь в Redis
            if (!userLoginRepository.isUserInRedis(username)) {
                // Добавляем пользователя в Redis
                userLoginRepository.addUserToRedis(user, passwordEncoder);
                logger.info("User {} added to Redis", username);
            }

            // Генерация JWT-токена
            String jwtToken = jwtService.generateJWTKey(user);
            logger.info("User {} authenticated successfully", username);
            return jwtToken;
        } else {
            logger.warn("Authentication failed for user: {}", username);
            throw new RuntimeException("Invalid credentials");
        }
    }

    /**
     * Удалить пользователя из Redis (логаут).
     *
     * @param username Имя пользователя.
     * @return true, если пользователь удален, иначе false.
     */
    public boolean logoutUser(String username) {
        logger.debug("Attempting to logout user: {}", username);
        boolean isDeleted = userLoginRepository.deleteUserFromRedis(username);

        if (isDeleted) {
            logger.info("User {} logged out successfully", username);
        } else {
            logger.warn("User {} not found in Redis", username);
        }

        return isDeleted;
    }

    /**
     * Получить JWT-токен пользователя из Redis.
     *
     * @param username Имя пользователя.
     * @return JWT-токен, если пользователь найден, иначе null.
     */
    public String getUserToken(String username) {
        logger.debug("Attempting to get token for user: {}", username);
        String token = userLoginRepository.getTokenFromRedis(username);

        if (token != null) {
            logger.info("Token found for user: {}", username);
        } else {
            logger.warn("No token found for user: {}", username);
        }

        return token;
    }

    /**
     * Обновить данные пользователя в Redis.
     *
     * @param user Пользователь.
     */
    public void updateUserInRedis(User user) {
        logger.debug("Attempting to update user in Redis: {}", user.getUsername());
        userLoginRepository.updateUserInRedis(user, passwordEncoder);
        logger.info("User {} updated in Redis", user.getUsername());
    }
}