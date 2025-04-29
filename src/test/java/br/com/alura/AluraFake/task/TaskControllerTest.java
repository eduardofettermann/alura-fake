package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.alternative.dto.NewAlternativeDTO;
import br.com.alura.AluraFake.exception.domain.*;
import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.TaskListItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private static NewOpenTextTaskDTO getValidNewOpenTextTaskDTO() {
        NewOpenTextTaskDTO newOpenTextTaskDTO = new NewOpenTextTaskDTO();
        newOpenTextTaskDTO.setCourseId(1L);
        newOpenTextTaskDTO.setStatement("Explique o que é KISS e as vantagens de sua utilização.");
        newOpenTextTaskDTO.setOrder(3);

        return newOpenTextTaskDTO;
    }

    private static NewSingleChoiceTaskDTO getValidNewSingleChoiceTaskDTO() {
        NewAlternativeDTO typeScript = new NewAlternativeDTO("TypeScript", true);
        NewAlternativeDTO java = new NewAlternativeDTO("Java 21", false);
        NewAlternativeDTO spring = new NewAlternativeDTO("Spring", false);

        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = new NewSingleChoiceTaskDTO();
        newSingleChoiceTaskDTO.setCourseId(1L);
        newSingleChoiceTaskDTO.setStatement("Explique o que é KISS e as vantagens de sua utilização.");
        newSingleChoiceTaskDTO.setOrder(3);
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
        newMultipleChoiceTaskDTO.setCourseId(1L);
        newMultipleChoiceTaskDTO.setOrder(1);
        newMultipleChoiceTaskDTO.setStatement("O que aprendemos hoje?");
        newMultipleChoiceTaskDTO.setOptions(List.of(java, spring, elk, aws, oci));

        return newMultipleChoiceTaskDTO;
    }

    @Test
    void createOpenTextTask_should_return_unprocessable_entity_when_course_not_found() throws Exception {
        NewOpenTextTaskDTO newOpenTextTaskDTO = getValidNewOpenTextTaskDTO();
        doThrow(new CourseNotFoundException("courseId", "Course not found"))
                .when(taskService).newOpenTextExercise(any(NewOpenTextTaskDTO.class));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_return_unprocessable_entity_when_statement_is_duplicated_by_course_id() throws Exception {
        NewOpenTextTaskDTO newOpenTextTaskDTO = getValidNewOpenTextTaskDTO();
        String expectedExceptionMessage = String.format("Já existe uma tarefa com o enunciado '%s' vinculado ao curso com ID %d",
                newOpenTextTaskDTO.getStatement(),
                newOpenTextTaskDTO.getCourseId()
        );

        doThrow(new DuplicateTaskStatementInException("statement", expectedExceptionMessage))
                .when(taskService).newOpenTextExercise(any(NewOpenTextTaskDTO.class));

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
    void newOpenTextExercise__should_return_unprocessable_entity_when_course_is_not_building() throws Exception {
        NewOpenTextTaskDTO newOpenTextTaskDTO = getValidNewOpenTextTaskDTO();
        String expectedExceptionMessage = String.format("O curso com o ID %d não está em construção.", newOpenTextTaskDTO.getCourseId());

        doThrow(new CourseIsNotBuildingException("courseId", expectedExceptionMessage)).when(taskService).newOpenTextExercise(any(NewOpenTextTaskDTO.class));

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
    void newOpenTextExercise__should_return_unprocessable_entity_when_order_is_out_of_sequence() throws Exception {
        NewOpenTextTaskDTO newOpenTextTaskDTO = getValidNewOpenTextTaskDTO();

        doThrow(new OutOfSequenceTaskOrderException("order"))
                .when(taskService)
                .newOpenTextExercise(any(NewOpenTextTaskDTO.class));

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
    void newOpenTextExercise_should_return_created_when_valid() throws Exception {
        NewOpenTextTaskDTO newOpenTextTaskDTO = getValidNewOpenTextTaskDTO();

        when(taskService.newOpenTextExercise(any(NewOpenTextTaskDTO.class))).thenReturn(null);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void newSingleChoice_should_return_unprocessable_entity_when_course_not_found() throws Exception {
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = getValidNewSingleChoiceTaskDTO();

        doThrow(new CourseNotFoundException("courseId", "Course not found"))
                .when(taskService).newSingleChoice(any(NewSingleChoiceTaskDTO.class));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleChoiceTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void newSingleChoice__should_return_unprocessable_entity_when_statement_is_duplicated_by_course_id() throws Exception {
        NewSingleChoiceTaskDTO invalidTask = getValidNewSingleChoiceTaskDTO();
        String expectedExceptionMessage = String.format("Já existe uma tarefa com o enunciado '%s' vinculado ao curso com ID %d",
                invalidTask.getStatement(),
                invalidTask.getCourseId()
        );

        doThrow(new DuplicateTaskStatementInException("statement", expectedExceptionMessage))
                .when(taskService).newSingleChoice(any(NewSingleChoiceTaskDTO.class));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("statement"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newSingleChoice__should_return_unprocessable_entity_when_course_is_not_building() throws Exception {
        NewSingleChoiceTaskDTO taskNotInBuilding = getValidNewSingleChoiceTaskDTO();
        String expectedExceptionMessage = String.format("O curso com o ID %d não está em construção.", taskNotInBuilding.getCourseId());

        doThrow(new CourseIsNotBuildingException("courseId", expectedExceptionMessage)).when(taskService).newSingleChoice(any(NewSingleChoiceTaskDTO.class));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskNotInBuilding)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("courseId"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newSingleChoice__should_return_unprocessable_entity_when_order_is_out_of_sequence() throws Exception {
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = getValidNewSingleChoiceTaskDTO();

        doThrow(new OutOfSequenceTaskOrderException("order"))
                .when(taskService).newSingleChoice(any(NewSingleChoiceTaskDTO.class));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleChoiceTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("order"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newSingleChoice_should_return_unprocessable_entity_when_options_invalid() throws Exception {
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = getValidNewSingleChoiceTaskDTO();

        doThrow(new InvalidCorrectOrIncorrectAlternativesException(
                "options",
                "A atividade deve ter exatamente uma alternativa correta."
        )).when(taskService).newSingleChoice(any(NewSingleChoiceTaskDTO.class));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleChoiceTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field").value("options"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void newSingleChoice_should_return_unprocessable_entity_when_duplicate_options() throws Exception {
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = getValidNewSingleChoiceTaskDTO();

        doThrow(new AlternativeOptionsMustBeUniqueException("options"))
                .when(taskService).newSingleChoice(any(NewSingleChoiceTaskDTO.class));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleChoiceTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field").value("options"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void newSingleChoice_should_return_created_when_valid() throws Exception {
        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO = getValidNewSingleChoiceTaskDTO();

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleChoiceTaskDTO)))
                .andExpect(status().isCreated());

        verify(taskService, times(1)).newSingleChoice(any(NewSingleChoiceTaskDTO.class));
    }

    @Test
    void newMultipleChoice_should_return_unprocessable_entity_when_course_not_found() throws Exception {
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = getValidNewMultipleChoiceTaskDTO();

        doThrow(new CourseNotFoundException("courseId", "Course not found"))
                .when(taskService).newMultipleChoice(any(NewMultipleChoiceTaskDTO.class));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void newMultipleChoice__should_return_unprocessable_entity_when_statement_is_duplicated_by_course_id() throws Exception {
        NewMultipleChoiceTaskDTO invalidTask = getValidNewMultipleChoiceTaskDTO();
        String expectedExceptionMessage = String.format("Já existe uma tarefa com o enunciado '%s' vinculado ao curso com ID %d",
                invalidTask.getStatement(),
                invalidTask.getCourseId()
        );

        doThrow(new DuplicateTaskStatementInException("statement", expectedExceptionMessage))
                .when(taskService).newMultipleChoice(any(NewMultipleChoiceTaskDTO.class));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("statement"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newMultipleChoice__should_return_unprocessable_entity_when_course_is_not_building() throws Exception {
        NewMultipleChoiceTaskDTO taskNotInBuilding = getValidNewMultipleChoiceTaskDTO();
        String expectedExceptionMessage = String.format("O curso com o ID %d não está em construção.", taskNotInBuilding.getCourseId());

        doThrow(new CourseIsNotBuildingException("courseId", expectedExceptionMessage)).when(taskService).newMultipleChoice(any(NewMultipleChoiceTaskDTO.class));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskNotInBuilding)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("courseId"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newMultipleChoice__should_return_unprocessable_entity_when_order_is_out_of_sequence() throws Exception {
        NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO = getValidNewMultipleChoiceTaskDTO();

        doThrow(new OutOfSequenceTaskOrderException("order")).when(taskService).newMultipleChoice(any(NewMultipleChoiceTaskDTO.class));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field")
                        .value("order"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newMultipleChoice__should_return_unprocessable_entity_when_options_invalid() throws Exception {
        NewMultipleChoiceTaskDTO taskDTO = getValidNewMultipleChoiceTaskDTO();

        doThrow(new InvalidCorrectOrIncorrectAlternativesException(
                "options",
                "A atividade deve ter duas ou mais alternativas corretas e ao menos uma incorreta."
        )).when(taskService).newMultipleChoice(any(NewMultipleChoiceTaskDTO.class));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field").value("options"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void newMultipleChoice_should_return_created_when_valid() throws Exception {
        NewMultipleChoiceTaskDTO taskDTO = getValidNewMultipleChoiceTaskDTO();

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isCreated());

        verify(taskService, times(1)).newMultipleChoice(any(NewMultipleChoiceTaskDTO.class));
    }

    @Test
    void listAllTasks_should_return_task_list() throws Exception {
        TaskListItemDTO yagniTask = new TaskListItemDTO(1L, "Explique o que é YAGNI e as vantagens da sua utilização.", 1);
        TaskListItemDTO dryTask = new TaskListItemDTO(1L, "Explique o que é DRY e as vantagens da sua utilização.", 2);

        when(taskService.listAllTasks()).thenReturn(Arrays.asList(yagniTask, dryTask));

        mockMvc.perform(get("/task/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statement").value(yagniTask.statement()))
                .andExpect(jsonPath("$[1].statement").value(dryTask.statement()));
    }
}