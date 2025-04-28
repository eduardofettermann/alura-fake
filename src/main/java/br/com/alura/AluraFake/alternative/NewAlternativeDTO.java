package br.com.alura.AluraFake.alternative;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class NewAlternativeDTO {
    @NotNull
    @NotBlank
    @Length(min = 4, max = 80, message = "The option length must be between 4 and 80 characters")
    private String option;
    @NotNull
    @JsonProperty("isCorrect")
    private Boolean isCorrect;

    public NewAlternativeDTO() {}

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @JsonProperty("isCorrect")
    public Boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect() {
        this.isCorrect = true;
    }

    public void setIncorrect() {
        this.isCorrect = false;
    }
}
