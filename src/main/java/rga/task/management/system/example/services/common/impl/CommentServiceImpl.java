package rga.task.management.system.example.services.common.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rga.task.management.system.example.dtos.CommentDto;
import rga.task.management.system.example.entities.Comment;
import rga.task.management.system.example.exceptions.AccessForbiddenException;
import rga.task.management.system.example.exceptions.NotFoundException;
import rga.task.management.system.example.mappers.impl.CommentMapper;
import rga.task.management.system.example.repositories.CommentRepository;
import rga.task.management.system.example.services.common.CommentService;
import rga.task.management.system.example.services.common.TaskService;
import rga.task.management.system.example.services.access.UserAccessService;
import rga.task.management.system.example.services.common.UserService;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final CommentMapper mapper;
    private final UserService userService;
    private final TaskService taskService;
    private final UserAccessService userAccessService;

    /**
     * Finding comment by its id
     * @param id of comment to be found
     * @return comment or throws NotFoundException
     */
    private Comment findById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND,
                        "Comment with id " + id + " is not found")
        );
    }

    /**
     * Getting comment by its id - for admin or comment author
     * @param id of comment to be got
     * @return comment or throws AccessForbiddenException
     */
    private Comment getById(Long id) {
        if (userAccessService.isAdminOrCommentator(findById(id))) {
            return findById(id);
        } else {
            throw new AccessForbiddenException(HttpStatus.FORBIDDEN,
                    "Access denied. User does not have the required permissions to get this comment");
        }
    }

    /**
     * Getting comments' page by task id
     * @param taskId of comments to be got
     * @param pageable - interface for pagination
     * @return comments' page
     */
    @Override
    public Page<CommentDto> getPageByTaskId(Long taskId, Pageable pageable) {
        var task = taskService.getById(taskId);
         if (pageable.getSort().isUnsorted()) {
                return mapper.toDtosPage(repository.findAllByTaskOrderByTimestampAsc(task, pageable));
            } else {
                return mapper.toDtosPage(repository.findAllByTask(task, pageable));
            }
    }

    /**
     * Deleting comment by its id - for comment author only (may throw AccessForbiddenException)
     * @param commentId - id of comment to be deleted
     */
    @Override
    @Transactional
    public void deleteById(Long commentId) {
        if (userAccessService.isCommentator(findById(commentId))) {
            repository.delete(findById(commentId));
        } else {
            throw new AccessForbiddenException(HttpStatus.FORBIDDEN,
                    "Access denied. This user cannot delete this comment because he/she is not the comment author.");
        }
    }

    /**
     * Adding new comment to particular task - for admin or task executor
     * @param taskId - id of task to which comment has to be added
     * @param content - actual content of comment to be added
     * @return CommentDto
     */
    @Override
    @Transactional
    public CommentDto add(Long taskId, String content) {
        var task = taskService.getById(taskId);

        var comment = mapper.toEntity(content);
        comment.setContent(content);

        comment.setTask(task);
        task.getComments().add(comment);

        var commentator = userService.getByEmail(userAccessService.getCurrentEmail());
        comment.setCommentator(commentator);

        return mapper.toDto(repository.save(comment));
    }

    /**
     * Updating existent comment - for comment author only
     * @param content of comment to be updated
     * @param commentId - id of comment to be updated
     * @return CommentDto
     */
    @Override
    @Transactional
    public CommentDto update(String content, Long commentId) {
        var comment = getById(commentId);
        if (userAccessService.isCommentator(comment)) {
            comment.setContent(content);
            repository.save(comment);
            return mapper.toDto(comment);
        } else {
            throw new AccessForbiddenException(HttpStatus.FORBIDDEN,
                    "Access denied. User does not have the required permissions to edit comment.");
        }
    }

}
