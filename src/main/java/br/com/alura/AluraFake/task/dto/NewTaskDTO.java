package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.task.model.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class NewTaskDTO {
    @Schema(description = "ID do curso ao qual a tarefa pertence", example = "1")
    @NotNull
    private Long courseId;
    @NotNull
    private TaskType type;
    @Schema(description = "Ordem da tarefa no curso", example = "1")
    @NotNull
    @Positive(message = "The order must be integer and positive")
    private int order;
    @Schema(description = "Enunciado da tarefa", example = "O que aprendemos hoje?")
    @NotBlank
    @Size(min = 4, max = 255, message = "The statement length must be between 4 and 255 characters")
    private String statement;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType taskType) {
        this.type = taskType;
    }

    public int getOrder() {
        return order;
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