package grebnev.yadoa.exception;


import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String VALIDATION_MESSAGE = "Validation Failed";

    @ExceptionHandler(value = NotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(NotFoundException ex, WebRequest request) {
        log.warn("Not found error: {}", ex.getMessage());
        return new ResponseEntity<>( request, HttpStatus.NOT_FOUND);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatus status,
            @NonNull WebRequest request
    ) {
        log.warn("Not Valid. Massege: {}", ex.getMessage());
        return new ResponseEntity<>(getGeneralErrorBody(status, VALIDATION_MESSAGE), headers, status);
    }

    private Map<String, Object> getGeneralErrorBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(CODE,status.value());
        body.put(MESSAGE, message);
        return body;
    }

    private String getErrorString(ObjectError error) {
        if (error instanceof FieldError) {
            return ((FieldError) error).getField() + " : " + error.getDefaultMessage();
        }
        return error.getDefaultMessage();
    }

}

