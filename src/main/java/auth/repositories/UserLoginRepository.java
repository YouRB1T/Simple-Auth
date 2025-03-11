package auth.repositories;

import auth.dao.UserDAO;
import auth.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserLoginRepository {

    @Autowired
    private UserDAO userDAO; // Предполагается, что UserDAO уже реализован

    /**
     * Найти пользователя по имени пользователя или email.
     *
     * @param username Имя пользователя или email.
     * @return Optional с пользователем, если найден, иначе пустой Optional.
     */
    public Optional<User> findByUsernameOrEmail(String username) {
        return userDAO.findByUsernameOrEmail(username, null);
    }
}