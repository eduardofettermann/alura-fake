package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.model.Task;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t " +
            "WHERE t.course.id = :courseId " +
            "ORDER BY t.order ASC")
    List<Task> findTasksByCourseIdOrderByOrderItemAsc(@Param("courseId") Long courseId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM Task t " +
            "WHERE t.course.id = :courseId AND t.statement = :statement")
    boolean existsTasksByCourseIdAndByStatement(@Param("courseId") Long courseId, @Param("statement") String statement);

    @Query("SELECT MAX(t.order) FROM Task t " +
            "WHERE t.course.id = :courseId")
    Integer findHighestOrderByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM Task t " +
            "WHERE t.course.id = :courseId " +
            "AND t.order = :order")
    boolean existsTasksByCourseIdAndByOrder(@Param("courseId") Long courseId, @Param("order") Integer order);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Task t " +
            "WHERE t.course.id = :courseId " +
            "AND t.order >= :order " +
            "ORDER BY t.order ASC")
    List<Task> findByCourseIdAndOrderGreaterThanEqualForUpdate(Long courseId, Integer order);

    @Query("SELECT CASE WHEN COUNT(DISTINCT t.taskType) = 3 THEN TRUE ELSE FALSE END " +
            "FROM Task t " +
            "WHERE t.course.id = :courseId")
    boolean existsAtLeatOneTaskOfEachTypeByCourseId(@Param("courseId") Long courseId);
}
