package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.task.model.TaskType;

public class NewOpenTextTaskDTO extends NewTaskDTO {

    @Override
    public TaskType getType() {
        return TaskType.OPEN_TEXT;
    }

    @Override
    public void setType(TaskType taskType) { /* O tipo é sempre OPEN_TEXT */ }

    public NewOpenTextTaskDTO() {
        super.setType(TaskType.OPEN_TEXT);
    }
}
