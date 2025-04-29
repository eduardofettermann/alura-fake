package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.dto.CourseListItemDTO;
import br.com.alura.AluraFake.course.dto.NewCourseDTO;
import br.com.alura.AluraFake.course.model.CourseStatus;
import br.com.alura.AluraFake.exception.domain.CourseIsNotBuildingException;
import br.com.alura.AluraFake.exception.domain.CourseNotFoundException;
import br.com.alura.AluraFake.exception.domain.MissingRequiredTaskTypesException;
import br.com.alura.AluraFake.exception.forbidden.NotAnInstructorException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCourse_should_return_bad_request_when_dto_is_invalid() throws Exception {
        NewCourseDTO invalidCourse = new NewCourseDTO("Java", "Java", "invalid-email");

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourse)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("emailInstructor"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void createCourse_should_return_forbidden_when_instructor_not_found() throws Exception {
        NewCourseDTO newCourse = new NewCourseDTO("Java", "Java", "paulo@alura.com.br");

        doThrow(new NotAnInstructorException("emailInstructor"))
                .when(courseService).createCourse(any(NewCourseDTO.class));

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourse)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.field").value("emailInstructor"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void createCourse_should_return_created_when_valid() throws Exception {
        NewCourseDTO newCourse = new NewCourseDTO("Java", "Java", "paulo@alura.com.br");

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourse)))
                .andExpect(status().isCreated());

        verify(courseService, times(1)).createCourse(any(NewCourseDTO.class));
    }

    @Test
    void listAllCourses_should_return_course_list() throws Exception {
        CourseListItemDTO expectedFirstCourse = new CourseListItemDTO(1L, "Java", "Curso de Java", CourseStatus.BUILDING);
        CourseListItemDTO expectedLastCourse = new CourseListItemDTO(2L, "Hibernate", "Curso de Hibernate", CourseStatus.BUILDING);
        List<CourseListItemDTO> courses = Arrays.asList(expectedFirstCourse, expectedLastCourse);
        when(courseService.listAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/course/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedFirstCourse.id()))
                .andExpect(jsonPath("$[0].title").value(expectedFirstCourse.title()))
                .andExpect(jsonPath("$[0].description").value(expectedFirstCourse.description()))
                .andExpect(jsonPath("$[0].status").value(expectedFirstCourse.status().toString()))
                .andExpect(jsonPath("$[1].id").value(expectedLastCourse.id()))
                .andExpect(jsonPath("$[1].title").value(expectedLastCourse.title()))
                .andExpect(jsonPath("$[1].description").value(expectedLastCourse.description()))
                .andExpect(jsonPath("$[1].status").value(expectedLastCourse.status().toString()));
    }

    @Test
    void publishCourse_should_return_unprocessable_entity_when_course_not_found() throws Exception {
        Long courseId = 1L;
        doThrow(new CourseNotFoundException("id", "Course not found"))
                .when(courseService).publishCourse(courseId);

        mockMvc.perform(post("/course/" + courseId + "/publish"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field").value("id"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void publishCourse_should_return_unprocessable_entity_when_missing_tasks_with_other_types() throws Exception {
        Long courseId = 1L;
        doThrow(new MissingRequiredTaskTypesException("tasks"))
                .when(courseService).publishCourse(courseId);

        mockMvc.perform(post("/course/" + courseId + "/publish"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field").value("tasks"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void publishCourse_should_return_unprocessable_entity_when_not_building() throws Exception {
        Long courseId = 1L;
        doThrow(new CourseIsNotBuildingException("status", "Course is not in building status"))
                .when(courseService).publishCourse(courseId);

        mockMvc.perform(post("/course/" + courseId + "/publish"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field").value("status"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void publishCourse_should_return_ok_when_valid() throws Exception {
        Long courseId = 1L;

        mockMvc.perform(post("/course/" + courseId + "/publish"))
                .andExpect(status().isOk());

        verify(courseService, times(1)).publishCourse(courseId);
    }
}