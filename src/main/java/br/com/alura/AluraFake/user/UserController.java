package br.com.alura.AluraFake.user;

import br.com.alura.AluraFake.user.dto.NewUserDTO;
import br.com.alura.AluraFake.user.dto.UserListItemDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void newUser(@RequestBody @Valid NewUserDTO newUser) {
        userService.newStudent(newUser);
    }

    @GetMapping
    public List<UserListItemDTO> listAllUsers() {
        return userService.listAllUsers();
    }
}
