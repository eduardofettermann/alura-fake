package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class TaskController {
    private TaskRepository taskRepository;
    private CourseRepository courseRepository;

    @Autowired
    public TaskController(TaskRepository taskRepository, CourseRepository courseRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/task/new/opentext")
    public ResponseEntity newOpenTextExercise(@RequestBody @Valid NewTaskDTO newTaskDTO) {
        Optional<ResponseEntity<ErrorItemDTO>> taskErrorItemDTOResponse = validateTask(newTaskDTO);
        if (taskErrorItemDTOResponse.isPresent()) {
            return taskErrorItemDTOResponse.get();
        }

        Optional<Course> possibleCourse = courseRepository.findById(newTaskDTO.getCourseId());

        Optional<ResponseEntity<ErrorItemDTO>> courseErrorItemDTOResponse = validateCourseByCourseId(
                possibleCourse,
                newTaskDTO.getCourseId()
        );
        if (courseErrorItemDTOResponse.isPresent()) {
            return courseErrorItemDTOResponse.get();
        }

        Task task = new Task(possibleCourse.get(), Type.OPEN_TEXT, newTaskDTO.getOrder(), newTaskDTO.getStatement());
        taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("task/all")
    public ResponseEntity<List<TaskListItemDTO>> listAllTasksByCourseIdOrderByOrder() {
        List<TaskListItemDTO> tasks = taskRepository.findAll().stream()
                .map(TaskListItemDTO::new)
                .toList();

        return ResponseEntity.ok(tasks);
    }

    public Optional<ResponseEntity<ErrorItemDTO>> validateTask(NewTaskDTO newTaskDTO) {
        boolean existsWithTheSameCourseIdAndStatement = taskRepository.existsTasksByCourseIdAndByStatement(
                newTaskDTO.getCourseId(),
                newTaskDTO.getStatement()
        );

        if (existsWithTheSameCourseIdAndStatement) {
            String message = "Já existe uma tarefa com o enunciado "
                    .concat("'").concat(newTaskDTO.getStatement()).concat("'")
                    .concat(" vinculado ao curso com ID ")
                    .concat(newTaskDTO.getCourseId().toString());
            return Optional.of(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("statement", message)));
        }

        boolean isCorrectSequence = newTaskDTO.getCourseId() > taskRepository.findMaxOrderByCourseId(newTaskDTO.getCourseId());

        if (!isCorrectSequence) {
            return Optional.of(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("order", "A ordem inserida está fora de sequência")));
        }

        return Optional.empty();
    }

    private Optional<ResponseEntity<ErrorItemDTO>> validateCourseByCourseId(Optional<Course> possibleCourse, Long courseId) {
        String message;

        if (possibleCourse.isEmpty()) {
            message = "Um curso com o ID ".concat(courseId.toString()).concat(" não foi encontrado.");
            return Optional.of(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorItemDTO("courseId", message)));
        }

        if (!possibleCourse.get().isBuilding()) {
            message = "O curso com o ID ".concat(courseId.toString()).concat(" não está em construção.");
            return Optional.of(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("courseId", message)));
        }

        return Optional.empty();
    }
}