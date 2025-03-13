package auth.repositories;

import auth.dao.AuthProviderDAO;
import auth.dao.RoleDAO;
import auth.dao.UserDAO;
import auth.dao.UserOAuthDAO;
import auth.dto.LocalUserRegistrationDTO;
import auth.dto.OAuthUserRegistrationDTO;
import auth.entities.AuthProvider;
import auth.entities.Role;
import auth.entities.User;
import auth.entities.UserOAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Random;

@Repository
public class UserRegistrationRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationRepository.class);

    private Random random = new Random();

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserOAuthDAO userOAuthDAO;

    @Autowired
    private AuthProviderDAO authProviderDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerLocalUser(LocalUserRegistrationDTO registrationDTO) {
        logger.debug("Attempting to register local user with email: {}", registrationDTO.getEmail());

        Optional<User> existingUser = userDAO.findByUsernameOrEmail(registrationDTO.getEmail(), registrationDTO.getNumber());

        if (existingUser.isPresent()) {
            logger.warn("User already exists with email: {} or number: {}", registrationDTO.getEmail(), registrationDTO.getNumber());
            throw new IllegalArgumentException("Пользователь с указанным email или номером уже существует.");
        }

        User newUser = new User();
        newUser.setEmail(registrationDTO.getEmail());
        newUser.setUsername(registrationDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        newUser.setNumber(registrationDTO.getNumber());
        userDAO.createUser(newUser);
        logger.info("Local user created successfully: {}", newUser.getUsername());

        Long userId = newUser.getIdUser();

        userDAO.addRoleToUser(userId.intValue(), 1);
        logger.info("Role USER added successfully to user: {}", newUser.getUsername());

        return newUser;
    }

    public User registerOAuthUser(OAuthUserRegistrationDTO registrationDTO) {
        logger.debug("Attempting to register OAuth user with email: {}", registrationDTO.getEmail());

        Optional<User> existingUser = userDAO.findByUsernameOrEmail(registrationDTO.getEmail(), null);

        if (existingUser.isPresent()) {
            logger.warn("User already exists with email: {}", registrationDTO.getEmail());
            throw new IllegalArgumentException("Пользователь с указанным email уже существует.");
        }

        User newUser = new User();
        newUser.setEmail(registrationDTO.getEmail());
        newUser.setUsername(registrationDTO.getUsername());
        Role userRole = new Role(1);
        newUser.getRoles().add(userRole);

        userDAO.createUser(newUser);
        logger.info("OAuth user created successfully: {}", newUser.getUsername());

        String providerName = registrationDTO.getNameProvider();
        Optional<AuthProvider> providerOptional = authProviderDAO.findByName(providerName);

        if (providerOptional.isEmpty()) {
            logger.error("Provider not found with name: {}", providerName);
            throw new IllegalArgumentException("Провайдер с указанным именем не найден.");
        }

        AuthProvider provider = providerOptional.get();

        UserOAuth userOAuth = new UserOAuth();
        userOAuth.setProvider(provider);
        userOAuth.setAccessToken(registrationDTO.getAccessToken());
        userOAuth.setUser(newUser);

        userOAuthDAO.create(userOAuth);
        logger.info("OAuth data saved successfully for user: {}", newUser.getUsername());

        userDAO.addRoleToUser(newUser.getIdUser().intValue(), 1);
        logger.info("Role USER added successfully to OAuth user: {}", newUser.getUsername());

        return newUser;
    }
}