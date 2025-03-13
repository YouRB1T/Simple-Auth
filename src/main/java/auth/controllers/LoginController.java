package auth.controllers;

import auth.dto.UserLoginDTO;
import auth.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * Аутентификация пользователя.
     *
     * @param userLoginDTO Данные для входа.
     * @return JWT-токен, если аутентификация успешна.
     */
    @PostMapping
    public ResponseEntity<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        try {
            String jwtToken = loginService.authenticateUser(userLoginDTO.getUsername(), userLoginDTO.getPassword());
            return ResponseEntity.ok(jwtToken);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Выход пользователя из системы.
     *
     * @param username Имя пользователя.
     * @return Сообщение об успешном выходе или ошибке.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String username) {
        boolean isLoggedOut = loginService.logoutUser(username);
        if (isLoggedOut) {
            return ResponseEntity.ok("User logged out successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    /**
     * Получить JWT-токен пользователя.
     *
     * @param username Имя пользователя.
     * @return JWT-токен, если пользователь найден, иначе сообщение об ошибке.
     */
    @GetMapping("/token")
    public ResponseEntity<String> getToken(@RequestParam String username) {
        String token = loginService.getUserToken(username);
        if (token != null) {
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found");
        }
    }
}