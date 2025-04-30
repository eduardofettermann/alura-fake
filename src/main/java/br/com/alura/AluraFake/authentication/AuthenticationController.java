package br.com.alura.AluraFake.authentication;

import br.com.alura.AluraFake.exception.EmailAlreadyRegisteredException;
import br.com.alura.AluraFake.exception.EmailOrPasswordInvalidException;
import br.com.alura.AluraFake.user.dto.UserAuthenticationDTO;
import br.com.alura.AluraFake.user.dto.UserLoginResponseDTO;
import br.com.alura.AluraFake.user.dto.UserRegisterDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody @Valid UserAuthenticationDTO userAuthenticationDTO) throws EmailOrPasswordInvalidException {
        Optional<UserLoginResponseDTO> userLoginResponseDTO = authenticationService.login(userAuthenticationDTO);
        if (userLoginResponseDTO.isPresent()) {
            return ResponseEntity.ok().body(userLoginResponseDTO.get());
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new UserLoginResponseDTO("Invalid credentials"));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) throws EmailAlreadyRegisteredException {
        authenticationService.register(userRegisterDTO);
    }
}
