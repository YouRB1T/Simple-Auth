package ru.vsu.cs.services.daos;

import ru.vsu.cs.daos.UserDAO;
import ru.vsu.cs.entities.Role;
import ru.vsu.cs.entities.User;

import java.util.Optional;

public class UserServiceDAO {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Проверка, имеет ли пользователь указанную роль
     *
     * @param usernameOrEmail Логин или email пользователя
     * @param roleName        Название роли
     * @return true, если пользователь имеет роль, иначе false
     */
    public boolean hasRole(String usernameOrEmail, String roleName) {
        Optional<User> userOptional = userDAO.findByUsernameOrEmail(usernameOrEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            for (Role role : user.getRoles()) {
                if (role.getRole().equals(roleName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Проверка, существует ли пользователь с указанным логином или email
     *
     * @param usernameOrEmail Логин или email пользователя
     * @return true, если пользователь существует, иначе false
     */
    public boolean userExists(String usernameOrEmail) {
        return userDAO.findByUsernameOrEmail(usernameOrEmail).isPresent();
    }
}
