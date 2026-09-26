package com.coop.onboarding.application.ai;

import com.coop.onboarding.domain.ai.TutorAnswer;
import reactor.core.publisher.Flux;

public interface AskTutorUseCase {
    TutorAnswer execute(AskTutorQuery query);

    default Flux<String> stream(AskTutorQuery query) {
        return Flux.just(execute(query).getAnswer());
    }
}
