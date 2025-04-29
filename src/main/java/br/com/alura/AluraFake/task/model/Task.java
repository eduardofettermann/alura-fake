package br.com.alura.AluraFake.task.model;

import br.com.alura.AluraFake.alternative.model.Alternative;
import br.com.alura.AluraFake.course.model.Course;
import jakarta.persistence.*;

import java.util.List;


@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    private Course course;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TaskType taskType;
    @Column(name = "order_item")
    private Integer order;
    private String statement;
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Alternative> alternatives;

    public Task(Course course, TaskType taskType, Integer order, String statement) {
        this.course = course;
        this.taskType = taskType;
        this.order = order;
        this.statement = statement;
    }

    @Deprecated
    public Task() {}

    public Course getCourse() {
        return course;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getStatement() {
        return statement;
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }
}
