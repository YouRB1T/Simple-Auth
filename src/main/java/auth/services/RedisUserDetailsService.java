package auth.services;

import auth.dto.UserRedisDTO;
import auth.repositories.UserRedisRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RedisUserDetailsService implements UserDetailsService {

    private final UserRedisRepository userRedisRepository;

    public RedisUserDetailsService(UserRedisRepository userRedisRepository) {
        this.userRedisRepository = userRedisRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Ищем пользователя в Redis по username
        UserRedisDTO userRedisDTO = userRedisRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Преобразуем UserRedisDTO в UserDetails
        return User.builder()
                .username(userRedisDTO.getUsername())
                .password(userRedisDTO.getPassword())
                .roles(userRedisDTO.getRoles().toArray(new String[0]))
                .build();
    }
}
