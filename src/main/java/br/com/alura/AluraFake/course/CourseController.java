package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.dto.CourseListItemDTO;
import br.com.alura.AluraFake.course.dto.NewCourseDTO;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.exception.domain.CourseIsNotBuildingException;
import br.com.alura.AluraFake.exception.domain.CourseNotFoundException;
import br.com.alura.AluraFake.exception.domain.MissingRequiredTaskTypesException;
import br.com.alura.AluraFake.exception.forbidden.NotAnInstructorException;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.user.*;
import br.com.alura.AluraFake.user.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CourseController {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository, UserRepository userRepository, TaskRepository taskRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    @PostMapping("/course/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse) {

        //Caso implemente o bonus, pegue o instrutor logado
        Optional<User> possibleAuthor = userRepository
                .findByEmail(newCourse.emailInstructor())
                .filter(User::isInstructor);

        if(possibleAuthor.isEmpty()) {
            throw new NotAnInstructorException("emailInstructor");
        }

        Course course = new Course(newCourse.title(), newCourse.description(), possibleAuthor.get());

        courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/course/all")
    public ResponseEntity<List<CourseListItemDTO>> createCourse() {
        List<CourseListItemDTO> courses = courseRepository.findAll().stream()
                .map(CourseListItemDTO::new)
                .toList();
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/course/{id}/publish")
    @Transactional
    public ResponseEntity publishCourse(@PathVariable("id") Long id) {
        // Dúvida: Validar ou não se a ordem das Tasks estão em sequência, pois essa validação já é feita na criação das mesmas
        Optional<Course> possibleCourse = courseRepository.findById(id);
        if (possibleCourse.isEmpty()) {
            String message = String.format("Um curso com o ID %d não foi encontrado.", id);
            throw new CourseNotFoundException("id", message);
        }

        Course course = possibleCourse.get();

        boolean courseHasAtLeastOneTaskOfEachType = taskRepository.existsAtLeatOneTaskOfEachTypeByCourseId(id);
        if (!courseHasAtLeastOneTaskOfEachType) {
            throw new MissingRequiredTaskTypesException("tasks");
        }

        if (!course.isBuilding()) {
            String message = String.format("O curso com o ID %d não está em construção.", course.getId());
            throw new CourseIsNotBuildingException("status", message);
        }

        course.publish();
        courseRepository.save(course);

        return ResponseEntity.ok().build();
    }

}
