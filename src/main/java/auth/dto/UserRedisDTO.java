package auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("user")
public class UserRedisDTO {
    @Id
    private Long idUser;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("JWT")
    private String JWT;
}
