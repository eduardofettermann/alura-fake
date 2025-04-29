package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.alternative.dto.NewAlternativeDTO;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.model.CourseStatus;
import br.com.alura.AluraFake.exception.domain.*;
import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.TaskListItemDTO;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.task.model.TaskType;
import br.com.alura.AluraFake.user.model.Role;
import br.com.alura.AluraFake.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private CourseRepository courseRepository;
    @InjectMocks
    private TaskService taskService;

    private static NewOpenTextTaskDTO getValidNewOpenTextTaskDTO() {
        NewOpenTextTaskDTO newOpenTextTaskDTO = new NewOpenTextTaskDTO();
        newOpenTextTaskDTO.setCourseId(getValidCourse().getId());
        newOpenTextTaskDTO.setStatement("Explique o que é KISS e as vantagens de sua utilização.");
        newOpenTextTaskDTO.setOrder(1);

        return newOpenTextTaskDTO;
    }

    private static NewSingleChoiceTaskDTO getValidNewSingleChoiceTaskDTO() {
        NewAlternativeDTO typeScript = new NewAlternativeDTO("TypeScript", true);
        NewAlternativeDTO java = new NewAlternativeDTO("Java 21", false);
        NewAlternativeDTO spring = new NewAlternativeDTO("Spring", false);

        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = new NewSingleChoiceTaskDTO();
        newSingleChoiceTaskDTO.setCourseId(getValidCourse().getId());
        newSingleChoiceTaskDTO.setStatement("Explique o que é KISS e as vantagens de sua utilização.");
        newSingleChoiceTaskDTO.setOrder(1);
        newSingleChoiceTaskDTO.setOptions(List.of(typeScript, java, spring));
        return newSingleChoiceTaskDTO;
    }

    private static NewMultipleChoiceTaskDTO getValidNewMultipleChoiceTaskDTO() {
        NewAlternativeDTO java = new NewAlternativeDTO("Java 21", true);
        NewAlternativeDTO spring = new NewAlternativeDTO("Spring", true);
        NewAlternativeDTO elk = new NewAlternativeDTO("ElasticSearch, Kibana e Logstash", false);
        NewAlternativeDTO aws = new NewAlternativeDTO("Amazon Web Services", false);
        NewAlternativeDTO oci = new NewAlternativeDTO("Oracle Cloud Infrastructure", false);

        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new NewMultipleChoiceTaskDTO();
        newMultipleChoiceTaskDTO.setCourseId(getValidCourse().getId());
        newMultipleChoiceTaskDTO.setOrder(1);
        newMultipleChoiceTaskDTO.setStatement("O que aprendemos hoje?");
        newMultipleChoiceTaskDTO.setOptions(List.of(java, spring, elk, aws, oci));

        return newMultipleChoiceTaskDTO;
    }

    private static Course getValidCourse() {
        return new Course("Curso de Java", "Aprenda Java com Spring", getValidInstructor());
    }

    private static User getValidInstructor() {
        return new User("Eduardo", "eduardo@alura.com.br", Role.INSTRUCTOR);
    }
    
    @Test
    void newOpenTextExercise__should_throw_course_not_found_exception_when_course_with_id_doesnt_exists() {
        NewOpenTextTaskDTO newOpenTextTaskDTO = getValidNewOpenTextTaskDTO();
        
        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class, () -> {
            taskService.newOpenTextExercise(newOpenTextTaskDTO);
        });

        assertEquals("courseId", exception.getField());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void newOpenTextExercise__should_throw_course_is_not_building_exception_when_status_course_is_not_building() {
        Course course = getValidCourse();
        course.setStatus(CourseStatus.PUBLISHED);
        NewOpenTextTaskDTO newOpenTextTaskDTO = getValidNewOpenTextTaskDTO();

        doReturn(Optional.of(course)).when(courseRepository).findById(newOpenTextTaskDTO.getCourseId());

        CourseIsNotBuildingException exception = assertThrows(CourseIsNotBuildingException.class, () -> {
           taskService.newOpenTextExercise(newOpenTextTaskDTO);
        });
        assertEquals("courseId", exception.getField());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void newOpenTextExercise__should_throw_duplicate_task_statement_in_exception_when_statement_is_duplicated_by_course_id() {
        NewOpenTextTaskDTO newOpenTextTaskDTO = getValidNewOpenTextTaskDTO();

        when(courseRepository.findById(newOpenTextTaskDTO.getCourseId())).thenReturn(Optional.of(getValidCourse()));;
        when(taskRepository.existsTasksByCourseIdAndByStatement(
                newOpenTextTaskDTO.getCourseId(),
                newOpenTextTaskDTO.getStatement()
        )).thenReturn(true);
        DuplicateTaskStatementInException exception = assertThrows(DuplicateTaskStatementInException.class, () -> {
            taskService.newOpenTextExercise(newOpenTextTaskDTO);
        });

        assertEquals("statement", exception.getField());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void newOpenTextExercise__should_throw_out_of_sequence_task_order_exception_when_order_is_out_of_sequence() {
        NewOpenTextTaskDTO newOpenTextTaskDTO = getValidNewOpenTextTaskDTO();
        newOpenTextTaskDTO.setOrder(4);

        when(courseRepository.findById(newOpenTextTaskDTO.getCourseId())).thenReturn(Optional.of(getValidCourse()));
        when(taskRepository.existsTasksByCourseIdAndByStatement(
                newOpenTextTaskDTO.getCourseId(),
                newOpenTextTaskDTO.getStatement()
        )).thenReturn(false);
        when(taskRepository.findHighestOrderByCourseId(newOpenTextTaskDTO.getCourseId())).thenReturn(1);
        OutOfSequenceTaskOrderException exception = assertThrows(OutOfSequenceTaskOrderException.class, () -> {
            taskService.newOpenTextExercise(newOpenTextTaskDTO);
        });

        assertEquals("order", exception.getField());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void newOpenTextExercise__should_throw_out_of_sequence_task_order_exception_when_hasnt_tasks_with_course_id_and_order_is_out_of_sequence() {
        NewOpenTextTaskDTO newOpenTextTaskDTO = getValidNewOpenTextTaskDTO();
        newOpenTextTaskDTO.setOrder(4);

        when(courseRepository.findById(newOpenTextTaskDTO.getCourseId())).thenReturn(Optional.of(getValidCourse()));
        when(taskRepository.existsTasksByCourseIdAndByStatement(
                newOpenTextTaskDTO.getCourseId(),
                newOpenTextTaskDTO.getStatement()
        )).thenReturn(false);
        when(taskRepository.findHighestOrderByCourseId(newOpenTextTaskDTO.getCourseId())).thenReturn(null);
        OutOfSequenceTaskOrderException exception = assertThrows(OutOfSequenceTaskOrderException.class, () -> {
            taskService.newOpenTextExercise(newOpenTextTaskDTO);
        });

        assertEquals("order", exception.getField());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void newOpenTextExercise__should_reorder_tasks_and_save_task_when_order_is_smaller_than_greater_order() {
        Task taskAlreadyExistent = new Task(
                getValidCourse(),
                TaskType.OPEN_TEXT,
                1,
                "O que aprendemos hoje?"
        );
        Task otherTaskExistentToo = new Task(
                getValidCourse(),
                TaskType.OPEN_TEXT,
                2,
                "Quais tecnologias usamos nesse treinamento?"
        );
        List<Task> tasksToReorder = List.of(taskAlreadyExistent, otherTaskExistentToo);

        NewOpenTextTaskDTO newOpenTextTaskDTO = getValidNewOpenTextTaskDTO();
        newOpenTextTaskDTO.setOrder(2);

        when(courseRepository.findById(newOpenTextTaskDTO.getCourseId())).thenReturn(Optional.of(getValidCourse()));
        when(taskRepository.existsTasksByCourseIdAndByStatement(
                newOpenTextTaskDTO.getCourseId(),
                newOpenTextTaskDTO.getStatement()
        )).thenReturn(false);
        when(taskRepository.findHighestOrderByCourseId(newOpenTextTaskDTO.getCourseId())).thenReturn(2);
        when(taskRepository.existsTasksByCourseIdAndByOrder(
                newOpenTextTaskDTO.getCourseId(),
                newOpenTextTaskDTO.getOrder())
        ).thenReturn(true);
        when(taskRepository.findByCourseIdAndOrderGreaterThanEqualForUpdate(
                newOpenTextTaskDTO.getCourseId(),
                newOpenTextTaskDTO.getOrder()
        )).thenReturn(tasksToReorder);
        taskService.newOpenTextExercise(newOpenTextTaskDTO);

        verify(taskRepository, times(1)).saveAll(tasksToReorder);
    }

    @Test
    void newOpenTextExercise__should_save_task_when_dto_is_valid() {
        NewOpenTextTaskDTO newOpenTextTaskDTO = getValidNewOpenTextTaskDTO();

        when(courseRepository.findById(newOpenTextTaskDTO.getCourseId())).thenReturn(Optional.of(getValidCourse()));;
        doReturn(null).when(taskRepository).findHighestOrderByCourseId(newOpenTextTaskDTO.getCourseId());
        taskService.newOpenTextExercise(newOpenTextTaskDTO);

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void newSingleChoice__should_throw_invalid_correct_or_incorrect_alternatives_exception_when_task_has_more_than_one_correct_alternative() {
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = getValidNewSingleChoiceTaskDTO();
        NewAlternativeDTO apiRest = new NewAlternativeDTO("API REST", true);
        NewAlternativeDTO bestPractices = new NewAlternativeDTO("Boas práticas", true);
        newSingleChoiceTaskDTO.setOptions(List.of(apiRest, bestPractices));

        when(courseRepository.findById(newSingleChoiceTaskDTO.getCourseId()))
                .thenReturn(Optional.of(getValidCourse()));

        when(taskRepository.findHighestOrderByCourseId(newSingleChoiceTaskDTO.getCourseId())).thenReturn(null);
        InvalidCorrectOrIncorrectAlternativesException exception = assertThrows(InvalidCorrectOrIncorrectAlternativesException.class, () -> {
            taskService.newSingleChoice(newSingleChoiceTaskDTO);
        });

        assertEquals("options", exception.getField());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void newSingleChoice__should__throw_alternative_options_must_be_unique_exception_when_task_has_alternatives_with_same_options() {
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = getValidNewSingleChoiceTaskDTO();
        NewAlternativeDTO solid = new NewAlternativeDTO("Solid", true);
        NewAlternativeDTO duplicatedSolid = new NewAlternativeDTO(solid.option(), false);
        newSingleChoiceTaskDTO.setOptions(List.of(solid, duplicatedSolid));

        when(courseRepository.findById(newSingleChoiceTaskDTO.getCourseId()))
                .thenReturn(Optional.of(getValidCourse()));
        when(taskRepository.findHighestOrderByCourseId(newSingleChoiceTaskDTO.getCourseId())).thenReturn(null);
        AlternativeOptionsMustBeUniqueException exception = assertThrows(AlternativeOptionsMustBeUniqueException.class, () -> {
            taskService.newSingleChoice(newSingleChoiceTaskDTO);
        });

        assertEquals("options", exception.getField());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void newSingleChoice__should_throw_unprocessable_entity_when_task_has_alternatives_with_options_equals_to_statement() {
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = getValidNewSingleChoiceTaskDTO();
        newSingleChoiceTaskDTO.setStatement(newSingleChoiceTaskDTO.getOptions().getFirst().option());

        when(courseRepository.findById(newSingleChoiceTaskDTO.getCourseId()))
                .thenReturn(Optional.of(getValidCourse()));
        when(taskRepository.findHighestOrderByCourseId(newSingleChoiceTaskDTO.getCourseId())).thenReturn(null);
        AlternativeOptionEqualsTaskStatementException exception = assertThrows(AlternativeOptionEqualsTaskStatementException.class, () -> {
            taskService.newSingleChoice(newSingleChoiceTaskDTO);
        });

        assertEquals("options", exception.getField());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void newSingleChoice__should_save_task_when_task_is_valid() {
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = getValidNewSingleChoiceTaskDTO();

        when(courseRepository.findById(newSingleChoiceTaskDTO.getCourseId()))
                .thenReturn(Optional.of(getValidCourse()));
        doReturn(false).when(taskRepository).existsTasksByCourseIdAndByStatement(
                newSingleChoiceTaskDTO.getCourseId(),
                newSingleChoiceTaskDTO.getStatement()
        );
        doReturn(null).when(taskRepository).findHighestOrderByCourseId(newSingleChoiceTaskDTO.getCourseId());

        taskService.newSingleChoice(newSingleChoiceTaskDTO);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void newMultipleChoice__should_throw_invalid_correct_or_incorrect_alternatives_exception_when_task_has_not_two_correct_alternatives() {
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = getValidNewMultipleChoiceTaskDTO();
        NewAlternativeDTO tdd = new NewAlternativeDTO("Desenvolvimento orientado a testes", false);
        NewAlternativeDTO junit = new NewAlternativeDTO("JUnit 5", false);
        NewAlternativeDTO mockito = new NewAlternativeDTO("Mockito", true);
        newMultipleChoiceTaskDTO.setOptions(List.of(tdd, junit, mockito));

        when(courseRepository.findById(newMultipleChoiceTaskDTO.getCourseId()))
                .thenReturn(Optional.of(getValidCourse()));
        doReturn(false).when(taskRepository).existsTasksByCourseIdAndByStatement(
                newMultipleChoiceTaskDTO.getCourseId(),
                newMultipleChoiceTaskDTO.getStatement()
        );
        doReturn(null).when(taskRepository).findHighestOrderByCourseId(newMultipleChoiceTaskDTO.getCourseId());

        InvalidCorrectOrIncorrectAlternativesException exception = assertThrows(InvalidCorrectOrIncorrectAlternativesException.class, () -> {
            taskService.newMultipleChoice(newMultipleChoiceTaskDTO);
        });

        assertEquals("options", exception.getField());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void newMultipleChoice__should_throw_invalid_correct_or_incorrect_alternatives_exception_when_task_has_not_incorrect_alternative() {
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = getValidNewMultipleChoiceTaskDTO();
        NewAlternativeDTO tdd = new NewAlternativeDTO("Desenvolvimento orientado a testes", true);
        NewAlternativeDTO junit = new NewAlternativeDTO("JUnit 5", true);
        NewAlternativeDTO mockito = new NewAlternativeDTO("Mockito", true);
        newMultipleChoiceTaskDTO.setOptions(List.of(tdd, junit, mockito));

        when(courseRepository.findById(newMultipleChoiceTaskDTO.getCourseId()))
                .thenReturn(Optional.of(getValidCourse()));
        when(taskRepository.existsTasksByCourseIdAndByStatement(
                newMultipleChoiceTaskDTO.getCourseId(),
                newMultipleChoiceTaskDTO.getStatement()
        )).thenReturn(false);
        when(taskRepository.findHighestOrderByCourseId(newMultipleChoiceTaskDTO.getCourseId())).thenReturn(null);

        InvalidCorrectOrIncorrectAlternativesException exception = assertThrows(InvalidCorrectOrIncorrectAlternativesException.class, () -> {
            taskService.newMultipleChoice(newMultipleChoiceTaskDTO);
        });

        assertEquals("options", exception.getField());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void newMultipleChoice__should_throw_alternative_options_must_be_unique_exception_when_task_has_alternatives_with_same_options() {
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = getValidNewMultipleChoiceTaskDTO();
        NewAlternativeDTO solid = new NewAlternativeDTO("Solid", true);
        NewAlternativeDTO duplicatedSolid = new NewAlternativeDTO(solid.option(), true);
        NewAlternativeDTO otherDuplicatedSolid = new NewAlternativeDTO(solid.option(), false);
        newMultipleChoiceTaskDTO.setOptions(List.of(solid, duplicatedSolid, otherDuplicatedSolid));

        when(courseRepository.findById(newMultipleChoiceTaskDTO.getCourseId()))
                .thenReturn(Optional.of(getValidCourse()));
        doReturn(false).when(taskRepository).existsTasksByCourseIdAndByStatement(
                newMultipleChoiceTaskDTO.getCourseId(),
                newMultipleChoiceTaskDTO.getStatement()
        );
        doReturn(null).when(taskRepository).findHighestOrderByCourseId(newMultipleChoiceTaskDTO.getCourseId());

        AlternativeOptionsMustBeUniqueException exception = assertThrows(AlternativeOptionsMustBeUniqueException.class, () -> {
            taskService.newMultipleChoice(newMultipleChoiceTaskDTO);
        });

        assertEquals("options", exception.getField());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void newMultipleChoice__should_throw_alternative_option_equals_task_statement_exception_when_task_has_alternatives_with_options_equals_to_statement() {
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = getValidNewMultipleChoiceTaskDTO();
        newMultipleChoiceTaskDTO.setStatement(newMultipleChoiceTaskDTO.getOptions().getFirst().option());

        doReturn(Optional.of(getValidCourse())).when(courseRepository).findById(newMultipleChoiceTaskDTO.getCourseId());

        doReturn(false).when(taskRepository).existsTasksByCourseIdAndByStatement(
                newMultipleChoiceTaskDTO.getCourseId(),
                newMultipleChoiceTaskDTO.getStatement()
        );
        doReturn(null).when(taskRepository).findHighestOrderByCourseId(newMultipleChoiceTaskDTO.getCourseId());

        AlternativeOptionEqualsTaskStatementException exception = assertThrows(AlternativeOptionEqualsTaskStatementException.class, () -> {
            taskService.newMultipleChoice(newMultipleChoiceTaskDTO);
        });

        assertEquals("options", exception.getField());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void newMultipleChoice__should_return_created_when_task_is_valid() {
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = getValidNewMultipleChoiceTaskDTO();

        when(courseRepository.findById(newMultipleChoiceTaskDTO.getCourseId()))
                .thenReturn(Optional.of(getValidCourse()));

        doReturn(false).when(taskRepository).existsTasksByCourseIdAndByStatement(
                newMultipleChoiceTaskDTO.getCourseId(),
                newMultipleChoiceTaskDTO.getStatement()
        );
        doReturn(null).when(taskRepository).findHighestOrderByCourseId(newMultipleChoiceTaskDTO.getCourseId());
        taskService.newMultipleChoice(newMultipleChoiceTaskDTO);

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void listAllTasks__should_list_all_tasks() {
        Task yagniTask = new Task(getValidCourse(), TaskType.OPEN_TEXT, 2, "Explique o que é YAGNI e as vantagens da sua utilização.");
        Task dryTask = new Task(getValidCourse(), TaskType.OPEN_TEXT, 3, "Explique o que é DRY e as vantagens da sua utilização.");
        List<Task> tasks = Arrays.asList(yagniTask, dryTask);
        List<TaskListItemDTO> expectedTasks = List.of(new TaskListItemDTO(yagniTask), new TaskListItemDTO(dryTask));

        when(taskRepository.findAll()).thenReturn(tasks);

        List<TaskListItemDTO> resultTasks = taskService.listAllTasks();

        assertEquals(expectedTasks, resultTasks);
    }
}
