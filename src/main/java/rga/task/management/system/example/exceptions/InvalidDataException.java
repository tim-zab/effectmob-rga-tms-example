package rga.task.management.system.example.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidDataException extends RuntimeException{

    private final HttpStatus errorHttpStatus;
    private final String messageCode;

    public InvalidDataException(HttpStatus errorHttpStatus, String errorMessage) {
        super(errorMessage);
        this.errorHttpStatus = errorHttpStatus;
        this.messageCode = errorMessage;
    }

}
