package rga.task.management.system.example.services.common;

import rga.task.management.system.example.dtos.UserAuthRequestDto;
import rga.task.management.system.example.entities.User;

public interface UserService {

    User getById(Long id);

    User getByEmail(String email);

    User create(UserAuthRequestDto dto);

    boolean validateEmail(String email);

    boolean validatePassword(String password);

    boolean existsByEmail(String email);

}
