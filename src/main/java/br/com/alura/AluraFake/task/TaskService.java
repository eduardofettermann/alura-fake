package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.alternative.model.Alternative;
import br.com.alura.AluraFake.alternative.dto.NewAlternativeDTO;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.exception.domain.*;
import br.com.alura.AluraFake.task.dto.*;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.task.model.TaskType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    public TaskService(TaskRepository taskRepository, CourseRepository courseRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Task newOpenTextExercise(NewOpenTextTaskDTO newOpenTextTaskDTO) {
        Course course = validateCourse(newOpenTextTaskDTO.getCourseId());
        validateTask(newOpenTextTaskDTO);

        Task task = new Task(course, TaskType.OPEN_TEXT, newOpenTextTaskDTO.getOrder(), newOpenTextTaskDTO.getStatement());
        taskRepository.save(task);
        return task;
    }

    @Transactional
    public void newSingleChoice(NewSingleChoiceTaskDTO newSingleChoiceTaskDTO) {
        Course course = validateCourse(newSingleChoiceTaskDTO.getCourseId());
        validateTask(newSingleChoiceTaskDTO);
        validateSingleChoice(newSingleChoiceTaskDTO);

        Task task = new Task(course, TaskType.SINGLE_CHOICE, newSingleChoiceTaskDTO.getOrder(), newSingleChoiceTaskDTO.getStatement());
        List<Alternative> alternatives = newSingleChoiceTaskDTO.getOptions().stream()
                .map(option -> new Alternative(task, option.option(), option.isCorrect()))
                .toList();
        task.setAlternatives(alternatives);

        taskRepository.save(task);
    }

    @Transactional
    public void newMultipleChoice(NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO) {
        Course course = validateCourse(newMultipleChoiceTaskDTO.getCourseId());
        validateTask(newMultipleChoiceTaskDTO);
        validateMultipleChoice(newMultipleChoiceTaskDTO);

        Task task = new Task(course, TaskType.MULTIPLE_CHOICE, newMultipleChoiceTaskDTO.getOrder(), newMultipleChoiceTaskDTO.getStatement());
        List<Alternative> alternatives = newMultipleChoiceTaskDTO.getOptions().stream()
                .map(option -> new Alternative(task, option.option(), option.isCorrect()))
                .toList();
        task.setAlternatives(alternatives);

        taskRepository.save(task);
    }

    public List<TaskListItemDTO> listAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskListItemDTO::new)
                .toList();
    }

    private Course validateCourse(Long courseId) {
        Optional<Course> possibleCourse = courseRepository.findById(courseId);
        if (possibleCourse.isEmpty()) {
            String message = String.format("Um curso com o ID %d não foi encontrado.", courseId);
            throw new CourseNotFoundException("courseId", message);
        }
        Course course = possibleCourse.get();
        if (!course.isBuilding()) {
            String message = String.format("O curso com o ID %d não está em construção.", courseId);
            throw new CourseIsNotBuildingException("courseId", message);
        }
        return course;
    }

    private void validateSingleChoice(NewSingleChoiceTaskDTO dto) {
        List<NewAlternativeDTO> options = dto.getOptions();

        long correctCount = options.stream().filter(NewAlternativeDTO::isCorrect).count();
        if (correctCount != 1) {
            throw new InvalidCorrectOrIncorrectAlternativesException("options", "A atividade deve ter exatamente uma alternativa correta.");
        }

        validateCommonAlternativeRules(options, dto.getStatement());
    }

    private void validateMultipleChoice(NewMultipleChoiceTaskDTO dto) {
        List<NewAlternativeDTO> options = dto.getOptions();

        long correctCount = options.stream().filter(NewAlternativeDTO::isCorrect).count();
        long incorrectCount = options.stream().filter(NewAlternativeDTO::isIncorrect).count();
        if (correctCount < 2 || incorrectCount < 1) {
            throw new InvalidCorrectOrIncorrectAlternativesException("options", "A atividade deve ter duas ou mais alternativas corretas e ao menos uma incorreta.");
        }

        validateCommonAlternativeRules(options, dto.getStatement());
    }

    private void validateCommonAlternativeRules(List<NewAlternativeDTO> options, String statement) {
        String optionsField = "options";

        Set<String> uniqueOptions = options.stream().map(NewAlternativeDTO::option).collect(Collectors.toSet());
        if (options.size() != uniqueOptions.size()) {
            throw new AlternativeOptionsMustBeUniqueException(optionsField);
        }

        boolean optionEqualsStatement = options.stream().anyMatch(opt -> opt.option().equals(statement));
        if (optionEqualsStatement) {
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
        int newOrder = newTaskDTO.getOrder();

        if (highestOrder == null && newOrder != 1) {
            throw new OutOfSequenceTaskOrderException("order");
        }

        if (highestOrder != null) {
            int expectedNextOrder = highestOrder + 1;
            if (newOrder > expectedNextOrder) {
                throw new OutOfSequenceTaskOrderException("order");
            }
            if (taskRepository.existsTasksByCourseIdAndByOrder(newTaskDTO.getCourseId(), newOrder)) {
                reorderTasks(newTaskDTO);
            }
        }
    }

    private void reorderTasks(NewTaskDTO newTaskDTO) {
        List<Task> tasksToReorder = taskRepository.findByCourseIdAndOrderGreaterThanEqualForUpdate(
                newTaskDTO.getCourseId(), newTaskDTO.getOrder());
        tasksToReorder.forEach(task -> task.setOrder(task.getOrder() + 1));
        taskRepository.saveAll(tasksToReorder);
    }
}