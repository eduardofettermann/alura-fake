package br.com.alura.AluraFake.course.model;

import br.com.alura.AluraFake.user.model.User;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String title;
    private String description;
    @ManyToOne
    private User instructor;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CourseStatus status;
    private LocalDateTime publishedAt;

    @Deprecated
    public Course(){}

    public Course(String title, String description, User instructor) {
        Assert.isTrue(instructor.isInstructor(), "Usuario deve ser um instrutor");
        this.title = title;
        this.instructor = instructor;
        this.description = description;
        this.status = CourseStatus.BUILDING;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setStatus(CourseStatus courseStatus) {
        this.status = courseStatus;
    }

    public User getInstructor() {
        return instructor;
    }

    public String getDescription() {
        return description;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public boolean isBuilding() {
        return CourseStatus.BUILDING == this.status;
    }

    public void publish() {
        this.status = CourseStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }
}
