package rga.task.management.system.example.services.auth;

import rga.task.management.system.example.dtos.UserAuthRequestDto;
import rga.task.management.system.example.dtos.UserAuthResponseDto;

public interface UserAuthService {

    UserAuthResponseDto authenticate(UserAuthRequestDto userAuthRequestDto);

    UserAuthResponseDto register(UserAuthRequestDto userAuthRequestDto);

}
