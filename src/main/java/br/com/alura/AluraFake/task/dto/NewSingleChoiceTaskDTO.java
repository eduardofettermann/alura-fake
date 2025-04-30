package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.alternative.dto.NewAlternativeDTO;
import br.com.alura.AluraFake.task.model.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class NewSingleChoiceTaskDTO extends NewTaskDTO {
    @Schema(description = "Lista de alternativas para a questão")
    @NotNull
    @Size(min = 2, max = 5, message = "The list must have between 2 and 5 alternatives")
    @Valid
    private List<NewAlternativeDTO> options;

    public List<NewAlternativeDTO> getOptions() {
        return options;
    }

    public void setOptions(List<NewAlternativeDTO> options) {
        this.options = options;
    }

    @Override
    public TaskType getType() {
        return TaskType.SINGLE_CHOICE;
    }

    @Override
    public void setType(TaskType taskType) { /* O tipo é sempre SINGLE_CHOICE */ }

    public NewSingleChoiceTaskDTO() {
        super.setType(TaskType.SINGLE_CHOICE);
    }
}
