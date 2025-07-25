package home.simple_user_api.users.dtos.exceptions;

import home.simple_user_api.commons.ApiException;
import home.simple_user_api.commons.ServiceErrorResponseCode;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends ApiException {
    public UserAlreadyExistsException() {
        super(ServiceErrorResponseCode.USER02, HttpStatus.CONFLICT);
    }
}
