package ru.vsu.cs.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import ru.vsu.cs.daos.UserDAO;
import ru.vsu.cs.entities.User;

import java.util.Optional;

public class AuthServiceIml implements AuthService {
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceIml(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Аутентификация пользователя
     *
     * @param usernameOrEmail Логин или email пользователя
     * @param password        Пароль пользователя
     * @return true, если аутентификация успешна, иначе false
     */
    @Override
    public boolean authenticate(String usernameOrEmail, String password) {
        Optional<User> userOptional = userDAO.findByUsernameOrEmail(usernameOrEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
}
