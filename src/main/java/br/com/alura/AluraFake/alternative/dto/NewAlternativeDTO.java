package br.com.alura.AluraFake.alternative.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record NewAlternativeDTO(
        @Schema(description = "Conteúdo da alternativa", example = "Java 21")
        @NotNull
        @NotBlank
        @Length(min = 4, max = 80, message = "The option length must be between 4 and 80 characters")
        String option,

        @Schema(description = "Definição da alternativa como correta", example = "true")
        @NotNull
        @JsonProperty("isCorrect")
        Boolean isCorrect
) {

    @JsonIgnore
    public boolean isIncorrect() {
        return !Boolean.TRUE.equals(isCorrect);
    }

    public NewAlternativeDTO setCorrect() {
        return new NewAlternativeDTO(option, true);
    }

    public NewAlternativeDTO setIncorrect() {
        return new NewAlternativeDTO(option, false);
    }
}
