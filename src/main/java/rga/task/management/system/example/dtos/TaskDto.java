package rga.task.management.system.example.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import rga.task.management.system.example.enums.Priority;
import rga.task.management.system.example.enums.Status;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskDto {

    private Long id;

    private String title;

    private String description;

    private Status status;

    private Priority priority;

    @JsonProperty("Author")
    private UserDto author;

    @JsonProperty("Executor")
    private UserDto executor;

    private Timestamp timestamp;
}
