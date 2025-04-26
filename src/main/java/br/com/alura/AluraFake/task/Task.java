package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    private Course course;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column(name = "order_item")
    private Integer order;
    private String statement;

    public Task(Course course, Type type, Integer order, String statement) {
        this.course = course;
        this.type = type;
        this.order = order;
        this.statement = statement;
    }

    @Deprecated
    public Task() {}
}
