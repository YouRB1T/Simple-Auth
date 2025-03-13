package auth.controllers;

import auth.dto.LocalUserRegistrationDTO;
import auth.dto.OAuthUserRegistrationDTO;
import auth.services.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    /**
     * Регистрация локального пользователя.
     *
     * @param userDTO Данные пользователя.
     * @return Сообщение об успешной регистрации или ошибке.
     */
    @PostMapping("/local")
    public ResponseEntity<String> registerLocalUser(@RequestBody LocalUserRegistrationDTO userDTO) {
        try {
            registerService.registerLocalUser(userDTO);
            return ResponseEntity.ok("Local user registered successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Регистрация пользователя через OAuth.
     *
     * @param userDTO Данные пользователя.
     * @return Сообщение об успешной регистрации или ошибке.
     */
    @PostMapping("/oauth")
    public ResponseEntity<String> registerOAuthUser(@RequestBody OAuthUserRegistrationDTO userDTO) {
        try {
            registerService.registerOAuthUser(userDTO);
            return ResponseEntity.ok("OAuth user registered successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}