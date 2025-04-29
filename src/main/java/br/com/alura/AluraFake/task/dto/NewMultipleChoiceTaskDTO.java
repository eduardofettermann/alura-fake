package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.alternative.dto.NewAlternativeDTO;
import br.com.alura.AluraFake.task.model.TaskType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class NewMultipleChoiceTaskDTO extends NewTaskDTO {
    @NotNull
    @Size(min = 3, max = 5, message = "The list must have between 3 and 5 alternatives")
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
        return TaskType.MULTIPLE_CHOICE;
    }

    @Override
    public void setType(TaskType taskType) { /* O tipo é sempre MULTIPLE_CHOICE */ }

    public NewMultipleChoiceTaskDTO() {
        super.setType(TaskType.MULTIPLE_CHOICE);
    }
}
