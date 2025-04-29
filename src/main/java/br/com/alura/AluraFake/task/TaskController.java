package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.alternative.model.Alternative;
import br.com.alura.AluraFake.alternative.dto.NewAlternativeDTO;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.exception.domain.*;
import br.com.alura.AluraFake.task.dto.*;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.task.model.TaskType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
    public ResponseEntity newOpenTextExercise(@RequestBody @Valid NewOpenTextTaskDTO newOpenTextTaskDTO) {
        Optional<Course> possibleCourse = courseRepository.findById(newOpenTextTaskDTO.getCourseId());

        validateCourseByCourseId(possibleCourse, newOpenTextTaskDTO.getCourseId());
        validateTask(newOpenTextTaskDTO);

        Task task = new Task(possibleCourse.get(), TaskType.OPEN_TEXT, newOpenTextTaskDTO.getOrder(), newOpenTextTaskDTO.getStatement());
        taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PostMapping("/task/new/singlechoice")
    @Transactional
    public ResponseEntity newSingleChoice(@RequestBody @Valid NewSingleChoiceTaskDTO newSingleChoiceTaskDTO) {
        Optional<Course> possibleCourse = courseRepository.findById(newSingleChoiceTaskDTO.getCourseId());

        validateCourseByCourseId(possibleCourse, newSingleChoiceTaskDTO.getCourseId());
        validateTask(newSingleChoiceTaskDTO);
        validateSingleChoice(newSingleChoiceTaskDTO);

        Task task = new Task(
                possibleCourse.get(),
                TaskType.SINGLE_CHOICE,
                newSingleChoiceTaskDTO.getOrder(),
                newSingleChoiceTaskDTO.getStatement()
        );

        List<Alternative> alternatives = newSingleChoiceTaskDTO.getOptions().stream()
                .map(option -> new Alternative(task, option.option(), option.isCorrect()))
                .toList();
        task.setAlternatives(alternatives);

        taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/task/new/multiplechoice")
    @Transactional
    public ResponseEntity newMultipleChoice(@RequestBody @Valid NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO) {
        Optional<Course> possibleCourse = courseRepository.findById(newMultipleChoiceTaskDTO.getCourseId());

        validateCourseByCourseId(possibleCourse, newMultipleChoiceTaskDTO.getCourseId());
        validateTask(newMultipleChoiceTaskDTO);
        validateMultipleChoice(newMultipleChoiceTaskDTO);

        Task task = new Task(
                possibleCourse.get(),
                TaskType.MULTIPLE_CHOICE,
                newMultipleChoiceTaskDTO.getOrder(),
                newMultipleChoiceTaskDTO.getStatement()
        );

        List<Alternative> alternatives = newMultipleChoiceTaskDTO.getOptions().stream()
                .map(option -> new Alternative(task, option.option(), option.isCorrect()))
                .toList();
        task.setAlternatives(alternatives);

        taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("task/all")
    public ResponseEntity<List<TaskListItemDTO>> listAllTasksByCourseIdOrderByOrder() {
        List<TaskListItemDTO> tasks = taskRepository.findAll().stream()
                .map(TaskListItemDTO::new)
                .toList();

        return ResponseEntity.ok(tasks);
    }

    private void validateSingleChoice(@Valid NewSingleChoiceTaskDTO newSingleChoiceTaskDTO) {
        String optionsField = "options";
        List<NewAlternativeDTO> options = newSingleChoiceTaskDTO.getOptions();
        boolean hasMoreOneCorrectAlternative = options.stream()
                .filter(NewAlternativeDTO::isCorrect)
                .count() > 1;

        if (hasMoreOneCorrectAlternative) {
            throw new InvalidCorrectOrIncorrectAlternativesException("options", "A atividade deve ter apenas uma única alternativa correta.");
        }

        Set<String> optionsWithoutRepetition = options.stream()
                .map(NewAlternativeDTO::option)
                .collect(Collectors.toSet());

        if (options.size() != optionsWithoutRepetition.size()) {
            throw new AlternativeOptionsMustBeUniqueException(optionsField);
        }

        String statement = newSingleChoiceTaskDTO.getStatement();
        boolean someOptionIsEqualToStatement = options.stream()
                .anyMatch(option -> Objects.equals(option.option(), statement));

        if (someOptionIsEqualToStatement) {
            throw new AlternativeOptionEqualsTaskStatementException(optionsField);
        }
    }

    private void validateMultipleChoice(@Valid NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO) {
        String optionsField = "options";
        List<NewAlternativeDTO> options = newMultipleChoiceTaskDTO.getOptions();

        boolean hasMoreOneCorrectAlternative = options.stream()
                .filter(NewAlternativeDTO::isCorrect)
                .count() > 1;
        if (!hasMoreOneCorrectAlternative) {
            throw new InvalidCorrectOrIncorrectAlternativesException(optionsField, "A atividade deve ter duas ou mais alternativas corretas, e ao menos uma alternativa incorreta");
        }

        boolean hasOneIncorrectAlternative = options.stream()
                .anyMatch(NewAlternativeDTO::isIncorrect);
        if (!hasOneIncorrectAlternative) {
            throw new InvalidCorrectOrIncorrectAlternativesException(optionsField, "A atividade deve ter duas ou mais alternativas corretas, e ao menos uma alternativa incorreta");
        }

        Set<String> optionsWithoutRepetition = options.stream()
                .map(NewAlternativeDTO::option)
                .collect(Collectors.toSet());

        if (options.size() != optionsWithoutRepetition.size()) {
            throw new AlternativeOptionsMustBeUniqueException(optionsField);
        }

        String statement = newMultipleChoiceTaskDTO.getStatement();
        boolean someOptionIsEqualToStatement = options.stream()
                .anyMatch(option -> Objects.equals(option.option(), statement));

        if (someOptionIsEqualToStatement) {
            throw new AlternativeOptionEqualsTaskStatementException(optionsField);
        }
    }

    private void validateTask(NewTaskDTO newTaskDTO) {
        if (taskRepository.existsTasksByCourseIdAndByStatement(newTaskDTO.getCourseId(), newTaskDTO.getStatement())) {
            String message = String.format("Já existe uma tarefa com o enunciado '%s' vinculado ao curso com ID %d",
                    newTaskDTO.getStatement(), newTaskDTO.getCourseId());
            throw new DuplicateTaskStatementInException("statement", message);
        }

        validateOrder(newTaskDTO);
    }

    private void validateOrder(NewTaskDTO newTaskDTO) {
        Integer highestOrder = taskRepository.findHighestOrderByCourseId(newTaskDTO.getCourseId());

        if (highestOrder == null && newTaskDTO.getOrder() != 1) {
            throw new OutOfSequenceTaskOrderException("order", "A ordem inserida está fora de sequência");
        }

        if (highestOrder != null) {
            int expectedNextOrder = highestOrder + 1;
            int newOrder = newTaskDTO.getOrder();

            if (newOrder > expectedNextOrder) {
                throw new OutOfSequenceTaskOrderException("order", "A ordem inserida está fora de sequência");
            }

            if (taskRepository.existsTasksByCourseIdAndByOrder(newTaskDTO.getCourseId(), newOrder)) {
                reorderTasks(newTaskDTO);
            }
        }
    }

    private void reorderTasks(NewTaskDTO newTaskDTO) {
        List<Task> tasksToReorder = taskRepository.findByCourseIdAndOrderGreaterThanEqualForUpdate(
                newTaskDTO.getCourseId(), newTaskDTO.getOrder()
        );

        tasksToReorder.forEach(task -> task.setOrder(task.getOrder() + 1));

        taskRepository.saveAll(tasksToReorder);
    }

    private void validateCourseByCourseId(Optional<Course> possibleCourse, Long courseId) {
        if (possibleCourse.isEmpty()) {
            String message = String.format("Um curso com o ID %d não foi encontrado.", courseId);
            throw new CourseNotFoundException("courseId", message);
        }

        if (!possibleCourse.get().isBuilding()) {
            String message = String.format("O curso com o ID %d não está em construção.", courseId);
            throw new CourseIsNotBuildingException("courseId", message);
        }
    }
}