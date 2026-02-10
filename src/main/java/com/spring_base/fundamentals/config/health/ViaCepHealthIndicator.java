package com.spring_base.fundamentals.config.health;

import com.spring_base.fundamentals.config.ApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ViaCepHealthIndicator implements HealthIndicator {

    private final WebClient webClient;
    private final ApiProperties apiProperties;

    @Override
    public Health health() {
        try {
            webClient.get()
                    .uri(apiProperties.viacep().url() + "/01001000/json")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return Health.up()
                    .withDetail("viaCEP", "API available")
                    .build();

        } catch (Exception e) {
            return Health.down()
                    .withDetail("viaCep", "API unavailable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
