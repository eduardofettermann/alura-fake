package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
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
    void newTask__should_return_not_found_when_course_with_id_doesnt_exists() throws Exception {
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(1L);
        newTaskDTO.setStatement("O que foi apresentado na Imersão Gemini?");
        newTaskDTO.setOrder(1);
        newTaskDTO.setType(Type.OPEN_TEXT);

        doReturn(Optional.empty()).when(courseRepository).findById(newTaskDTO.getCourseId());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.field")
                        .value("courseId"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newTask__should_return_bad_request_when_statement_length_is_smaller_than_4() throws Exception {
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(1L);
        newTaskDTO.setStatement("123");
        newTaskDTO.setOrder(1);
        newTaskDTO.setType(Type.OPEN_TEXT);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field")
                        .value("statement"))
                .andExpect(jsonPath("$[0].message")
                        .isNotEmpty());
    }

    @Test
    void newTask__should_return_bad_request_when_statement_length_is_grater_than_255() throws Exception {
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(1L);
        String statement = "a".repeat(256);
        newTaskDTO.setStatement(statement);
        newTaskDTO.setOrder(1);
        newTaskDTO.setType(Type.OPEN_TEXT);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field")
                        .value("statement"))
                .andExpect(jsonPath("$[0].message")
                        .isNotEmpty());
    }

    @Test
    void newTask__should_return_bad_request_when_statement_is_duplicated_by_course_id() throws Exception {
        Course course = mock(Course.class);
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setType(Type.OPEN_TEXT);
        Long courseId = 1L;
        newTaskDTO.setCourseId(courseId);
        String statement = "Aprenda testes unitários com JUnit";
        newTaskDTO.setStatement(statement);
        newTaskDTO.setOrder(1);

        doReturn(Optional.of(course)).when(courseRepository).findById(newTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();
        when(taskRepository.existsTasksByCourseIdAndByStatement(courseId, statement)).thenReturn(true);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field")
                        .value("statement"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newTask__should_return_bad_request_when_order_is_negative() throws Exception {
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(1L);
        newTaskDTO.setOrder(-1);
        newTaskDTO.setType(Type.OPEN_TEXT);
        newTaskDTO.setStatement("Aprenda a criar uma aplicação com autenticação utilizando Spring Security com Alura");

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field")
                        .value("order"))
                .andExpect(jsonPath("$[0].message")
                        .isNotEmpty());
    }

    @Test
    void newTask__should_return_bad_request_when_course_is_not_building() throws Exception {
        Course course = mock(Course.class);
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(course.getId());
        newTaskDTO.setOrder(1);
        newTaskDTO.setType(Type.OPEN_TEXT);
        newTaskDTO.setStatement("Aprenda a criar uma aplicação com autenticação utilizando Spring Security com Alura");

        doReturn(Optional.of(course)).when(courseRepository).findById(newTaskDTO.getCourseId());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field")
                        .value("courseId"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newTask__should_return_bad_request_when_order_is_out_of_sequence() throws Exception {
        Course course = mock(Course.class);
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(course.getId());
        newTaskDTO.setStatement("Explique o que é KISS e as vantagens de sua utilização.");
        newTaskDTO.setType(Type.OPEN_TEXT);
        newTaskDTO.setOrder(3);

        doReturn(Optional.of(course)).when(courseRepository).findById(newTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();
        doReturn(1).when(taskRepository).findHighestOrderByCourseId(newTaskDTO.getCourseId());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field")
                        .value("order"))
                .andExpect(jsonPath("$.message")
                        .isNotEmpty());
    }

    @Test
    void newTask__should_reorder_tasks_and_return_created_when_order_is_smaller_than_greater_order() throws Exception {
        Course course = mock(Course.class);
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(course.getId());
        newTaskDTO.setStatement("Explique o que é KISS e as vantagens de sua utilização.");
        newTaskDTO.setType(Type.OPEN_TEXT);
        newTaskDTO.setOrder(2);
        Task mockitoTask = new Task(
                course,
                Type.OPEN_TEXT,
                2,
                "Qual framework é amplamente utilizado para mockar dados no Java?"
        );
        Task springBootTask = new Task(
                course,
                Type.SINGLE_CHOICE,
                3,
                "Qual desses frameworks serve para auxiliar na criação de REST APIs?");
        List<Task> tasks = List.of(mockitoTask, springBootTask);

        doReturn(Optional.of(course)).when(courseRepository).findById(newTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();
        when(taskRepository.existsTasksByCourseIdAndByStatement(
                newTaskDTO.getCourseId(),
                newTaskDTO.getStatement()
        )).thenReturn(false);
        doReturn(2).when(taskRepository).findHighestOrderByCourseId(newTaskDTO.getCourseId());
        when(taskRepository.existsTasksByCourseIdAndByOrder(
                newTaskDTO.getCourseId(),
                newTaskDTO.getOrder())
        ).thenReturn(true);

        when(taskRepository.findByCourseIdAndOrderGreaterThanEqualForUpdate(
                newTaskDTO.getCourseId(),
                newTaskDTO.getOrder()
        )).thenReturn(tasks);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isCreated());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void newTask__should_return_created_when_new_task_request_is_valid() throws Exception {
        Course course = mock(Course.class);
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(course.getId());
        newTaskDTO.setOrder(1);
        newTaskDTO.setType(Type.OPEN_TEXT);
        newTaskDTO.setStatement("Aprenda a criar uma aplicação com autenticação utilizando Spring Security com Alura");

        doReturn(Optional.of(course)).when(courseRepository).findById(newTaskDTO.getCourseId());
        doReturn(true).when(course).isBuilding();
        doReturn(null).when(taskRepository).findHighestOrderByCourseId(newTaskDTO.getCourseId());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isCreated());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void listAllTasks__should_list_all_tasks() throws Exception {
        Course course = mock(Course.class);
        Task kissTask = new Task(course, Type.OPEN_TEXT, 1, "Explique o que é KISS e as vantagens de sua utilização.");
        Task yagniTask = new Task(course, Type.OPEN_TEXT, 2, "Explique o que é YAGNI e as vantagens da sua utilização.");
        Task dryTask = new Task(course, Type.OPEN_TEXT, 3, "Explique o que é DRY e as vantagens da sua utilização.");

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
