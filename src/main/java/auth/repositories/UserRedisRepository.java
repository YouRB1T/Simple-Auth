package auth.repositories;

import auth.dto.UserRedisDTO;
import org.springframework.data.repository.CrudRepository;

public interface UserRedisRepository extends CrudRepository<UserRedisDTO, Long> {
}
