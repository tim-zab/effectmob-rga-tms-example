package rga.task.management.system.example.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rga.task.management.system.example.entities.Comment;
import rga.task.management.system.example.entities.Task;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByTaskOrderByTimestampAsc (Task task, Pageable pageable);

    Page<Comment> findAllByTask (Task task, Pageable pageable);

}
