package grebnev.yadoa.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {
    private static final String VALIDATION_MESSAGE = "Validation Failed";
    private static final String NOT_FOUND_MESSAGE = "Item not found";

    //todo uncomment
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(Throwable.class)
//    protected ErrorResponse commonHandler(Throwable ex, WebRequest request) {
//        log.warn("Unexpected error. Massage: {}", ex.getMessage());
//        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error has occurred");
//    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("Not Valid. Massage: {}", ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST, VALIDATION_MESSAGE);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponse handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Error: {}", ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST, VALIDATION_MESSAGE);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = NotFoundException.class)
    protected ErrorResponse handleNotFound(NotFoundException ex) {
        log.warn("Not found error: {}", ex.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ValidationException.class)
    protected ErrorResponse handleValidationError(ValidationException ex) {
        log.warn("Not Valid. Massage: {}", ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST, VALIDATION_MESSAGE);
    }
}

