package auth.controllers;

import auth.entities.User;
import auth.services.JwtService;
import auth.services.TokenService;
import auth.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

    private final UserService userService;
    private final JwtService jwtService;
    private final TokenService tokenService;

    @Autowired
    public TestController(UserService userService, JwtService jwtService, TokenService tokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
    }

    /**
     * Регистрация нового пользователя
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {

        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Аутентификация пользователя и возврат JWT токена
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        // В реальном приложении здесь должна быть проверка логина и пароля
        String token = jwtService.generateJWTKey(user);
        return ResponseEntity.ok(token);
    }

    /**
     * Доступ к защищенному ресурсу
     */
    @GetMapping("/secure")
    public ResponseEntity<String> secureEndpoint(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok("Welcome to the secure area, " + userDetails.getUsername());
    }

    /**
     * Извлечение данных из токена
     */
    @GetMapping("/token-info")
    public ResponseEntity<User> getTokenInfo(@RequestHeader("Authorization") String token) {
        User user = tokenService.createUserFromToken(token);
        return ResponseEntity.ok(user);
    }

    /**
     * Публичный эндпоинт
     */
    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("This is a public endpoint");
    }
}