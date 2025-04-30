package br.com.alura.AluraFake;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Página inicial", description = "Interface gráfica para verificar os usuário e cursos criados")
@SecurityRequirement(name = "bearer-key")
public class HomeController {

    @GetMapping
    public String home() {
        return """
            <h1>Bem vindo ao teste de java Alura</h1>
            <ul>
                <li><a href="/user/all">Usuários cadastrados</a></li>
                <li><a href="/course/all">Cursos cadastrados</a></li>
            </ul>
            """;
    }
}
