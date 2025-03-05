package auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

public class JwtConverter {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_MS = 3600000; // 1 час

    public static void main(String[] args) {
        args = new String[]{"", "1234545", "User", "a@a.ru"};
        String jwt = generateJwt("1234545", "User", "a@a.ru");
        System.out.println(jwt);
        verifyJwt(jwt);
    }

    private static void handleGenerateCommand(String[] args) {
        if (args.length < 4) {
            System.out.println("Недостаточно параметров для генерации");
            printHelp();
            return;
        }

        String userId = args[1];
        String username = args[2];
        String email = args[3];

        String jwt = generateJwt(userId, username, email);
        System.out.println("Сгенерированный JWT:");
        System.out.println(jwt);
    }

    private static void handleVerifyCommand(String[] args) {
        if (args.length < 2) {
            System.out.println("Не указан токен для проверки");
            printHelp();
            return;
        }

        String jwt = args[1];
        verifyJwt(jwt);
    }

    private static String generateJwt(String userId, String username, String email) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SECRET_KEY)
                .compact();
    }

    private static void verifyJwt(String jwt) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(jwt);

            System.out.println("Токен валиден!");
            System.out.println("Данные пользователя:");
            System.out.println("ID: " + claims.getBody().getSubject());
            System.out.println("Username: " + claims.getBody().get("username"));
            System.out.println("Email: " + claims.getBody().get("email"));
            System.out.println("Выдан: " + claims.getBody().getIssuedAt());
            System.out.println("Истекает: " + claims.getBody().getExpiration());
        } catch (Exception e) {
            System.out.println("Невалидный токен: " + e.getMessage());
        }
    }

    private static void printHelp() {
        System.out.println("Использование:");
        System.out.println("Генерация JWT: java JwtConverter generate <user_id> <username> <email>");
        System.out.println("Проверка JWT:  java JwtConverter verify <jwt_token>");
        System.out.println("\nПример:");
        System.out.println("java JwtConverter generate 123 john_doe john@example.com");
        System.out.println("java JwtConverter verify eyJhbGciOiJIUzI1NiJ9...");
    }
}
