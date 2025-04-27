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
        String lastExpectedStatement = "Por que não devemos abreviar nomes de variáveis?";
        String firstExpectedStatement = "Qual desses métodos segue a convenção 'Don’t Abbreviate'?";
        Task lastExpectedTask = new Task(course, OPEN_TEXT, 2, lastExpectedStatement);
        Task firstExpectedTask = new Task(course, SINGLE_CHOICE, 1, firstExpectedStatement);
        List<Task> expectedTasksList = List.of(firstExpectedTask, lastExpectedTask);

        userRepository.save(user);
        courseRepository.save(course);
        taskRepository.save(lastExpectedTask);
        taskRepository.save(firstExpectedTask);
        List<Task> foundTasks = taskRepository.findTasksByCourseIdOrderByOrderItemAsc(course.getId());

        assertThat(foundTasks.size()).isEqualTo(2);
        assertThat(foundTasks).isEqualTo(expectedTasksList);

        foundTasks = taskRepository.findTasksByCourseIdOrderByOrderItemAsc(2L);
        assertThat(foundTasks.size()).isEqualTo(0);
    }

    @Test
    void existsTasksByCourseIdAndByStatement__should_return_true_when_exists_tasks_with_the_course_id_and_statement() {
        User user = new User("Eduardo", "eduardofettermann212@gmail.com", Role.INSTRUCTOR);
        Course course = new Course(
                "Object Calisthenics em Java ",
                "Aprofunde-se em boas práticas de POO com Alura",
                user
        );
        String statement = "Por que não devemos abreviar nomes de variáveis?";
        Task firstTask = new Task(course, SINGLE_CHOICE, 1, statement);
        Task lastTask = new Task(course, OPEN_TEXT, 2, statement);

        userRepository.save(user);
        courseRepository.save(course);
        taskRepository.save(firstTask);
        boolean existsTasksWithTheSameCourseIdAndStatement = taskRepository.existsTasksByCourseIdAndByStatement(
                course.getId(),
                lastTask.getStatement()
        );

        assertThat(existsTasksWithTheSameCourseIdAndStatement).isTrue();

        existsTasksWithTheSameCourseIdAndStatement = taskRepository.existsTasksByCourseIdAndByStatement(2L, statement);
        assertThat(existsTasksWithTheSameCourseIdAndStatement).isFalse();
    }

    @Test
    void findMaxOrderByCourseId__should_return_max_task_order() {
        User user = new User("Eduardo", "eduardofettermann212@gmail.com", Role.INSTRUCTOR);
        Course course = new Course(
                "Object Calisthenics em Java ",
                "Aprofunde-se em boas práticas de POO com Alura",
                user
        );
        Task firstTask = new Task(course, SINGLE_CHOICE, 1, "Pra que serve o Spring Security?");
        Task lastTask = new Task(
                course, OPEN_TEXT,
                2,
                "Como é possível testar uma classe de serviço sem chamar a repository?"
        );

        userRepository.save(user);
        courseRepository.save(course);
        taskRepository.save(firstTask);
        taskRepository.save(lastTask);
        Integer maxOrder = taskRepository.findMaxOrderByCourseId(
                course.getId()
        );

        assertThat(maxOrder).isEqualTo(lastTask.getOrder());

        maxOrder = taskRepository.findMaxOrderByCourseId(2L);
        assertThat(maxOrder).isNull();
    }
}