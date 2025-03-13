package auth.repositories;

import auth.dao.UserDAO;
import auth.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserLoginRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserLoginRepository.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserRedisRepository userRedisRepository;

    /**
     * Найти пользователя по имени пользователя или email.
     *
     * @param username Имя пользователя или email.
     * @return Optional с пользователем, если найден, иначе пустой Optional.
     */
    public Optional<User> findByUsernameOrEmail(String username) {
        logger.debug("Attempting to find user by username or email: {}", username);
        return userDAO.findByUsernameOrEmail(username, null);
    }

    /**
     * Проверить, существует ли пользователь в Redis.
     *
     * @param username Имя пользователя.
     * @return true, если пользователь существует в Redis, иначе false.
     */
    public boolean isUserInRedis(String username) {
        logger.debug("Checking if user exists in Redis: {}", username);
        return userRedisRepository.findTokenByUsername(username) != null;
    }

    /**
     * Добавить пользователя в Redis.
     *
     * @param user Пользователь.
     * @param passwordEncoder Кодировщик паролей.
     */
    public void addUserToRedis(User user, PasswordEncoder passwordEncoder) {
        logger.debug("Attempting to add user to Redis: {}", user.getUsername());
        userRedisRepository.addUser(user, passwordEncoder);
    }

    /**
     * Удалить пользователя из Redis.
     *
     * @param username Имя пользователя.
     * @return true, если пользователь удален, иначе false.
     */
    public boolean deleteUserFromRedis(String username) {
        logger.debug("Attempting to delete user from Redis: {}", username);
        return userRedisRepository.deleteUserByUsername(username);
    }

    /**
     * Получить JWT-токен пользователя из Redis.
     *
     * @param username Имя пользователя.
     * @return JWT-токен, если пользователь найден, иначе null.
     */
    public String getTokenFromRedis(String username) {
        logger.debug("Attempting to get token for user: {}", username);
        return userRedisRepository.findTokenByUsername(username);
    }

    /**
     * Обновить данные пользователя в Redis.
     *
     * @param user Пользователь.
     * @param passwordEncoder Кодировщик паролей.
     */
    public void updateUserInRedis(User user, PasswordEncoder passwordEncoder) {
        logger.debug("Attempting to update user in Redis: {}", user.getUsername());
        userRedisRepository.updateUser(user, passwordEncoder);
    }
}