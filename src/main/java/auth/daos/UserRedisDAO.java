package auth.daos;

import auth.dto.UserRedisDTO;
import org.springframework.data.repository.CrudRepository;

public interface UserRedisDAO extends CrudRepository<UserRedisDTO, Long> {
}
