package auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Provider name is required")
    @Column(unique = true)
    private String nameProvider;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Access token is required")
    @Column(name = "access_token", nullable = false)
    private String accessToken;
}
