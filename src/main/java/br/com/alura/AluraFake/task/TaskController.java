package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.dto.*;
import br.com.alura.AluraFake.task.model.Task;
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
@RequestMapping("/task")
@Tag(name = "Tarefas", description = "API para gerenciamento de tarefas e exercícios")
@SecurityRequirement(name = "bearer-key")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/new/opentext")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar exercício de texto aberto",
            description = "Cria uma nova tarefa do tipo texto aberto"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tarefa criada com sucesso",
                    content = @Content(schema = @Schema(implementation = Task.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Curso não encontrado"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Regras de negócio não atendidas"
            )
    })
    public Task newOpenTextExercise(@Valid @RequestBody NewOpenTextTaskDTO newOpenTextTaskDTO) {
        return taskService.newOpenTextExercise(newOpenTextTaskDTO);
    }

    @PostMapping("/new/singlechoice")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar exercício de escolha única",
            description = "Cria uma nova tarefa do tipo escolha única"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tarefa criada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Curso não encontrado"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Regras de negócio não atendidas"
            )
    })
    public void newSingleChoice(@Valid @RequestBody NewSingleChoiceTaskDTO newSingleChoiceTaskDTO) {
        taskService.newSingleChoice(newSingleChoiceTaskDTO);
    }

    @PostMapping("/new/multiplechoice")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar exercício de múltipla escolha",
            description = "Cria uma nova tarefa do tipo escolha única"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tarefa criada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Curso não encontrado"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Regras de negócio não atendidas"
            )
    })
    public void newMultipleChoice(@Valid @RequestBody NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO) {
        taskService.newMultipleChoice(newMultipleChoiceTaskDTO);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Listar todas as tarefas",
            description = "Retorna a lista de todas as tarefas cadastradas"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tarefas retornada com sucesso",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = TaskListItemDTO.class))
                    )
            )
    })
    public ResponseEntity<List<TaskListItemDTO>> listAllTasks() {
        List<TaskListItemDTO> tasks = taskService.listAllTasks();
        return ResponseEntity.ok(tasks);
    }
}