package rga.task.management.system.example.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import rga.task.management.system.example.enums.Priority;
import rga.task.management.system.example.enums.Status;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskAddOrUpdateDto {

    private String title;

    private String description;

    private Status status;

    private Priority priority;

    @JsonProperty(value = "executor's e-mail")
    private String executorEmail;

}
