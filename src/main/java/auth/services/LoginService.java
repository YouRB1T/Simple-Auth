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

    public String authenticateUser(String username, String password) {
        logger.debug("Attempting to authenticate user: {}", username);

        Optional<User> userOptional = userLoginRepository.findByUsernameOrEmail(username);

        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            User user = userOptional.get();

            if (!userLoginRepository.isUserInRedis(username)) {
                userLoginRepository.addUserToRedis(user, passwordEncoder);
                logger.info("User {} added to Redis", username);
            }

            String jwtToken = jwtService.generateJWTKey(user);
            logger.info("User {} authenticated successfully", username);
            return jwtToken;
        } else {
            logger.warn("Authentication failed for user: {}", username);
            throw new RuntimeException("Invalid credentials");
        }
    }

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

    public void updateUserInRedis(User user) {
        logger.debug("Attempting to update user in Redis: {}", user.getUsername());
        userLoginRepository.updateUserInRedis(user, passwordEncoder);
        logger.info("User {} updated in Redis", user.getUsername());
    }
}