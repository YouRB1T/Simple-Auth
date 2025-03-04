package auth.services;

import auth.dto.UserRedisDTO;
import auth.repositories.UserRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserRedisService {

    @Autowired
    private UserRedisRepository userRedisRepository;

    public Iterable<UserRedisDTO> findAllAuthUsers() {
        return userRedisRepository.findAll();
    }

    public Optional<UserRedisDTO> findAuthUserById(Long id) {
        return userRedisRepository.findById(id);
    }

    public UserRedisDTO saveAuthUser(UserRedisDTO userRedisDTO) {
        return userRedisRepository.save(userRedisDTO);
    }

    public void deleteUserById(Long id) {
        userRedisRepository.deleteById(id);
    }

    public void deleteUser(UserRedisDTO userRedisDTO) {
        userRedisRepository.delete(userRedisDTO);
    }

}
