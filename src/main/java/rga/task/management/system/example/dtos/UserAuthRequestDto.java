package rga.task.management.system.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAuthRequestDto {

    private String email;

    private String password;

}
