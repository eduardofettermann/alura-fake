package br.com.alura.AluraFake.authentication;

import br.com.alura.AluraFake.exception.EmailAlreadyRegisteredException;
import br.com.alura.AluraFake.exception.EmailOrPasswordInvalidException;
import br.com.alura.AluraFake.infra.security.TokenService;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.user.dto.UserAuthenticationDTO;
import br.com.alura.AluraFake.user.dto.UserLoginResponseDTO;
import br.com.alura.AluraFake.user.dto.UserRegisterDTO;
import br.com.alura.AluraFake.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public AuthenticationService(AuthenticationManager authenticationManager, UserRepository userRepository, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public Optional<UserLoginResponseDTO> login(UserAuthenticationDTO userAuthenticationDTO) throws EmailOrPasswordInvalidException {
        if (!userRepository.existsByEmail(userAuthenticationDTO.email())) {
            throw new EmailOrPasswordInvalidException();
        }

        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(
                userAuthenticationDTO.email(),
                userAuthenticationDTO.password()
        );

        Authentication authenticated = authenticationManager.authenticate(usernamePassword);
        String token = tokenService.generateToken((User) authenticated.getPrincipal());

        return Optional.of(new UserLoginResponseDTO(token));
    }

    @Transactional
    public void register(UserRegisterDTO userRegisterDTO) throws EmailAlreadyRegisteredException {
        if (userRepository.existsByEmail(userRegisterDTO.email())) {
            throw new EmailAlreadyRegisteredException();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(userRegisterDTO.password());
        User user = new User(
                userRegisterDTO.name(),
                userRegisterDTO.email(),
                userRegisterDTO.role(),
                encryptedPassword
        );

        userRepository.save(user);
    }
}
