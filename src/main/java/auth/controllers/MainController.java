package auth.controllers;

import auth.dto.UserRedisDTO;
import auth.services.UserRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class MainController {

    @Autowired
    private UserRedisService userRedisService;

    @GetMapping
    public ResponseEntity<Iterable<UserRedisDTO>> getAllUsers() {
        Iterable<UserRedisDTO> users = userRedisService.findAllAuthUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRedisDTO> getUserById(@PathVariable Long id) {
        Optional<UserRedisDTO> user = userRedisService.findAuthUserById(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<UserRedisDTO> createUser(@RequestBody UserRedisDTO userRedisDTO) {
        UserRedisDTO createdUser = userRedisService.saveAuthUser(userRedisDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserRedisDTO> updateUser(@PathVariable Long id, @RequestBody UserRedisDTO userRedisDTO) {
        Optional<UserRedisDTO> existingUser = userRedisService.findAuthUserById(id);
        if (existingUser.isPresent()) {
            userRedisDTO.setIdUser(id); // Устанавливаем ID из пути
            UserRedisDTO updatedUser = userRedisService.saveAuthUser(userRedisDTO);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<UserRedisDTO> user = userRedisService.findAuthUserById(id);
        if (user.isPresent()) {
            userRedisService.deleteUserById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}