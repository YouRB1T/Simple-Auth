package auth.services;

import auth.dto.LocalUserRegistrationDTO;
import auth.dto.OAuthUserRegistrationDTO;
import auth.entities.User;
import auth.repositories.UserRegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    @Autowired
    private UserRegistrationRepository registrationRepository;

    public User registerLocalUser(LocalUserRegistrationDTO registrationDTO) {
        logger.debug("Attempting to register local user with email: {}", registrationDTO.getEmail());
        return registrationRepository.registerLocalUser(registrationDTO);
    }

    public User registerOAuthUser(OAuthUserRegistrationDTO registrationDTO) {
        logger.debug("Attempting to register OAuth user with email: {}", registrationDTO.getEmail());
        return registrationRepository.registerOAuthUser(registrationDTO);
    }
}