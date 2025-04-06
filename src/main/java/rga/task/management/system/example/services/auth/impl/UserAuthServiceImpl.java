package rga.task.management.system.example.services.auth.impl;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rga.task.management.system.example.dtos.UserAuthRequestDto;
import rga.task.management.system.example.dtos.UserAuthResponseDto;
import rga.task.management.system.example.exceptions.InvalidDataException;
import rga.task.management.system.example.exceptions.UserAlreadyExistentException;
import rga.task.management.system.example.security.JwtAccessProvider;
import rga.task.management.system.example.services.common.UserService;
import rga.task.management.system.example.services.auth.UserAuthService;

@AllArgsConstructor
@Service
public class UserAuthServiceImpl implements UserAuthService {

    private final JwtAccessProvider provider;
    private final UserService service;
    private final PasswordEncoder encoder;

    @Override
    public UserAuthResponseDto authenticate(UserAuthRequestDto dto) {
        if (!service.validateEmail(dto.getEmail())) {
            throw new InvalidDataException(HttpStatus.BAD_REQUEST,
                    "Invalid e-mail " + dto.getEmail() + " has been provided");
        } else {
            var user = service.getByEmail(dto.getEmail());
            if (encoder.matches(dto.getPassword(), user.getPassword())) {
                return new UserAuthResponseDto(provider.buildAccessJwt(user));
            } else {
                throw new InvalidDataException(HttpStatus.BAD_REQUEST,
                        "Invalid password " + dto.getPassword() + " has been provided");
            }
        }
    }

    @Override
    @Transactional
    public UserAuthResponseDto register(UserAuthRequestDto dto) {
        if(service.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistentException(HttpStatus.BAD_REQUEST,
                    "User with e-mail " + dto.getEmail() + " is already existent in the system. " +
                            "Try to use another e-mail to register or just try to authenticate with this e-mail.");
        } else {
            if(!service.validateEmail(dto.getEmail())) {
                throw new InvalidDataException(HttpStatus.BAD_REQUEST,
                        "Invalid e-mail " + dto.getEmail() + " has been provided");
            } else if (!service.validatePassword(dto.getPassword())) {
                throw new InvalidDataException(HttpStatus.BAD_REQUEST,
                        "Invalid password " + dto.getPassword() + " has been provided");
            } else {
                var user = service.create(dto);
                return new UserAuthResponseDto(provider.buildAccessJwt(user));
            }
        }
    }

}
