package home.simple_user_api.users.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RegistrationRequest(@NotBlank String login, @NotBlank @Length(min = 10) String password) {}