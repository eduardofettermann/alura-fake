package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.dto.NewCourseDTO;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.user.model.Role;
import br.com.alura.AluraFake.user.model.User;
import br.com.alura.AluraFake.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CourseRepository courseRepository;
    @MockBean
    private TaskRepository taskRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void newCourseDTO__should_return_bad_request_when_email_is_invalid() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");
        newCourseDTO.setEmailInstructor("paulo@alura.com.br");

        doReturn(Optional.empty()).when(userRepository)
                .findByEmail(newCourseDTO.getEmailInstructor());

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("emailInstructor"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }


    @Test
    void newCourseDTO__should_return_bad_request_when_email_is_no_instructor() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");
        newCourseDTO.setEmailInstructor("paulo@alura.com.br");

        User user = mock(User.class);
        doReturn(false).when(user).isInstructor();

        doReturn(Optional.of(user)).when(userRepository)
                .findByEmail(newCourseDTO.getEmailInstructor());

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("emailInstructor"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void newCourseDTO__should_return_created_when_new_course_request_is_valid() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");
        newCourseDTO.setEmailInstructor("paulo@alura.com.br");

        User user = mock(User.class);
        doReturn(true).when(user).isInstructor();

        doReturn(Optional.of(user)).when(userRepository).findByEmail(newCourseDTO.getEmailInstructor());

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isCreated());

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void listAllCourses__should_list_all_courses() throws Exception {
        User paulo = new User("Paulo", "paulo@alua.com.br", Role.INSTRUCTOR);

        Course java = new Course("Java", "Curso de java", paulo);
        Course hibernate = new Course("Hibernate", "Curso de hibernate", paulo);
        Course spring = new Course("Spring", "Curso de spring", paulo);

        when(courseRepository.findAll()).thenReturn(Arrays.asList(java, hibernate, spring));

        mockMvc.perform(get("/course/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java"))
                .andExpect(jsonPath("$[0].description").value("Curso de java"))
                .andExpect(jsonPath("$[1].title").value("Hibernate"))
                .andExpect(jsonPath("$[1].description").value("Curso de hibernate"))
                .andExpect(jsonPath("$[2].title").value("Spring"))
                .andExpect(jsonPath("$[2].description").value("Curso de spring"));
    }

    @Test
    void publishCourse__should_return_not_found_when_course_not_exists() throws Exception {
        Long courseIdMocked = 1L;
        doReturn(Optional.empty()).when(courseRepository).findById(courseIdMocked);

        mockMvc.perform(post("/course/".concat(courseIdMocked.toString()).concat("/publish")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.field").value("id"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void publishCourse__should_return_bad_request_when_course_has_not_at_least_one_task_of_each_type() throws Exception {
        Long courseIdMocked = 1L;

        doReturn(Optional.of(mock(Course.class))).when(courseRepository).findById(courseIdMocked);
        doReturn(false).when(taskRepository).existsAtLeatOneTaskOfEachTypeByCourseId(courseIdMocked);

        mockMvc.perform(post("/course/".concat(courseIdMocked.toString()).concat("/publish")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("tasks"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void publishCourse__should_return_bad_request_when_course_status_is_not_building() throws Exception {
        Course mockedCourse =  mock(Course.class);
        Long mockedCourseId = 1L;

        doReturn(Optional.of(mockedCourse)).when(courseRepository).findById(mockedCourseId);
        doReturn(true).when(taskRepository).existsAtLeatOneTaskOfEachTypeByCourseId(mockedCourseId);
        doReturn(false).when(mockedCourse).isBuilding();

        mockMvc.perform(post("/course/".concat(mockedCourseId.toString()).concat("/publish")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("status"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void publishCourse__should_return_ok_when_course_is_valid_to_publish() throws Exception {
        Course mockedCourse =  mock(Course.class);
        Long mockedCourseId = 1L;

        doReturn(Optional.of(mockedCourse)).when(courseRepository).findById(mockedCourseId);
        doReturn(true).when(taskRepository).existsAtLeatOneTaskOfEachTypeByCourseId(mockedCourseId);
        doReturn(true).when(mockedCourse).isBuilding();

        mockMvc.perform(post("/course/".concat(mockedCourseId.toString()).concat("/publish")))
                .andExpect(status().isOk());

        verify(courseRepository, times(1)).save(any(Course.class));
    }
}