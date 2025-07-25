package home.simple_user_api.commons;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getHttpStatusCode().value(),
                exception.getErrorResponseCode().getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, exception.getHttpStatusCode());
    }
}
