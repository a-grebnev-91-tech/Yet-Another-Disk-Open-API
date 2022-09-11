package grebnev.yadoa.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorResponse {
    private int code;
    private String message;

    public ErrorResponse(HttpStatus status, String message) {
        this.code = status.value();
        this.message = message;
    }
}
