package rga.task.management.system.example.mappers.impl;

import org.springframework.stereotype.Component;
import rga.task.management.system.example.dtos.UserAuthRequestDto;
import rga.task.management.system.example.dtos.UserDto;
import rga.task.management.system.example.entities.User;
import rga.task.management.system.example.enums.Role;
import rga.task.management.system.example.mappers.Mapper;

@Component
public class UserMapper implements Mapper<User, UserDto, UserAuthRequestDto> {

    @Override
    public UserDto toDto(User user) {
        var dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

    @Override
    public User toEntity(UserAuthRequestDto dto){
        var user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(Role.ROLE_USER);
        return user;
    }

}
