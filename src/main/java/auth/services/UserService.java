package auth.services;

import auth.entities.User;
import auth.repositories.UserRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRedisRepository userRedisRepository;

    @Autowired
    public UserService(UserRedisRepository userRedisRepository) {
        this.userRedisRepository = userRedisRepository;
    }


}