package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.dto.CourseListItemDTO;
import br.com.alura.AluraFake.course.dto.NewCourseDTO;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.exception.domain.CourseIsNotBuildingException;
import br.com.alura.AluraFake.exception.domain.CourseNotFoundException;
import br.com.alura.AluraFake.exception.domain.MissingRequiredTaskTypesException;
import br.com.alura.AluraFake.exception.forbidden.NotAnInstructorException;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.user.model.Role;
import br.com.alura.AluraFake.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CourseService courseService;

    private User instructor;
    private NewCourseDTO newCourseDTO;

    @BeforeEach
    void setUp() {
        instructor = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR);
        newCourseDTO = new NewCourseDTO("Java", "Curso de Java", "paulo@alura.com.br");
    }

    @Test
    void createCourse_should_throw_not_an_instructor_exception_when_user_is_not_instructor() {
        when(userRepository.findByEmail(newCourseDTO.emailInstructor())).thenReturn(Optional.of(new User("Paulo", "paulo@alura.com.br", Role.STUDENT)));

        assertThrows(NotAnInstructorException.class, () -> courseService.createCourse(newCourseDTO));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void createCourse_should_save_course_when_valid() {
        when(userRepository.findByEmail(newCourseDTO.emailInstructor())).thenReturn(Optional.of(instructor));

        courseService.createCourse(newCourseDTO);

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void listAllCourses_should_return_course_list() {
        Course java = new Course("Java", "Curso de Java", instructor);
        Course hibernate = new Course("Hibernate", "Curso de Hibernate", instructor);
        when(courseRepository.findAll()).thenReturn(Arrays.asList(java, hibernate));

        List<CourseListItemDTO> result = courseService.listAllCourses();

        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).title());
        assertEquals("Curso de Java", result.get(0).description());
        assertEquals("Hibernate", result.get(1).title());
        assertEquals("Curso de Hibernate", result.get(1).description());
    }

    @Test
    void publishCourse_should_throw_course_not_found_exception_when_course_does_not_exist() {
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class, () -> courseService.publishCourse(courseId));
        assertEquals("id", exception.getField());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void publishCourse_should_throw_missing_required_task_types_exception_when_tasks_missing() {
        Long courseId = 1L;
        Course course = new Course("Java", "Curso de Java", instructor);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(taskRepository.existsAtLeatOneTaskOfEachTypeByCourseId(courseId)).thenReturn(false);

        MissingRequiredTaskTypesException exception = assertThrows(MissingRequiredTaskTypesException.class, () -> courseService.publishCourse(courseId));
        assertEquals("tasks", exception.getField());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void publishCourse_should_throw_course_is_not_building_exception_when_status_not_building() {
        Long courseId = 1L;
        Course course = mock(Course.class);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(taskRepository.existsAtLeatOneTaskOfEachTypeByCourseId(courseId)).thenReturn(true);
        when(course.isBuilding()).thenReturn(false);
        when(course.getId()).thenReturn(courseId);

        CourseIsNotBuildingException exception = assertThrows(CourseIsNotBuildingException.class, () -> courseService.publishCourse(courseId));
        assertEquals("status", exception.getField());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void publishCourse_should_publish_course_when_valid() {
        Long courseId = 1L;
        Course course = mock(Course.class);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(taskRepository.existsAtLeatOneTaskOfEachTypeByCourseId(courseId)).thenReturn(true);
        when(course.isBuilding()).thenReturn(true);

        courseService.publishCourse(courseId);

        verify(course).publish();
        verify(courseRepository, times(1)).save(course);
    }
}
