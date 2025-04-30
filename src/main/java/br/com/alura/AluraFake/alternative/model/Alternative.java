package br.com.alura.AluraFake.alternative.model;

import br.com.alura.AluraFake.task.model.Task;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Alternative {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
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

    public Boolean isCorrect() {
        return isCorrect;
    }
}
