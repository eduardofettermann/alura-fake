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

import java.util.Optional;

import static org.mockito.Mockito.*;
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
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setType(Type.OPEN_TEXT);
        Long courseId = 1L;
        newTaskDTO.setCourseId(courseId);
        String statement = "Aprenda testes unitários com JUnit";
        newTaskDTO.setStatement(statement);
        newTaskDTO.setOrder(1);
        System.out.println(objectMapper.writeValueAsString(newTaskDTO));

        when(taskRepository.existsTasksByCourseIdAndByStatement(courseId, statement)).thenReturn(true);
        System.out.println(objectMapper.writeValueAsString(newTaskDTO));

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
}
