package br.com.alura.AluraFake.user;

import br.com.alura.AluraFake.user.dto.NewUserDTO;
import br.com.alura.AluraFake.user.dto.UserListItemDTO;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "Usuários", description = "API para gerenciamento de usuários")
@SecurityRequirement(name = "bearer-key")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/new")
    @Operation(
            summary = "Cadastrar novo usuário",
            description = "Endpoint para criar um novo usuário no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário criado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos",
                    content = @Content(schema = @Schema(implementation = ErrorItemDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado"
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    public void newUser(@RequestBody @Valid NewUserDTO newUser) {
        userService.newStudent(newUser);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Listar todos usuários",
            description = "Retorna a lista de todos os usuários cadastrados"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuários retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = UserListItemDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado"
            )
    })
    public List<UserListItemDTO> listAllUsers() {
        return userService.listAllUsers();
    }
}
