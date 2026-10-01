package com.coop.onboarding.infrastructure.web.dto;

import com.coop.onboarding.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotNull(message = "O papel é obrigatório")
        UserRole role,

        String department,
        String jobTitle,
        String password,
        String phone,
        String bio
) {}
