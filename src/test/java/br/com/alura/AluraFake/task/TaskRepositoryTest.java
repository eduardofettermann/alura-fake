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

import static br.com.alura.AluraFake.task.Type.*;
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
    void findMaxOrderByCourseId__should_return_highest_task_order() {
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
        Integer maxOrder = taskRepository.findHighestOrderByCourseId(
                course.getId()
        );

        assertThat(maxOrder).isEqualTo(lastTask.getOrder());

        maxOrder = taskRepository.findHighestOrderByCourseId(2L);
        assertThat(maxOrder).isNull();
    }

    @Test
    void existsTasksByCourseIdAndByOrder__should_return_true_when_already_has_task_with_course_id_and_order() {
        User user = new User("Eduardo", "eduardofettermann212@gmail.com", Role.INSTRUCTOR);
        Course course = new Course(
                "Object Calisthenics em Java ",
                "Aprofunde-se em boas práticas de POO com Alura",
                user
        );
        Task task = new Task(
                course, OPEN_TEXT,
                2,
                "Pra que serve o Spring Security?"
        );

        userRepository.save(user);
        courseRepository.save(course);
        taskRepository.save(task);
        boolean existsTasksByCourseIdAndByOrder = taskRepository.existsTasksByCourseIdAndByOrder(
                course.getId(),
                task.getOrder()
        );

        assertThat(existsTasksByCourseIdAndByOrder).isTrue();

        existsTasksByCourseIdAndByOrder = taskRepository.existsTasksByCourseIdAndByOrder(2L, 2);
        assertThat(existsTasksByCourseIdAndByOrder).isFalse();
    }
    @Test
    void findByCourseIdAndOrderGreaterThanEqualForUpdate__should_return_tasks_with_greater_or_equal_order() {
        User user = new User("Eduardo", "eduardofettermann212@gmail.com", Role.INSTRUCTOR);
        Course course = new Course(
                "Object Calisthenics em Java ",
                "Aprofunde-se em boas práticas de POO com Alura",
                user
        );
        Task firstTask = new Task(
                course, OPEN_TEXT,
                1,
                "Pra que serve o Spring Security?"
        );
        Task lastTask = new Task(
                course, OPEN_TEXT,
                2,
                "Pra que serve o Spring Security?"
        );
        List<Task> expectedTasks = List.of(lastTask);

        userRepository.save(user);
        courseRepository.save(course);
        taskRepository.save(firstTask);
        taskRepository.save(lastTask);
        List<Task> tasksWithGreaterOrderThanNewOrder = taskRepository.findByCourseIdAndOrderGreaterThanEqualForUpdate(
                course.getId(),
                lastTask.getOrder()
        );

        assertThat(tasksWithGreaterOrderThanNewOrder).isEqualTo(expectedTasks);

        tasksWithGreaterOrderThanNewOrder = taskRepository
                .findByCourseIdAndOrderGreaterThanEqualForUpdate(2L, 2);
        assertThat(tasksWithGreaterOrderThanNewOrder).isEqualTo(List.of());
    }

    @Test
    void existsAtLeatOneTaskOfEachTypeByCourseId__should_return_if_course_has_at_least_one_task_of_each_type() {
        User user = new User("Eduardo", "eduardofettermann212@gmail.com", Role.INSTRUCTOR);
        Course course = new Course(
                "Object Calisthenics em Java ",
                "Aprofunde-se em boas práticas de POO com Alura",
                user
        );
        Task springSecurity = new Task(
                course, OPEN_TEXT,
                1,
                "Pra que serve o Spring Security?"
        );
        Task migrations = new Task(
                course, SINGLE_CHOICE,
                2,
                "No que as migrations nos ajudam?"
        );
        Task codeConventions = new Task(
                course, MULTIPLE_CHOICE,
                2,
                "No que as convenções de código ajudam?"
        );

        userRepository.save(user);
        courseRepository.save(course);
        taskRepository.save(springSecurity);
        taskRepository.save(migrations);
        taskRepository.save(codeConventions);
        boolean tasksWithGreaterOrderThanNewOrder = taskRepository.existsAtLeatOneTaskOfEachTypeByCourseId(
                course.getId()
        );

        assertThat(tasksWithGreaterOrderThanNewOrder).isTrue();

        tasksWithGreaterOrderThanNewOrder = taskRepository
                .existsAtLeatOneTaskOfEachTypeByCourseId(2L);
        assertThat(tasksWithGreaterOrderThanNewOrder).isFalse();
    }
}