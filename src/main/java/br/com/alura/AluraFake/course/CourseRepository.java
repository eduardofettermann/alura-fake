package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long>{

}
