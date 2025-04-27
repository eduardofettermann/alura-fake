package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.*;

public class NewTaskDTO {

    @NotNull
    private Long courseId;
    @NotNull
    private Type type;
    @NotNull
    @Positive(message = "The order must be integer and positive")
    private int order;
    @NotBlank
    @Size(min = 4, max = 255, message = "The statement length must be between 4 and 255 characters")
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