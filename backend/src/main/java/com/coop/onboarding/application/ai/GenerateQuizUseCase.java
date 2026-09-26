package com.coop.onboarding.application.ai;

import com.coop.onboarding.domain.ai.GeneratedQuiz;

public interface GenerateQuizUseCase {
    GeneratedQuiz execute(GenerateQuizCommand command);
}
