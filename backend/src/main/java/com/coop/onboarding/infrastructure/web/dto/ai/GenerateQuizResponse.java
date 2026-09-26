package com.coop.onboarding.infrastructure.web.dto.ai;

import com.coop.onboarding.domain.ai.GeneratedQuiz;
import com.coop.onboarding.domain.ai.QuizQuestion;

import java.util.List;
import java.util.UUID;

public record GenerateQuizResponse(
        UUID lessonId,
        List<QuizQuestion> questions
) {
    public static GenerateQuizResponse fromDomain(GeneratedQuiz quiz) {
        return new GenerateQuizResponse(
                quiz.getLessonId(),
                quiz.getQuestions()
        );
    }
}
