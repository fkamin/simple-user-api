package home.simple_user_api.commons;

import lombok.Getter;

@Getter
public enum ServiceErrorResponseCode {
    ITEM01("You have not provided an authentication token, the one provided has expired, was revoked or is not authentic."),

    USER01("User does not exist exception");

    private final String message;

    ServiceErrorResponseCode(String message) {
        this.message = message;
    }
}
