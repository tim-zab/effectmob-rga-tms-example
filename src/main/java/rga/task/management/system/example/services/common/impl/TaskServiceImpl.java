package rga.task.management.system.example.services.common.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rga.task.management.system.example.dtos.TaskAddOrUpdateDto;
import rga.task.management.system.example.dtos.TaskDto;
import rga.task.management.system.example.entities.Task;
import rga.task.management.system.example.enums.Status;
import rga.task.management.system.example.exceptions.AccessForbiddenException;
import rga.task.management.system.example.exceptions.InvalidDataException;
import rga.task.management.system.example.exceptions.NotFoundException;
import rga.task.management.system.example.mappers.impl.TaskMapper;
import rga.task.management.system.example.repositories.TaskRepository;
import rga.task.management.system.example.services.common.TaskService;
import rga.task.management.system.example.services.access.UserAccessService;
import rga.task.management.system.example.services.common.UserService;

@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final UserAccessService userAccessService;

    /**
     * Getting task by its id for admin or task executor
     * @param id of task to get
     * @return - task or throws AccessForbiddenException
     */
    @Override
    public Task getById(Long id) {
        if (userAccessService.isAdminOrTaskExecutor(findTaskById(id))) {
            return findTaskById(id);
        } else {
            throw new AccessForbiddenException(HttpStatus.FORBIDDEN,
                    "Access denied. User does not have the required permissions to get this task");
        }
    }

    /**
     * Finding task by its id if it exists in db
     * @param id of task to find
     * @return - task or throws NotFoundException
     */
    private Task findTaskById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND,
                        "Task with id " + id + " is not found")
        );
    }

    /**
     * Getting page with tasks by author id - for admin
     * @param authorId - id of author this page of tasks
     * @param pageable - interface for pagination
     * @return page of tasks dto or throws AccessForbiddenException
     */
    @Override
    public Page<TaskDto> getByAuthorId(Long authorId, Pageable pageable) {
        if (userAccessService.isAdmin()) {
            var author = userService.getById(authorId);
            if (pageable.getSort().isUnsorted()){
                return taskMapper.toDtosPage(repository.findAllByAuthorOrderByTimestampAsc(author, pageable));
            } else {
                return taskMapper.toDtosPage(repository.findAllByAuthor(author, pageable));
            }
        } else {
            throw new AccessForbiddenException(HttpStatus.FORBIDDEN,
                    "Access denied. User does not have the required permissions to get tasks page by author id");
        }
    }

    /**
     * Getting page with tasks by executor id - for admin or task executor
     * @param executorId - id of executor this page of tasks
     * @param pageable - interface for pagination
     * @return page of tasks dto or throws AccessForbiddenException
     */
    @Override
    public Page<TaskDto> getByExecutorId(Long executorId, Pageable pageable) {
        var userEmail = userAccessService.getCurrentEmail();
        var currentUser = userService.getByEmail(userEmail);
        if (userAccessService.isAdmin() || currentUser.getId().equals(executorId)) {
            var executor = userService.getById(executorId);
            if (pageable.getSort().isUnsorted()){
                return taskMapper.toDtosPage(repository.findAllByExecutorOrderByTimestampAsc(executor, pageable));
            } else {
                return taskMapper.toDtosPage(repository.findAllByExecutor(executor, pageable));
            }
        } else {
            throw new AccessForbiddenException(HttpStatus.FORBIDDEN,
                    "Access denied. User does not have the required permissions to get tasks page by this executor id");
        }
    }

    /**
     * Deleting task by its id - for admin (may throw AccessForbiddenException)
     * @param id of task to delete
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        if(!userAccessService.isAdmin()){
            throw new AccessForbiddenException(HttpStatus.FORBIDDEN,
                    "Access denied. User does not have the required permissions to delete task");
        }
        repository.delete(findTaskById(id));
    }

    /**
     * Task creating - for admin (may throw AccessForbiddenException, InvalidDataException)
     * @param dto for creating new task
     * @return - created TaskDto
     */
    @Override
    @Transactional
    public TaskDto add(TaskAddOrUpdateDto dto) {
        if (userAccessService.isAdmin()) {
            var task = taskMapper.toEntity(dto);
            if (userService.validateEmail(task.getExecutor().getEmail())) {
                var executor = userService.getByEmail(task.getExecutor().getEmail());
                task.setExecutor(executor);
                var author = userService.getByEmail(userAccessService.getCurrentEmail());
                task.setAuthor(author);
                return taskMapper.toDto(repository.save(task));
            } else {
                throw new InvalidDataException(HttpStatus.BAD_REQUEST,
                        "Email " + task.getExecutor().getEmail() + " is not valid");
            }
        }
        throw new AccessForbiddenException(HttpStatus.FORBIDDEN,
                "Access is denied. User does not have permission to create task.");
    }

    /**
     * Task updating - for admin (may throw AccessForbiddenException, InvalidDataException)
     * @param id of task for updating
     * @param dto for updating existent task
     * @return TaskDto - updated taskDto
     */
    @Override
    @Transactional
    public TaskDto update(Long id, TaskAddOrUpdateDto dto) {
        if (userAccessService.isAdmin()) {
            var task = findTaskById(id);
            if (atLeastOneFieldIsNotNull(dto)) {
                if (userService.validateEmail(dto.getExecutorEmail())) {
                    updateTask(dto, task);
                } else{
                    throw new InvalidDataException(HttpStatus.BAD_REQUEST,
                            "Email " + dto.getExecutorEmail() + " is not valid");
                }
            }
            return taskMapper.toDto(repository.save(task));
        } else {
            throw new AccessForbiddenException(HttpStatus.FORBIDDEN,
                    "Access is denied. User does not have permission to update task.");
        }
    }

    /**
     * Updating task status - for admin and task executor
     * @param id of task for updating its status
     * @param status for updating existent task status
     * @return TaskDto with updated status
     */
    @Override
    @Transactional
    public TaskDto update(Long id, Status status) {
        var task = getById(id);
        if(statusIsNotNull(status)){
            updateStatus(status, task);
        }
        return taskMapper.toDto(repository.save(task));
    }

    /**
     * At least one task field is not null
     * @param dto - TaskAddOrUpdateDto for update fields
     * @return true / false
     */
    private boolean atLeastOneFieldIsNotNull(TaskAddOrUpdateDto dto) {
        return dto.getTitle() != null || dto.getDescription() != null ||
                dto.getStatus() != null || dto.getPriority() != null ||
                dto.getExecutorEmail() != null;
    }

    /**
     * Task status is not null
     * @param status - task status
     * @return true / false
     */
    private boolean statusIsNotNull(Status status) {
        return status != null;
    }

    /**
     * Update task from TaskAddOrUpdateDto
     * @param dto - dto
     * @param task - entity
     */
    private void updateTask(TaskAddOrUpdateDto dto, Task task) {
        task.setTitle(dto.getTitle() != null ? dto.getTitle() : task.getTitle());
        task.setDescription(dto.getDescription() != null ? dto.getDescription() : task.getDescription());
        task.setStatus(dto.getStatus() != null ? dto.getStatus() : task.getStatus());
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : task.getPriority());
        task.setExecutor(
        !userService.getByEmail(dto.getExecutorEmail()).equals(userService.getByEmail(task.getExecutor().getEmail())) ?
            userService.getByEmail(dto.getExecutorEmail()) : task.getExecutor()
    );
        }

    /**
     * Update task status
     * @param status - task status
     * @param task - entity
     */
    private void updateStatus(Status status, Task task) {
        task.setStatus(status != null ? status : task.getStatus());
    }

}
