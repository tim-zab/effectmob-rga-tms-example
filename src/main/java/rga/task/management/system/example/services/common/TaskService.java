package rga.task.management.system.example.services.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rga.task.management.system.example.dtos.TaskAddOrUpdateDto;
import rga.task.management.system.example.dtos.TaskDto;
import rga.task.management.system.example.entities.Task;
import rga.task.management.system.example.enums.Status;

public interface TaskService {

    Task getById(Long id);

    Page<TaskDto> getByAuthorId(Long authorId, Pageable pageable);

    Page<TaskDto> getByExecutorId(Long executorId, Pageable pageable);

    void deleteById(Long id);

    TaskDto add(TaskAddOrUpdateDto dto);

    TaskDto update(Long id, Status status);

    TaskDto update(Long id, TaskAddOrUpdateDto dto);

}
