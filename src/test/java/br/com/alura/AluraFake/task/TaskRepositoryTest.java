package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static br.com.alura.AluraFake.task.Type.OPEN_TEXT;
import static br.com.alura.AluraFake.task.Type.SINGLE_CHOICE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Test
    void findAllByCourseId__should_return_tasks_with_course_id() {
        User user = new User("Eduardo", "eduardofettermann212@gmail.com", Role.INSTRUCTOR);
        Course course = new Course(
                "Object Calisthenics em Java ",
                "Aprofunde-se em boas práticas de POO com Alura",
                user
        );
        String firstExpectedStatement = "Por que não devemos abreviar nomes de variáveis?";
        String secondExpectedStatement = "Qual desses métodos segue a convenção 'Don’t Abbreviate'?";
        Task firstTask = new Task(course, OPEN_TEXT, 1, firstExpectedStatement);
        Task secondTask = new Task(course, SINGLE_CHOICE, 2, secondExpectedStatement);

        userRepository.save(user);
        courseRepository.save(course);
        taskRepository.save(firstTask);
        taskRepository.save(secondTask);
        List<Task> foundTasks = taskRepository.findAllByCourseId(course.getId());

        assertThat(foundTasks.size()).isEqualTo(2);
        assertThat(foundTasks.get(0).getStatement()).isEqualTo(firstExpectedStatement);
        assertThat(foundTasks.get(1).getStatement()).isEqualTo(secondExpectedStatement);

        foundTasks = taskRepository.findAllByCourseId(2L);
        assertThat(foundTasks.size()).isEqualTo(0);
    }
}