package auth.services;

import auth.dto.UserRedisDTO;
import auth.repositories.UserRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserRedisServiceTest {

    @Mock
    private UserRedisRepository userRedisRepository;

    @InjectMocks
    private UserRedisService userRedisService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllAuthUsers() {
        UserRedisDTO user1 = new UserRedisDTO(1L, "jwt1");
        UserRedisDTO user2 = new UserRedisDTO(2L, "jwt2");
        when(userRedisRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        Iterable<UserRedisDTO> result = userRedisService.findAllAuthUsers();

        assertNotNull(result);
        assertEquals(2, ((java.util.Collection<?>) result).size());
        verify(userRedisRepository, times(1)).findAll();
    }

    @Test
    void findAuthUserById() {
        UserRedisDTO user = new UserRedisDTO(1L, "jwt1");
        when(userRedisRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserRedisDTO> result = userRedisService.findAuthUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRedisRepository, times(1)).findById(1L);
    }

    @Test
    void saveAuthUser() {
        UserRedisDTO user = new UserRedisDTO(1L, "jwt1");
        when(userRedisRepository.save(user)).thenReturn(user);

        UserRedisDTO result = userRedisService.saveAuthUser(user);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRedisRepository, times(1)).save(user);
    }

    @Test
    void deleteUserById() {
        doNothing().when(userRedisRepository).deleteById(1L);

        userRedisService.deleteUserById(1L);

        verify(userRedisRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser() {
        UserRedisDTO user = new UserRedisDTO(1L, "jwt1");
        doNothing().when(userRedisRepository).delete(user);

        userRedisService.deleteUser(user);

        verify(userRedisRepository, times(1)).delete(user);
    }
}
