package com.spring_base.fundamentals.service;

import com.spring_base.fundamentals.config.ApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Slf4j
public class CepService {

    private final WebClient webClient;
    private final ApiProperties apiProperties;

    public Map<String, Object> fetchCepVirtualThreads(String cep) {

        log.info("Fetching data for CEP (Virtual Threads): {}", cep);
        long start = System.currentTimeMillis();

        try(ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            Future<String> futureCep = executor.submit(() -> apiCep(cep));
            Future<String> futureNationalize = executor.submit(() -> apiNationalize(cep));

            String cepResult = futureCep.get();
            String nationalizeResult = futureNationalize.get();

            long duration = System.currentTimeMillis() - start;
            log.info("Parallel query (Virtual Threads) completed in {}ms", duration);

            return Map.of(
                    "viaCep", cepResult,
                    "nationalize", nationalizeResult,
                    "elapsedMs", duration,
                    "method", "Virtual Threads"
            );

        } catch (Exception e) {
            throw new RuntimeException("Error fetching data", e);
        }

    }

    public Map<String, Object> fetchCepCompletableFuture(String cep) {

        log.info("Fetching data for CEP: {}", cep);
        long start = System.currentTimeMillis();

        CompletableFuture<String> futureCep = CompletableFuture.supplyAsync(() -> apiCep(cep));
        CompletableFuture<String> futureNationalize = CompletableFuture.supplyAsync(() -> apiNationalize(cep));

        CompletableFuture.allOf(futureCep, futureNationalize).join();

        long duration = System.currentTimeMillis() - start;
        log.info("Parallel query completed in {}ms", duration);

        return Map.of(
            "viaCep", futureCep.join(),
            "nationalize", futureNationalize.join(),
            "elapsedMs", duration,
                "method", "CompletableFuture"
        );
    }

    private String apiNationalize(String name) {
        return webClient.get()
                .uri(apiProperties.second().url() + "/?name=" + name)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private String apiCep(String cep) {
        return webClient.get()
                .uri(apiProperties.viacep().url() + "/" + cep + "/json")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
