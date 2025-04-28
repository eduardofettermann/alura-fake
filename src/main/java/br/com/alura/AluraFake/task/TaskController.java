package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.alternative.NewAlternativeDTO;
import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Transactional
    public ResponseEntity newOpenTextExercise(@RequestBody @Valid NewTaskDTO newTaskDTO) {
        Optional<Course> possibleCourse = courseRepository.findById(newTaskDTO.getCourseId());
        Optional<ResponseEntity<ErrorItemDTO>> possibleCourseErrorItemDTOResponse = validateCourseByCourseId(
                possibleCourse,
                newTaskDTO.getCourseId()
        );

        if (possibleCourseErrorItemDTOResponse.isPresent()) {
            return possibleCourseErrorItemDTOResponse.get();
        }

        Optional<ResponseEntity<ErrorItemDTO>> possibleTaskErrorItemDTOResponse = validateTask(newTaskDTO);

        if (possibleTaskErrorItemDTOResponse.isPresent()) {
            return possibleTaskErrorItemDTOResponse.get();
        }

        Task task = new Task(possibleCourse.get(), Type.OPEN_TEXT, newTaskDTO.getOrder(), newTaskDTO.getStatement());
        taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice(@RequestBody @Valid NewSingleChoiceTaskDTO newSingleChoiceTaskDTO) {
        Optional<Course> possibleCourse = courseRepository.findById(newSingleChoiceTaskDTO.getCourseId());
        Optional<ResponseEntity<ErrorItemDTO>> possibleCourseErrorItemDTOResponse = validateCourseByCourseId(
                possibleCourse,
                newSingleChoiceTaskDTO.getCourseId()
        );

        if (possibleCourseErrorItemDTOResponse.isPresent()) {
            return possibleCourseErrorItemDTOResponse.get();
        }

        Optional<ResponseEntity<ErrorItemDTO>> possibleTaskErrorItemDTOResponse = validateTask(newSingleChoiceTaskDTO);

        if (possibleTaskErrorItemDTOResponse.isPresent()) {
            return possibleTaskErrorItemDTOResponse.get();
        }

        Optional<ResponseEntity<ErrorItemDTO>> possibleOptionsErrorItemDTOResponse = validateSingleChoice(newSingleChoiceTaskDTO);
        if (possibleOptionsErrorItemDTOResponse.isPresent()) {
            return possibleOptionsErrorItemDTOResponse.get();
        }

        return ResponseEntity.ok().build();
    }

    private Optional<ResponseEntity<ErrorItemDTO>> validateSingleChoice(@Valid NewSingleChoiceTaskDTO newSingleChoiceTaskDTO) {
        List<NewAlternativeDTO> options = newSingleChoiceTaskDTO.getOptions();
        boolean hasMoreOneCorrectAlternative = options.stream()
                .filter(NewAlternativeDTO::isCorrect)
                .count() > 1;

        if (hasMoreOneCorrectAlternative) {
            return buildErrorResponse(
                    "options",
                    "A atividade deve ter apenas uma única alternativa correta.",
                    HttpStatus.BAD_REQUEST);
        }

        Set<String> optionsWithoutRepetition = options.stream()
                .map(NewAlternativeDTO::getOption)
                .collect(Collectors.toSet());

        if (options.size() != optionsWithoutRepetition.size()) {
            return buildErrorResponse("options", "As alternativas não podem ser iguais entre si", HttpStatus.BAD_REQUEST);
        }

        return Optional.empty();
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

    private Optional<ResponseEntity<ErrorItemDTO>> validateTask(NewTaskDTO newTaskDTO) {
        if (taskRepository.existsTasksByCourseIdAndByStatement(newTaskDTO.getCourseId(), newTaskDTO.getStatement())) {
            String message = String.format("Já existe uma tarefa com o enunciado '%s' vinculado ao curso com ID %d",
                    newTaskDTO.getStatement(), newTaskDTO.getCourseId());
            return buildErrorResponse("statement", message, HttpStatus.BAD_REQUEST);
        }

        return validateOrder(newTaskDTO);
    }

    private Optional<ResponseEntity<ErrorItemDTO>> validateOrder(NewTaskDTO newTaskDTO) {
        Integer highestOrder = taskRepository.findHighestOrderByCourseId(newTaskDTO.getCourseId());

        if (highestOrder == null && newTaskDTO.getOrder() != 1) {
            return buildErrorResponse("order", "A ordem inserida está fora de sequência", HttpStatus.BAD_REQUEST);
        }

        if (highestOrder != null) {
            int expectedNextOrder = highestOrder + 1;
            int newOrder = newTaskDTO.getOrder();

            if (newOrder > expectedNextOrder) {
                return buildErrorResponse("order", "A ordem inserida está fora de sequência", HttpStatus.BAD_REQUEST);
            }

            if (taskRepository.existsTasksByCourseIdAndByOrder(newTaskDTO.getCourseId(), newOrder)) {
                reorderTasks(newTaskDTO);
            }
        }

        return Optional.empty();
    }

    private void reorderTasks(NewTaskDTO newTaskDTO) {
        List<Task> tasksToReorder = taskRepository.findByCourseIdAndOrderGreaterThanEqualForUpdate(
                newTaskDTO.getCourseId(), newTaskDTO.getOrder()
        );

        tasksToReorder.forEach(task -> task.setOrder(task.getOrder() + 1));

        taskRepository.saveAll(tasksToReorder);
    }

    private Optional<ResponseEntity<ErrorItemDTO>> validateCourseByCourseId(Optional<Course> possibleCourse, Long courseId) {
        if (possibleCourse.isEmpty()) {
            String message = String.format("Um curso com o ID %d não foi encontrado.", courseId);
            return buildErrorResponse("courseId", message, HttpStatus.NOT_FOUND);
        }

        if (!possibleCourse.get().isBuilding()) {
            String message = String.format("O curso com o ID %d não está em construção.", courseId);
            return buildErrorResponse("courseId", message, HttpStatus.BAD_REQUEST);
        }

        return Optional.empty();
    }

    private Optional<ResponseEntity<ErrorItemDTO>> buildErrorResponse(String field, String message, HttpStatus status) {
        return Optional.of(ResponseEntity.status(status).body(new ErrorItemDTO(field, message)));
    }
}