package home.simple_user_api.items.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record CreateItemRequest(@NotBlank String name) {}
