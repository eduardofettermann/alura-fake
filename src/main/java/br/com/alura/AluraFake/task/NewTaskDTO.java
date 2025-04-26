package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.*;

public class NewTaskDTO {

    @NotNull
    private Long courseId;
    @NotNull
    private Type type;
    @NotNull
    @Positive(message = "A ordem deve ser um inteiro positivo")
    private int order;
    @NotBlank
    @Size(min = 4, max = 255, message = "O enunciado deve ter entre 4 e 255 caracteres")
    private String statement;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }
}