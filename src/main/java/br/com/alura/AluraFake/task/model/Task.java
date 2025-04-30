package br.com.alura.AluraFake.task.model;

import br.com.alura.AluraFake.alternative.model.Alternative;
import br.com.alura.AluraFake.course.model.Course;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    private Course course;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TaskType type;
    @Setter
    @Getter
    @Column(name = "order_item")
    private Integer order;
    @Getter
    private String statement;
    @Setter
    @Getter
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Alternative> alternatives;

    public Task(Course course, TaskType type, Integer order, String statement) {
        this.course = course;
        this.type = type;
        this.order = order;
        this.statement = statement;
    }
}
