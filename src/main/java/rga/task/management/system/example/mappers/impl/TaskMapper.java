package rga.task.management.system.example.mappers.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import rga.task.management.system.example.dtos.TaskAddOrUpdateDto;
import rga.task.management.system.example.dtos.TaskDto;
import rga.task.management.system.example.entities.Task;
import rga.task.management.system.example.entities.User;
import rga.task.management.system.example.mappers.Mapper;
import rga.task.management.system.example.mappers.PageMapper;
import rga.task.management.system.example.services.common.UserService;

import java.sql.Timestamp;

@Component
@AllArgsConstructor
public class TaskMapper implements Mapper<Task, TaskDto, TaskAddOrUpdateDto>, PageMapper<Task, TaskDto> {

    private UserMapper userMapper;
    private UserService userService;

    @Override
    public TaskDto toDto(Task task) {
        var dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setAuthor(userMapper.toDto(userService.getById(task.getAuthor().getId())));
        dto.setExecutor(userMapper.toDto(userService.getById(task.getExecutor().getId())));
        dto.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return dto;
    }

    @Override
    public Task toEntity(TaskAddOrUpdateDto dto) {
        var task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setExecutor(new User(
                dto.getExecutorEmail()
        ));
        return task;
    }

    @Override
    public Page<TaskDto> toDtosPage(Page<Task> tasks) {
        return new PageImpl<>(tasks.stream().map(this::toDto).toList());
    }

}
