package auth.controllers;

import auth.dto.UserRedisDTO;
import auth.services.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/users")
    public ResponseEntity<List<UserRedisDTO>> getAllUsers() {
        List<UserRedisDTO> users = testService.getAllUsersFromRedis();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getRedisData() {
        return ResponseEntity.ok(testService.getAllData());
    }
}
