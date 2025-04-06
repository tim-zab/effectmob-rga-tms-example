package rga.task.management.system.example.services.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rga.task.management.system.example.dtos.CommentDto;

public interface CommentService {

    Page<CommentDto> getPageByTaskId(Long taskId, Pageable pageable);

    void deleteById(Long commentId);

    CommentDto add(Long taskId, String content);

    CommentDto update(String content, Long commentId);

}
