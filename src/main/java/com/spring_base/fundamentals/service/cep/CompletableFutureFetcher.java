package com.spring_base.fundamentals.service.cep;

import io.micrometer.context.ContextSnapshotFactory;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
@Qualifier("v1")
public class CompletableFutureFetcher implements CepFetcher{

    private final MeterRegistry meterRegistry;

    private final CepApiClient cepApiClient;

    @Override
    public Map<String, Object> fetch(String cep) {

        meterRegistry.counter("cep_requests_total", "version", "v1").increment();

        log.info("Fetching data for CEP: {}", cep);
        long start = System.currentTimeMillis();


        var snapshot = ContextSnapshotFactory.builder().build().captureAll();

        CompletableFuture<String> futureCep = CompletableFuture.supplyAsync(() -> {
            try (var scope = snapshot.setThreadLocals()){
                return cepApiClient.fetchViaCep(cep);
            }
        });
        CompletableFuture<String> futureNationalize = CompletableFuture.supplyAsync(() -> {
            try (var scope = snapshot.setThreadLocals()){
                return cepApiClient.fetchNationalize(cep);
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

}
