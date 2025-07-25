package home.simple_user_api.users.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record RegistrationRequest(@NotBlank String login, @NotBlank String password) {}