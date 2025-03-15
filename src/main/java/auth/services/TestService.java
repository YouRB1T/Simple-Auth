package auth.services;

import auth.dto.UserRedisDTO;
import auth.repositories.UserRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TestService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserRedisRepository userRedisRepository;

    public List<UserRedisDTO> getAllUsersFromRedis() {
        return userRedisRepository.getAllUsers();
    }

    public Map<String, Object> getAllData() {
        Map<String, Object> data = new HashMap<>();
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null) {
            for (String key : keys) {
                DataType type = redisTemplate.type(key);
                switch (type) {
                    case STRING:
                        data.put(key, redisTemplate.opsForValue().get(key));
                        break;
                    case LIST:
                        data.put(key, redisTemplate.opsForList().range(key, 0, -1));
                        break;
                    case HASH:
                        data.put(key, redisTemplate.opsForHash().entries(key));
                        break;
                    case SET:
                        data.put(key, redisTemplate.opsForSet().members(key));
                        break;
                    case ZSET:
                        data.put(key, redisTemplate.opsForZSet().range(key, 0, -1));
                        break;
                    default:
                        System.out.println("Unsupported key type '" + type + "' for key '" + key + "'");
                        break;
                }
            }
        }
        return data;
    }
}
