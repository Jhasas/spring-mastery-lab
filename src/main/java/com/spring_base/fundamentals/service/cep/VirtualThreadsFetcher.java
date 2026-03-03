package com.spring_base.fundamentals.service.cep;

import com.spring_base.fundamentals.config.ApiProperties;
import io.micrometer.context.ContextSnapshotFactory;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
@Slf4j
@Qualifier("v2")
public class VirtualThreadsFetcher implements CepFetcher{

    private final MeterRegistry meterRegistry;

    private final CepApiClient cepApiClient;

    @Override
    public Map<String, Object> fetch(String cep) {
        meterRegistry.counter("cep_requests_total", "version", "v2").increment();

        log.info("Fetching data for CEP (Virtual Threads): {}", cep);
        long start = System.currentTimeMillis();

        try(ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            var snapshot = ContextSnapshotFactory.builder().build().captureAll();

            Future<String> futureCep = executor.submit(() -> {
                try (var scope = snapshot.setThreadLocals()){
                    return cepApiClient.fetchViaCep(cep);
                }
            });
            Future<String> futureNationalize = executor.submit(() -> {
                try (var scope = snapshot.setThreadLocals()){
                    return cepApiClient.fetchNationalize(cep);
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
}
