package br.com.alura.AluraFake.authentication;

import br.com.alura.AluraFake.exception.EmailAlreadyRegisteredException;
import br.com.alura.AluraFake.exception.EmailOrPasswordInvalidException;
import br.com.alura.AluraFake.user.dto.UserAuthenticationDTO;
import br.com.alura.AluraFake.user.dto.UserLoginResponseDTO;
import br.com.alura.AluraFake.user.dto.UserRegisterDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Tag(name = "Autenticação", description = "API para autenticação e registro de usuários")
@RequestMapping("auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login de usuário",
            description = "Autentica um usuário e retorna um token JWT"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticação bem-sucedida",
                    content = @Content(schema = @Schema(implementation = UserLoginResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais inválidas"
            )
    })
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
    @Operation(
            summary = "Registrar usuário",
            description = "Cadastra um novo usuário no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário registrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email já cadastrado"
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) throws EmailAlreadyRegisteredException {
        authenticationService.register(userRegisterDTO);
    }
}
