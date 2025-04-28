package br.com.alura.AluraFake.alternative;

import br.com.alura.AluraFake.task.Task;
import jakarta.persistence.*;

@Entity
public class Alternative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", referencedColumnName = "id", nullable = false)
    private Task task;
    @Column(name = "description")
    private String option;
    @Column(name = "is_correct")
    private Boolean isCorrect;

    public Alternative(Task task, String option, Boolean isCorrect) {
        this.task = task;
        this.option = option;
        this.isCorrect = isCorrect;
    }

    @Deprecated
    public Alternative() {}

    public Long getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public String getOption() {
        return option;
    }

    public Boolean isCorrect() {
        return isCorrect;
    }
}
