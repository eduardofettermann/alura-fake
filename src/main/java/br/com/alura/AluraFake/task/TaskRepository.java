package br.com.alura.AluraFake.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.course.id = :courseId ORDER BY t.order ASC")
    List<Task> findTasksByCourseIdOrderByOrderItemAsc(@Param("courseId") Long courseId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM Task t " +
            "WHERE t.course.id = :courseId AND t.statement = :statement")
    boolean existsTasksByCourseIdAndByStatement(@Param("courseId") Long courseId, @Param("statement") String statement);

    @Query("SELECT MAX(t.order)" +
            "FROM Task t " +
            "WHERE t.course.id = :courseId")
    Integer findMaxOrderByCourseId(@Param("courseId") Long courseId);
}
