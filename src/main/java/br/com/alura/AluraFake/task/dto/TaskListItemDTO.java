package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.task.model.Task;

public record TaskListItemDTO(
        Long courseId,
        String statement,
        Integer order
) {

    public TaskListItemDTO(Task task) {
        this(
                task.getCourse().getId(),
                task.getStatement(),
                task.getOrder()
        );
    }
}