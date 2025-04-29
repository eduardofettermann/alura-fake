package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.alternative.dto.NewAlternativeDTO;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.task.model.TaskType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TaskRepository taskRepository;
    @MockBean
    private CourseRepository courseRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void newOpenTextExercise__should_return_not_found_when_course_with_id_doesnt_exists() throws Exception {
        NewOpenTextTaskDTO newOpenTextTaskDTO = new NewOpenTextTaskDTO();
        newOpenTextTaskDTO.setCourseId(1L);
        newOpenTextTaskDTO.setStatement("O que foi apresentado na Imersão Gemini?");
        newOpenTextTaskDTO.setOrder(1);

        doReturn(Optional.empty()).when(courseRepository).findById(newOpenTextTaskDTO.getCourseId());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("courseId"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_statement_length_is_smaller_than_4() throws Exception {
        NewOpenTextTaskDTO newOpenTextTaskDTO = new NewOpenTextTaskDTO();
        newOpenTextTaskDTO.setCourseId(1L);
        newOpenTextTaskDTO.setStatement("123");
        newOpenTextTaskDTO.setOrder(1);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field")
                        .value("statement"))
                .andExpect(jsonPath("$[0].message")
                        .isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_statement_length_is_grater_than_255() throws Exception {
        NewOpenTextTaskDTO newOpenTextTaskDTO = new NewOpenTextTaskDTO();
        newOpenTextTaskDTO.setCourseId(1L);
        String statement = "a".repeat(256);
        newOpenTextTaskDTO.setStatement(statement);
        newOpenTextTaskDTO.setOrder(1);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field")
                        .value("statement"))
                .andExpect(jsonPath("$[0].message")
                        .isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_statement_is_duplicated_by_course_id() throws Exception {
        Course course = mock(Course.class);
        NewOpenTextTaskDTO newOpenTextTaskDTO = new NewOpenTextTaskDTO();
        Long courseId = 1L;
        newOpenTextTaskDTO.setCourseId(courseId);
        String statement = "Aprenda testes unitários com JUnit";
        newOpenTextTaskDTO.setStatement(statement);
        newOpenTextTaskDTO.setOrder(1);

        doReturn(Optional.of(course)).when(courseRepository).findById(newOpenTextTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();
        when(taskRepository.existsTasksByCourseIdAndByStatement(courseId, statement)).thenReturn(true);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("statement"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_order_is_negative() throws Exception {
        NewOpenTextTaskDTO newOpenTextTaskDTO = new NewOpenTextTaskDTO();
        newOpenTextTaskDTO.setCourseId(1L);
        newOpenTextTaskDTO.setOrder(-1);
        newOpenTextTaskDTO.setStatement("Aprenda a criar uma aplicação com autenticação utilizando Spring Security com Alura");

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field")
                        .value("order"))
                .andExpect(jsonPath("$[0].message")
                        .isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_course_is_not_building() throws Exception {
        Course course = mock(Course.class);
        NewOpenTextTaskDTO newOpenTextTaskDTO = new NewOpenTextTaskDTO();
        newOpenTextTaskDTO.setCourseId(course.getId());
        newOpenTextTaskDTO.setOrder(1);
        newOpenTextTaskDTO.setStatement("Aprenda a criar uma aplicação com autenticação utilizando Spring Security com Alura");

        doReturn(Optional.of(course)).when(courseRepository).findById(newOpenTextTaskDTO.getCourseId());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("courseId"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_order_is_out_of_sequence() throws Exception {
        Course course = mock(Course.class);
        NewOpenTextTaskDTO newOpenTextTaskDTO = new NewOpenTextTaskDTO();
        newOpenTextTaskDTO.setCourseId(course.getId());
        newOpenTextTaskDTO.setStatement("Explique o que é KISS e as vantagens de sua utilização.");
        newOpenTextTaskDTO.setOrder(3);

        doReturn(Optional.of(course)).when(courseRepository).findById(newOpenTextTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();
        doReturn(1).when(taskRepository).findHighestOrderByCourseId(newOpenTextTaskDTO.getCourseId());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("order"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_hasnt_tasks_with_course_id_and_order_is_out_of_sequence() throws Exception {
        Course course = mock(Course.class);
        NewOpenTextTaskDTO newOpenTextTaskDTO = new NewOpenTextTaskDTO();
        newOpenTextTaskDTO.setCourseId(course.getId());
        newOpenTextTaskDTO.setStatement("Explique o que é KISS e as vantagens de sua utilização.");
        newOpenTextTaskDTO.setOrder(2);

        doReturn(Optional.of(course)).when(courseRepository).findById(newOpenTextTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();
        doReturn(null).when(taskRepository).findHighestOrderByCourseId(newOpenTextTaskDTO.getCourseId());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("order"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_reorder_tasks_and_return_created_when_order_is_smaller_than_greater_order() throws Exception {
        Course course = mock(Course.class);
        NewOpenTextTaskDTO newOpenTextTaskDTO = new NewOpenTextTaskDTO();
        newOpenTextTaskDTO.setCourseId(course.getId());
        newOpenTextTaskDTO.setStatement("Explique o que é KISS e as vantagens de sua utilização.");
        newOpenTextTaskDTO.setOrder(2);
        Task mockitoTask = new Task(
                course,
                TaskType.OPEN_TEXT,
                2,
                "Qual framework é amplamente utilizado para mockar dados no Java?"
        );
        Task springBootTask = new Task(
                course,
                TaskType.SINGLE_CHOICE,
                3,
                "Qual desses frameworks serve para auxiliar na criação de REST APIs?");
        List<Task> tasks = List.of(mockitoTask, springBootTask);

        doReturn(Optional.of(course)).when(courseRepository).findById(newOpenTextTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();
        when(taskRepository.existsTasksByCourseIdAndByStatement(
                newOpenTextTaskDTO.getCourseId(),
                newOpenTextTaskDTO.getStatement()
        )).thenReturn(false);
        doReturn(2).when(taskRepository).findHighestOrderByCourseId(newOpenTextTaskDTO.getCourseId());
        when(taskRepository.existsTasksByCourseIdAndByOrder(
                newOpenTextTaskDTO.getCourseId(),
                newOpenTextTaskDTO.getOrder())
        ).thenReturn(true);

        when(taskRepository.findByCourseIdAndOrderGreaterThanEqualForUpdate(
                newOpenTextTaskDTO.getCourseId(),
                newOpenTextTaskDTO.getOrder()
        )).thenReturn(tasks);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isCreated());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void newOpenTextExercise__should_return_created_when_new_task_request_is_valid() throws Exception {
        Course course = mock(Course.class);
        NewOpenTextTaskDTO newOpenTextTaskDTO = new NewOpenTextTaskDTO();
        newOpenTextTaskDTO.setCourseId(course.getId());
        newOpenTextTaskDTO.setOrder(1);
        newOpenTextTaskDTO.setStatement("Aprenda a criar uma aplicação com autenticação utilizando Spring Security com Alura");

        doReturn(Optional.of(course)).when(courseRepository).findById(newOpenTextTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();
        doReturn(null).when(taskRepository).findHighestOrderByCourseId(newOpenTextTaskDTO.getCourseId());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isCreated());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void newSingleChoice__should_return_bad_request_when_any_option_length_is_smaller_than_4() throws Exception {
        Course course = mock(Course.class);
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = new NewSingleChoiceTaskDTO();
        newSingleChoiceTaskDTO.setCourseId(course.getId());
        newSingleChoiceTaskDTO.setOrder(1);
        newSingleChoiceTaskDTO.setStatement("O que aprendemos hoje?");

        NewAlternativeDTO alternativeWithOptionInvalid = new NewAlternativeDTO("123", true);
        NewAlternativeDTO java = new NewAlternativeDTO("Java 21", false);
        NewAlternativeDTO spring = new NewAlternativeDTO("Spring", false);
        java.setIncorrect();

        newSingleChoiceTaskDTO.setOptions(List.of(alternativeWithOptionInvalid, java, spring));

        doReturn(Optional.of(course)).when(courseRepository).findById(newSingleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleChoiceTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field")
                        .value("options[0].option"))
                .andExpect(jsonPath("$[0].message")
                        .isNotEmpty());
    }

    @Test
    void newSingleChoice__should_return_bad_request_when_any_option_length_is_greater_than_80() throws Exception {
        Course course = mock(Course.class);
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = new NewSingleChoiceTaskDTO();
        newSingleChoiceTaskDTO.setCourseId(course.getId());
        newSingleChoiceTaskDTO.setOrder(1);
        newSingleChoiceTaskDTO.setStatement("O que aprendemos hoje?");

        NewAlternativeDTO alternativeWithOptionInvalid = new NewAlternativeDTO("fiap".repeat(21), false);
        NewAlternativeDTO java = new NewAlternativeDTO("Java 21", false);
        NewAlternativeDTO spring = new NewAlternativeDTO("Spring", false);

        newSingleChoiceTaskDTO.setOptions(List.of(alternativeWithOptionInvalid, java, spring));

        doReturn(Optional.of(course)).when(courseRepository).findById(newSingleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleChoiceTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field")
                        .value("options[0].option"))
                .andExpect(jsonPath("$[0].message")
                        .isNotEmpty());
    }

    @Test
    void newSingleChoice__should_return_bad_request_when_task_has_no_more_than_two_options() throws Exception {
        Course course = mock(Course.class);
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = new NewSingleChoiceTaskDTO();
        newSingleChoiceTaskDTO.setCourseId(course.getId());
        newSingleChoiceTaskDTO.setOrder(1);
        newSingleChoiceTaskDTO.setStatement("O que aprendemos hoje?");

        NewAlternativeDTO alternative = new NewAlternativeDTO("Redis", true);

        newSingleChoiceTaskDTO.setOptions(List.of(alternative));

        doReturn(Optional.of(course)).when(courseRepository).findById(newSingleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleChoiceTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field")
                        .value("options"))
                .andExpect(jsonPath("$[0].message")
                        .isNotEmpty());
    }

    @Test
    void newSingleChoice__should_return_bad_request_when_task_has_more_than_five_options() throws Exception {
        Course course = mock(Course.class);
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = new NewSingleChoiceTaskDTO();
        newSingleChoiceTaskDTO.setCourseId(course.getId());
        newSingleChoiceTaskDTO.setOrder(1);
        newSingleChoiceTaskDTO.setStatement("O que aprendemos hoje?");

        NewAlternativeDTO docker = new NewAlternativeDTO("Docker", true);
        NewAlternativeDTO java = new NewAlternativeDTO("Java 21", false);
        NewAlternativeDTO spring = new NewAlternativeDTO("Spring", false);
        NewAlternativeDTO elk = new NewAlternativeDTO("ElasticSearch, Kibana e Logstash", false);
        NewAlternativeDTO aws = new NewAlternativeDTO("Amazon Web Services", false);
        NewAlternativeDTO oci = new NewAlternativeDTO("Oracle Cloud Infrastructure", false);

        newSingleChoiceTaskDTO.setOptions(List.of(docker, java, spring, elk, aws, oci));

        doReturn(Optional.of(course)).when(courseRepository).findById(newSingleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleChoiceTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field")
                        .value("options"))
                .andExpect(jsonPath("$[0].message")
                        .isNotEmpty());
    }

    @Test
    void newSingleChoice__should_return_bad_request_when_task_has_more_than_one_correct_alternative() throws Exception {
        Course course = mock(Course.class);
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = new NewSingleChoiceTaskDTO();
        newSingleChoiceTaskDTO.setCourseId(course.getId());
        newSingleChoiceTaskDTO.setOrder(1);
        newSingleChoiceTaskDTO.setStatement("O que aprendemos hoje?");

        NewAlternativeDTO apiRest = new NewAlternativeDTO("API REST", true);
        NewAlternativeDTO bestPractices = new NewAlternativeDTO("Boas práticas", true);

        newSingleChoiceTaskDTO.setOptions(List.of(apiRest, bestPractices));

        doReturn(Optional.of(course)).when(courseRepository).findById(newSingleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleChoiceTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("options"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newSingleChoice__should_return_bad_request_when_task_has_alternatives_with_same_options() throws Exception {
        Course course = mock(Course.class);
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = new NewSingleChoiceTaskDTO();
        newSingleChoiceTaskDTO.setCourseId(course.getId());
        newSingleChoiceTaskDTO.setOrder(1);
        newSingleChoiceTaskDTO.setStatement("O que aprendemos hoje?");

        NewAlternativeDTO solid = new NewAlternativeDTO("Solid", true);
        NewAlternativeDTO duplicatedSolid = new NewAlternativeDTO(solid.option(), false);

        newSingleChoiceTaskDTO.setOptions(List.of(solid, duplicatedSolid));
        doReturn(Optional.of(course)).when(courseRepository).findById(newSingleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleChoiceTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("options"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newSingleChoice__should_return_bad_request_when_task_has_alternatives_with_options_equals_to_statement() throws Exception {
        Course course = mock(Course.class);
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = new NewSingleChoiceTaskDTO();
        newSingleChoiceTaskDTO.setCourseId(course.getId());
        newSingleChoiceTaskDTO.setOrder(1);
        newSingleChoiceTaskDTO.setStatement("Fazer deploy com Github Actions");

        NewAlternativeDTO deploy = new NewAlternativeDTO("Fazer deploy com Github Actions", true);
        NewAlternativeDTO codeReview = new NewAlternativeDTO("Code review", false);

        newSingleChoiceTaskDTO.setOptions(List.of(deploy, codeReview));
        doReturn(Optional.of(course)).when(courseRepository).findById(newSingleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleChoiceTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("options"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newSingleChoice__should_return_created_when_task_is_valid() throws Exception {
        Course course = mock(Course.class);
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = new NewSingleChoiceTaskDTO();
        newSingleChoiceTaskDTO.setCourseId(course.getId());
        newSingleChoiceTaskDTO.setOrder(1);
        newSingleChoiceTaskDTO.setStatement("O que aprendemos hoje?");

        NewAlternativeDTO tdd = new NewAlternativeDTO("Desenvolvimento orientado a testes", true);
        NewAlternativeDTO rabbitMQ = new NewAlternativeDTO("RabbitMQ", false);

        newSingleChoiceTaskDTO.setOptions(List.of(tdd, rabbitMQ));
        doReturn(Optional.of(course)).when(courseRepository).findById(newSingleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();
        doReturn(false).when(taskRepository).existsTasksByCourseIdAndByStatement(
                newSingleChoiceTaskDTO.getCourseId(),
                newSingleChoiceTaskDTO.getStatement()
        );
        doReturn(null).when(taskRepository).findHighestOrderByCourseId(newSingleChoiceTaskDTO.getCourseId());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleChoiceTaskDTO)))
                .andExpect(status().isCreated());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void newMultipleChoice__should_return_bad_request_when_any_option_length_is_smaller_than_4() throws Exception {
        Course course = mock(Course.class);
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new NewMultipleChoiceTaskDTO();
        newMultipleChoiceTaskDTO.setCourseId(course.getId());
        newMultipleChoiceTaskDTO.setOrder(1);
        newMultipleChoiceTaskDTO.setStatement("O que aprendemos hoje?");

        NewAlternativeDTO alternativeWithOptionInvalid = new NewAlternativeDTO("123", false);
        NewAlternativeDTO java = new NewAlternativeDTO("Java 21", true);
        NewAlternativeDTO spring = new NewAlternativeDTO("Spring", true);

        alternativeWithOptionInvalid.setIncorrect();
        java.setCorrect();
        spring.setCorrect();

        newMultipleChoiceTaskDTO.setOptions(List.of(alternativeWithOptionInvalid, java, spring));

        doReturn(Optional.of(course)).when(courseRepository).findById(newMultipleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field")
                        .value("options[0].option"))
                .andExpect(jsonPath("$[0].message")
                        .isNotEmpty());
    }

    @Test
    void newMultipleChoice__should_return_bad_request_when_any_option_length_is_greater_than_80() throws Exception {
        Course course = mock(Course.class);
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new NewMultipleChoiceTaskDTO();
        newMultipleChoiceTaskDTO.setCourseId(course.getId());
        newMultipleChoiceTaskDTO.setOrder(1);
        newMultipleChoiceTaskDTO.setStatement("O que aprendemos hoje?");

        NewAlternativeDTO alternativeWithOptionInvalid = new NewAlternativeDTO("caelum".repeat(14), false);
        NewAlternativeDTO java = new NewAlternativeDTO("Java 21", true);
        NewAlternativeDTO spring = new NewAlternativeDTO("Spring", true);

        alternativeWithOptionInvalid.setIncorrect();
        java.setCorrect();
        spring.setCorrect();

        newMultipleChoiceTaskDTO.setOptions(List.of(alternativeWithOptionInvalid, java, spring));

        doReturn(Optional.of(course)).when(courseRepository).findById(newMultipleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field")
                        .value("options[0].option"))
                .andExpect(jsonPath("$[0].message")
                        .isNotEmpty());
    }

    @Test
    void newMultipleChoice__should_return_bad_request_when_task_has_no_at_least_three_options() throws Exception {
        Course course = mock(Course.class);
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new NewMultipleChoiceTaskDTO();
        newMultipleChoiceTaskDTO.setCourseId(course.getId());
        newMultipleChoiceTaskDTO.setOrder(1);
        newMultipleChoiceTaskDTO.setStatement("O que aprendemos hoje?");

        NewAlternativeDTO alternative = new NewAlternativeDTO("Redis", true);
        NewAlternativeDTO otherAlternative = new NewAlternativeDTO("MySQL", false);

        newMultipleChoiceTaskDTO.setOptions(List.of(alternative, otherAlternative));

        doReturn(Optional.of(course)).when(courseRepository).findById(newMultipleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field")
                        .value("options"))
                .andExpect(jsonPath("$[0].message")
                        .isNotEmpty());
    }

    @Test
    void newMultipleChoice__should_return_bad_request_when_task_has_more_than_five_options() throws Exception {
        Course course = mock(Course.class);
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new NewMultipleChoiceTaskDTO();
        newMultipleChoiceTaskDTO.setCourseId(course.getId());
        newMultipleChoiceTaskDTO.setOrder(1);
        newMultipleChoiceTaskDTO.setStatement("O que aprendemos hoje?");

        NewAlternativeDTO java = new NewAlternativeDTO("Java 21",true);
        NewAlternativeDTO spring = new NewAlternativeDTO("Spring", true);
        NewAlternativeDTO docker = new NewAlternativeDTO("Docker", false);
        NewAlternativeDTO elk = new NewAlternativeDTO("ElasticSearch, Kibana e Logstash", false);
        NewAlternativeDTO aws = new NewAlternativeDTO("Amazon Web Services", false);
        NewAlternativeDTO oci = new NewAlternativeDTO("Oracle Cloud Infrastructure", false);

        newMultipleChoiceTaskDTO.setOptions(List.of(docker, java, spring, elk, aws, oci));

        doReturn(Optional.of(course)).when(courseRepository).findById(newMultipleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field")
                        .value("options"))
                .andExpect(jsonPath("$[0].message")
                        .isNotEmpty());
    }

    @Test
    void newMultipleChoice__should_return_bad_request_when_task_has_not_incorrect_alternative() throws Exception {
        Course course = mock(Course.class);
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new NewMultipleChoiceTaskDTO();
        newMultipleChoiceTaskDTO.setCourseId(course.getId());
        newMultipleChoiceTaskDTO.setOrder(1);
        newMultipleChoiceTaskDTO.setStatement("O que aprendemos hoje?");

        NewAlternativeDTO tdd = new NewAlternativeDTO("Desenvolvimento orientado a testes", true);
        NewAlternativeDTO junit = new NewAlternativeDTO("JUnit 5", true);
        NewAlternativeDTO mockito = new NewAlternativeDTO("Mockito", true);

        newMultipleChoiceTaskDTO.setOptions(List.of(tdd, junit, mockito));

        doReturn(Optional.of(course)).when(courseRepository).findById(newMultipleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("options"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newMultipleChoice__should_return_bad_request_when_task_has_alternatives_with_same_options() throws Exception {
        Course course = mock(Course.class);
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new NewMultipleChoiceTaskDTO();
        newMultipleChoiceTaskDTO.setCourseId(course.getId());
        newMultipleChoiceTaskDTO.setOrder(1);
        newMultipleChoiceTaskDTO.setStatement("O que aprendemos hoje?");

        NewAlternativeDTO solid = new NewAlternativeDTO("Solid", true);
        NewAlternativeDTO duplicatedSolid = new NewAlternativeDTO(solid.option(), true);
        NewAlternativeDTO otherDuplicatedSolid = new NewAlternativeDTO(solid.option(), false);

        newMultipleChoiceTaskDTO.setOptions(List.of(solid, duplicatedSolid, otherDuplicatedSolid));
        doReturn(Optional.of(course)).when(courseRepository).findById(newMultipleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("options"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newMultipleChoice__should_return_bad_request_when_task_has_alternatives_with_options_equals_to_statement() throws Exception {
        Course course = mock(Course.class);
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new NewMultipleChoiceTaskDTO();
        newMultipleChoiceTaskDTO.setCourseId(course.getId());
        newMultipleChoiceTaskDTO.setOrder(1);
        newMultipleChoiceTaskDTO.setStatement("Fazer deploy com Github Actions");

        NewAlternativeDTO codeReview = new NewAlternativeDTO("Code review", false);
        NewAlternativeDTO deploy = new NewAlternativeDTO("Fazer deploy com Github Actions", true);
        NewAlternativeDTO pairProgramming = new NewAlternativeDTO("Pair programming", true);

        newMultipleChoiceTaskDTO.setOptions(List.of(deploy, codeReview, pairProgramming));
        doReturn(Optional.of(course)).when(courseRepository).findById(newMultipleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("options"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newMultipleChoice__should_return_created_when_task_is_valid() throws Exception {
        Course course = mock(Course.class);
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new NewMultipleChoiceTaskDTO();
        newMultipleChoiceTaskDTO.setCourseId(course.getId());
        newMultipleChoiceTaskDTO.setOrder(1);
        newMultipleChoiceTaskDTO.setStatement("O que aprendemos hoje?");

        NewAlternativeDTO rabbitMQ = new NewAlternativeDTO("RabbitMQ", true);
        NewAlternativeDTO eda = new NewAlternativeDTO("Arquitetura orientada a eventos", true);
        NewAlternativeDTO tdd = new NewAlternativeDTO("Desenvolvimento orientado a testes", false);
        NewAlternativeDTO springSecurity = new NewAlternativeDTO("Spring Security", false);
        NewAlternativeDTO springCloud = new NewAlternativeDTO("Spring Cloud", false);

        newMultipleChoiceTaskDTO.setOptions(List.of(tdd, rabbitMQ, eda, springSecurity, springCloud));
        doReturn(Optional.of(course)).when(courseRepository).findById(newMultipleChoiceTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();
        doReturn(false).when(taskRepository).existsTasksByCourseIdAndByStatement(
                newMultipleChoiceTaskDTO.getCourseId(),
                newMultipleChoiceTaskDTO.getStatement()
        );
        doReturn(null).when(taskRepository).findHighestOrderByCourseId(newMultipleChoiceTaskDTO.getCourseId());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isCreated());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void listAllTasks__should_list_all_tasks() throws Exception {
        Course course = mock(Course.class);
        Task kissTask = new Task(course, TaskType.OPEN_TEXT, 1, "Explique o que é KISS e as vantagens de sua utilização.");
        Task yagniTask = new Task(course, TaskType.OPEN_TEXT, 2, "Explique o que é YAGNI e as vantagens da sua utilização.");
        Task dryTask = new Task(course, TaskType.OPEN_TEXT, 3, "Explique o que é DRY e as vantagens da sua utilização.");

        doReturn(true).when(course).isBuilding();

        when(taskRepository.findAll()).thenReturn(Arrays.asList(yagniTask, kissTask, dryTask));

        mockMvc.perform(get("/task/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statement").value(yagniTask.getStatement()))
                .andExpect(jsonPath("$[1].statement").value(kissTask.getStatement()))
                .andExpect(jsonPath("$[2].statement").value(dryTask.getStatement()));
    }
}
