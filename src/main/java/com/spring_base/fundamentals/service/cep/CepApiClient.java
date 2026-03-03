package com.spring_base.fundamentals.service.cep;

import com.spring_base.fundamentals.config.ApiProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CepApiClient {

    private final WebClient webClient;
    private final ApiProperties apiProperties;

    public CepApiClient(
            WebClient webClient,
            ApiProperties apiProperties
    ) {
        this.webClient = webClient;
        this.apiProperties = apiProperties;
    }

    public String fetchViaCep(String cep) {
        return webClient.get()
                .uri(apiProperties.viacep().url() + "/" + cep + "/json")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String fetchNationalize(String name) {
        return webClient.get()
                .uri(apiProperties.second().url() + "/?name=" + name)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
