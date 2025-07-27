package home.simple_user_api.users.dtos.exceptions;

import home.simple_user_api.commons.ApiException;
import home.simple_user_api.commons.ServiceErrorResponseCode;
import org.springframework.http.HttpStatus;

public class InvalidLoginOrPasswordException extends ApiException {
    public InvalidLoginOrPasswordException() {
        super(ServiceErrorResponseCode.AUTH01, HttpStatus.UNAUTHORIZED);
    }
}
