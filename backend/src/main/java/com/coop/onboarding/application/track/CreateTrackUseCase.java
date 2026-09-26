package com.coop.onboarding.application.track;

import com.coop.onboarding.domain.model.Track;

public interface CreateTrackUseCase {
    Track execute(CreateTrackCommand command);
}
