package br.com.alura.AluraFake.course.dto;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.model.CourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

public record CourseListItemDTO(
        @Schema(description = "ID do curso", example = "1")
        Long id,
        @Schema(description = "Nome do curso", example = "Spring Boot na prática")
        String title,
        @Schema(description = "Descrição resumida do curso", example = "Aprenda a desenvolver APIs REST com Spring Boot")
        String description,
        @Schema(description = "Situação do curso", example = "BUILDING", allowableValues = {"BUILDING", "PUBLISHED"})
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
