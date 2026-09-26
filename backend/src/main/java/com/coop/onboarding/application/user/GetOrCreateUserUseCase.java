package com.coop.onboarding.application.user;

import com.coop.onboarding.domain.model.User;

public interface GetOrCreateUserUseCase {
    User execute(GetOrCreateUserCommand command);
}
