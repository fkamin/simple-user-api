package home.simple_user_api.users.dtos.exceptions;

import home.simple_user_api.commons.ApiException;
import home.simple_user_api.commons.ServiceErrorResponseCode;
import org.springframework.http.HttpStatus;

public class UserDoesNotExistsException extends ApiException {
    public UserDoesNotExistsException() {
        super(ServiceErrorResponseCode.USER01, HttpStatus.NOT_FOUND);
    }
}
