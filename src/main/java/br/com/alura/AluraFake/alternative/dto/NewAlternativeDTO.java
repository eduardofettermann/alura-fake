package br.com.alura.AluraFake.alternative.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record NewAlternativeDTO(
        @NotNull
        @NotBlank
        @Length(min = 4, max = 80, message = "The option length must be between 4 and 80 characters")
        String option,

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
