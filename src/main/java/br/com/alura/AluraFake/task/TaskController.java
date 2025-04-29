package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.dto.*;
import br.com.alura.AluraFake.task.model.Task;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/new/opentext")
    @ResponseStatus(HttpStatus.CREATED)
    public Task newOpenTextExercise(@Valid @RequestBody NewOpenTextTaskDTO newOpenTextTaskDTO) {
        return taskService.newOpenTextExercise(newOpenTextTaskDTO);
    }

    @PostMapping("/new/singlechoice")
    @ResponseStatus(HttpStatus.CREATED)
    public void newSingleChoice(@Valid @RequestBody NewSingleChoiceTaskDTO newSingleChoiceTaskDTO) {
        taskService.newSingleChoice(newSingleChoiceTaskDTO);
    }

    @PostMapping("/new/multiplechoice")
    @ResponseStatus(HttpStatus.CREATED)
    public void newMultipleChoice(@Valid @RequestBody NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO) {
        taskService.newMultipleChoice(newMultipleChoiceTaskDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TaskListItemDTO>> listAllTasks() {
        List<TaskListItemDTO> tasks = taskService.listAllTasks();
        return ResponseEntity.ok(tasks);
    }
}