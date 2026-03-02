package com.spring_base.fundamentals.service;

import com.spring_base.fundamentals.config.ApiProperties;
import io.micrometer.context.ContextSnapshotFactory;
import io.micrometer.core.instrument.MeterRegistry;
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

    private final MeterRegistry meterRegistry;

    public Map<String, Object> fetchCepVirtualThreads(String cep) {

        meterRegistry.counter("cep_requests_total", "version", "v2").increment();

        log.info("Fetching data for CEP (Virtual Threads): {}", cep);
        long start = System.currentTimeMillis();

        try(ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            var snapshot = ContextSnapshotFactory.builder().build().captureAll();

            Future<String> futureCep = executor.submit(() -> {
                try (var scope = snapshot.setThreadLocals()){
                    return apiCep(cep);
                }
            });
            Future<String> futureNationalize = executor.submit(() -> {
                try (var scope = snapshot.setThreadLocals()){
                    return apiNationalize(cep);
                }
            });

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

        meterRegistry.counter("cep_requests_total", "version", "v1").increment();

        log.info("Fetching data for CEP: {}", cep);
        long start = System.currentTimeMillis();


        var snapshot = ContextSnapshotFactory.builder().build().captureAll();

        CompletableFuture<String> futureCep = CompletableFuture.supplyAsync(() -> {
            try (var scope = snapshot.setThreadLocals()){
                return apiCep(cep);
            }
        });
        CompletableFuture<String> futureNationalize = CompletableFuture.supplyAsync(() -> {
            try (var scope = snapshot.setThreadLocals()){
                return apiNationalize(cep);
            }
        });

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
