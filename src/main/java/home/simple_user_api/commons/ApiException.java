package home.simple_user_api.commons;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class ApiException extends ResponseStatusException {
    private final HttpStatusCode httpStatusCode;
    private final ServiceErrorResponseCode errorResponseCode;

    public ApiException(ServiceErrorResponseCode errorResponseCode, HttpStatusCode httpStatusCode) {
        super(httpStatusCode, errorResponseCode.getMessage());
        this.httpStatusCode = httpStatusCode;
        this.errorResponseCode = errorResponseCode;
    }
}
