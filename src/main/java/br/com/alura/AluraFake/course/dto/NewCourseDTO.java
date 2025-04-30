package br.com.alura.AluraFake.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record NewCourseDTO(
        @Schema(description = "Nome do curso", example = "Spring Boot na prática")
        @NotNull
        @NotBlank
        String title,

        @Schema(description = "Descrição do curso", example = "Aprenda a desenvolver APIs REST com Spring Boot")
        @NotNull
        @NotBlank
        @Length(min = 4, max = 255)
        String description,

        @Schema(description = "Email do instrutor", example = "juliano@alura.com.br")
        @NotNull
        @NotBlank
        @Email
        String emailInstructor
) {}
