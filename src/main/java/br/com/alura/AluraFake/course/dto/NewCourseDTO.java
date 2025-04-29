package br.com.alura.AluraFake.course.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record NewCourseDTO(
        @NotNull
        @NotBlank
        String title,

        @NotNull
        @NotBlank
        @Length(min = 4, max = 255)
        String description,

        @NotNull
        @NotBlank
        @Email
        String emailInstructor
) {}
