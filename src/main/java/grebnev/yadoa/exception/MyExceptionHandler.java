package grebnev.yadoa.exception;


import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ValidationException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String VALIDATION_MESSAGE = "Validation Failed";

    @ExceptionHandler(value = Throwable.class)
    protected ResponseEntity<Object> commonHandler(Throwable ex, WebRequest request) {
        log.warn("Unexpected error. Massage: {}", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(getGeneralErrorBody(status, "Unexpected error has occurred"), status);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatus status,
            @NonNull WebRequest request
    ) {
        log.warn("Not Valid. Massage: {}", ex.getMessage());
        return new ResponseEntity<>(getGeneralErrorBody(status, VALIDATION_MESSAGE), headers, status);
    }

    @ExceptionHandler(value = NotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(NotFoundException ex, WebRequest request) {
        log.warn("Not found error: {}", ex.getMessage());
        return new ResponseEntity<>( request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ValidationException.class)
    protected ResponseEntity<Object> handleValidationError(ValidationException ex, WebRequest request) {
        log.warn("Not Valid. Massage: {}", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(getGeneralErrorBody(status, VALIDATION_MESSAGE), status);
    }

    private Map<String, Object> getGeneralErrorBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(CODE,status.value());
        body.put(MESSAGE, message);
        return body;
    }

}

