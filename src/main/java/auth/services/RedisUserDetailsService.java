package auth.services;

import auth.daos.RoleDAO;
import auth.entities.Role;
import auth.entities.User;
import auth.repositories.UserRedisRepository;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Getter
@RequiredArgsConstructor
@Service
public class RedisUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserRedisRepository userRedisRepository;
    @Autowired
    private final TokenService tokenService;
    private final RoleDAO roleDAO;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String userToken = userRedisRepository.findTokenByUsername(username);

        if (userToken == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        try {
            return tokenService.createUserFromToken(userToken);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Error parsing user data", e);
        }
    }

    public long getUserID(String token) {
        return Long.parseLong(tokenService.verifyToken(token).getSubject());
    }

    public Set<Role> getUserRoles(String token) {
        Set<Role> roles = new HashSet<>();

        try {
            Claims claims = tokenService.verifyToken(token);
            org.hibernate.mapping.List rolesClaim = claims.get("roles", org.hibernate.mapping.List.class);

            if (rolesClaim == null) {
                return Collections.emptySet();
            }

            Iterator<?> iterator = rolesClaim.getColumns().iterator();
            while (iterator.hasNext()) {
                Object roleObj = iterator.next();
                String roleName = roleObj.toString();

                Optional<Role> roleOptional = convertToRole(roleName);
                if (roleOptional.isPresent()) {
                    roles.add(roleOptional.get());
                }
            }
            return roles;
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    private Optional<Role> convertToRole(String roleName) {
        try {
            return roleDAO.findByName(roleName);
        } catch (Exception e) {
            System.out.println("Invalid role name in token: {}" + roleName);
            return Optional.empty();
        }
    }
}
