package br.com.alura.AluraFake.course.dto;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.model.CourseStatus;

import java.io.Serializable;

public record CourseListItemDTO(
        Long id,
        String title,
        String description,
        CourseStatus status
) implements Serializable {

    public CourseListItemDTO(Course course) {
        this(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getStatus()
        );
    }
}
