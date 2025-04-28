package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.alternative.NewAlternativeDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class NewSingleChoiceTaskDTO extends NewTaskDTO {
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
    public Type getType() {
        return Type.SINGLE_CHOICE;
    }

    @Override
    public void setType(Type type) { /* O tipo é sempre SINGLE_CHOICE */ }
}
