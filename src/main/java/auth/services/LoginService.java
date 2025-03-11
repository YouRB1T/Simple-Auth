package auth.services;

import auth.dto.UserRedisDTO;
import auth.entities.User;
import auth.repositories.UserLoginRepository;
import auth.repositories.UserRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private UserRedisRepository userRedisRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    /**
     * Аутентифицировать пользователя по имени пользователя или email и паролю.
     *
     * @param username Имя пользователя или email.
     * @param password Пароль пользователя.
     * @return Объект UserRedisDTO с данными пользователя и JWT-токеном, если аутентификация успешна, иначе null.
     */
    public UserRedisDTO authenticateUser(String username, String password) {
        Optional<User> userOptional = userLoginRepository.findByUsernameOrEmail(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Проверяем, совпадает ли пароль
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Генерируем JWT-токен
                String token = jwtService.generateJWTKey(user);

                // Создаем объект UserRedisDTO
                UserRedisDTO userRedisDTO = new UserRedisDTO();
                userRedisDTO.setIdUser(user.getIdUser());
                userRedisDTO.setUserName(user.getUsername());
                userRedisDTO.setJWT(token);

                // Сохраняем токен в Redis
                userRedisRepository.addUser(user, passwordEncoder);

                return userRedisDTO; // Возвращаем данные пользователя и токен
            }
        }

        return null; // Аутентификация не удалась
    }
}

