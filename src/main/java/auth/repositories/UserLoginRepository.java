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

    public Optional<User> findByUsernameOrEmail(String email) {
        logger.debug("Attempting to find user by username or email: {}", email);
        return userDAO.findByUsernameOrEmail(email, null);
    }

    public boolean isUserInRedis(String username) {
        logger.debug("Checking if user exists in Redis: {}", username);
        return userRedisRepository.findTokenByUsername(username) != null;
    }

    public void addUserToRedis(User user, PasswordEncoder passwordEncoder) {
        logger.debug("Attempting to add user to Redis: {}", user.getUsername());
        userRedisRepository.addUser(user, passwordEncoder);
    }

    public boolean deleteUserFromRedis(String username) {
        logger.debug("Attempting to delete user from Redis: {}", username);
        return userRedisRepository.deleteUserByUsername(username);
    }

    public String getTokenFromRedis(String username) {
        logger.debug("Attempting to get token for user: {}", username);
        return userRedisRepository.findTokenByUsername(username);
    }

    public void updateUserInRedis(User user, PasswordEncoder passwordEncoder) {
        logger.debug("Attempting to update user in Redis: {}", user.getUsername());
        userRedisRepository.updateUser(user, passwordEncoder);
    }
}