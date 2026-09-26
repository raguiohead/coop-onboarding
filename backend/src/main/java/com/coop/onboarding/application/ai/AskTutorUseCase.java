package com.coop.onboarding.application.ai;

import com.coop.onboarding.domain.ai.TutorAnswer;

public interface AskTutorUseCase {
    TutorAnswer execute(AskTutorQuery query);
}
