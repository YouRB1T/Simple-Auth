package auth.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthUserRegistrationDTO {
    @Column(unique = true)
    private String nameProvider;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "access_token", nullable = false)
    private String accessToken;
}
