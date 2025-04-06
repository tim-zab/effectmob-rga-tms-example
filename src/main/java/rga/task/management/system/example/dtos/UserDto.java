package rga.task.management.system.example.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import rga.task.management.system.example.enums.Role;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDto {

    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "e-mail")
    private String email;

    @JsonProperty(value = "user role")
    private Role role;

}
