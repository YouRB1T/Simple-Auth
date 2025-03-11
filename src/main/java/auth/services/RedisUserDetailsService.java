package auth.services;

import auth.daos.RoleDAO;
import auth.entities.Role;
import auth.entities.User;
import auth.repositories.UserRedisRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RedisUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(RedisUserDetailsService.class);

    @Autowired
    private final UserRedisRepository userRedisRepository;

    @Autowired
    private final TokenService tokenService;

    private final RoleDAO roleDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String userToken = userRedisRepository.findTokenByUsername(username);

        if (userToken == null) {
            logger.warn("User not found in Redis: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        try {
            UserDetails userDetails = tokenService.createUserFromToken(userToken);
            logger.info("User loaded successfully from Redis: {}", username);
            return userDetails;
        } catch (Exception e) {
            logger.error("Error parsing user data from token for user: {}", username, e);
            throw new UsernameNotFoundException("Error parsing user data", e);
        }
    }

    public Set<Role> getUserRoles(String token) {
        Set<Role> roles = new HashSet<>();

        try {
            Claims claims = tokenService.verifyToken(token);

            List<?> rolesClaim = claims.get("roles", List.class);

            if (rolesClaim == null || rolesClaim.isEmpty()) {
                logger.warn("No roles found in token");
                return Collections.emptySet();
            }

            roles.addAll(rolesClaim.stream()
                    .map(Object::toString)
                    .map(roleName -> convertToRole(roleName).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet()));

            logger.info("Roles retrieved successfully from token");
            return roles;
        } catch (Exception e) {
            logger.error("Error retrieving roles from token", e);
            return Collections.emptySet();
        }
    }

    private Optional<Role> convertToRole(String roleName) {
        try {
            Optional<Role> roleOptional = roleDAO.findByName(roleName);
            if (!roleOptional.isPresent()) {
                logger.warn("Invalid role name in token: {}", roleName);
            }
            return roleOptional;
        } catch (Exception e) {
            logger.error("Error converting role name to Role: {}", roleName, e);
            return Optional.empty();
        }
    }
}