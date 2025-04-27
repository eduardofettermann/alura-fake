package br.com.alura.AluraFake.task;

public class TaskListItemDTO {
    private Long courseId;
    private String statement;
    private Integer order;

    public TaskListItemDTO(Task task) {
        this.courseId = task.getCourse().getId();
        this.statement = task.getStatement();
        this.order = task.getOrder();
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getStatement() {
        return statement;
    }

    public Integer getOrder() {
        return order;
    }
}
