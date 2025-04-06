package rga.task.management.system.example.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rga.task.management.system.example.entities.Task;
import rga.task.management.system.example.entities.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findAllByAuthorOrderByTimestampAsc(User author, Pageable pageable);

    Page<Task> findAllByAuthor(User author, Pageable pageable);

    Page<Task> findAllByExecutorOrderByTimestampAsc(User executor, Pageable pageable);

    Page<Task> findAllByExecutor(User executor, Pageable pageable);

}
