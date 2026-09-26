package com.coop.onboarding;

import org.springframework.boot.SpringApplication;

public class TestCoopOnboardingApplication {

	public static void main(String[] args) {
		SpringApplication.from(CoopOnboardingApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
