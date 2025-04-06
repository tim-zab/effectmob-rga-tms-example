package rga.task.management.system.example.services.common.impl;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rga.task.management.system.example.dtos.UserAuthRequestDto;
import rga.task.management.system.example.entities.User;
import rga.task.management.system.example.exceptions.NotFoundException;
import rga.task.management.system.example.mappers.impl.UserMapper;
import rga.task.management.system.example.repositories.UserRepository;
import rga.task.management.system.example.services.common.UserService;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private static final String EMAIL_PATTERN = "^[\\w-.]+@[\\w-]+(\\.[\\w-]+)*\\.[a-z]{2,}$";
    private static final int PASSWORD_MIN_LENGTH = 8;

    private final UserRepository repository;
    private final UserMapper mapper;

    /**
     * Getting user by id
     * @param id of user to be got
     * @return found user or throw NotFoundException
     */
    @Override
    public User getById(@NotNull Long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND, "User with id " + id + " is not found")
        );
    }

    /**
     * Getting user by email
     * @param email of user to be got
     * @return found user or throw NotFoundException
     */
    @Override
    public User getByEmail(@NotNull String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND, "User with e-mail " + email + " is not found")
        );
    }

    /**
     * Creating new user
     * @param dto for creating user
     * @return created user
     */
    @Override
    @Transactional
    public User create(UserAuthRequestDto dto) {
        return repository.save(
                mapper.toEntity(dto)
        );
    }

    /**
     * Validating user's email
     * @param email to be validated
     * @return true/false
     */
    @Override
    public boolean validateEmail(@NotNull String email) {
        return email.matches(EMAIL_PATTERN);
    }

    /**
     * Validating user's password
     * @param password to be validated
     * @return true/false
     */
    @Override
    public boolean validatePassword(@NotNull String password) {
        return password.length() >= PASSWORD_MIN_LENGTH;
    }

    /**
     * Checking if email exists in db
     * @param email to be checked
     * @return true/false
     */
    @Override
    public boolean existsByEmail(@NotNull String email) {
            return repository.existsByEmail(email);
    }

}
