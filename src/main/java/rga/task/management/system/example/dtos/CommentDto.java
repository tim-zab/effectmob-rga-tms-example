package rga.task.management.system.example.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CommentDto {

    private Long id;

    private String content;

    @JsonProperty(value = "Commentator")
    private UserDto commentator;

    private Timestamp timestamp;

}
