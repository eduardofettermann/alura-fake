package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.dto.CourseListItemDTO;
import br.com.alura.AluraFake.course.dto.NewCourseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
@Tag(name = "Cursos", description = "API para gerenciamento de cursos")
@SecurityRequirement(name = "bearer-key")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar novo curso",
            description = "Endpoint para criar um novo curso no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Curso criado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Instrutor não encontrado"
            )
    })
    public void createCourse(@Valid @RequestBody NewCourseDTO newCourse) {
        courseService.createCourse(newCourse);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Listar todos os cursos",
            description = "Retorna a lista de todos os cursos cadastrados"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de cursos retornada com sucesso",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = CourseListItemDTO.class))
                    )
            )
    })
    public ResponseEntity<List<CourseListItemDTO>> createCourse() {
        List<CourseListItemDTO> courses = courseService.listAllCourses();
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/{id}/publish")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Publicar um curso",
            description = "Publica um curso específico identificado pelo ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Curso publicado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Curso não encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Regras de negócio não atendidas"
            )
    })
    public void publishCourse(@PathVariable("id") Long id) {
        courseService.publishCourse(id);
    }

}
