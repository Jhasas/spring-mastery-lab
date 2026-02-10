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
    private final ApiProperties apiProperties;  // final → injetado via construtor

    public Map<String, Object> buscaCepVirtualThreads(String cep) {

        log.info("Buscando dados para CEP (Virtual Threads): {}", cep);
        long inicio = System.currentTimeMillis();

        try(ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            Future<String> futureCep = executor.submit(() -> apiCep(cep));
            Future<String> futureNacionalize = executor.submit(() -> apiNacionalize(cep));

            String resultadoCep = futureCep.get();
            String resultadoNacionalize = futureNacionalize.get();

            long duracao = System.currentTimeMillis() - inicio;
            log.info("Consulta paralela (Virtual Threads) concluída em {}ms", duracao);

            return Map.of(
                    "viaCep", resultadoCep,
                    "nationalize", resultadoNacionalize,
                    "tempoMs", duracao,
                    "metodo", "Virtual Threads"
            );

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar dados", e);
        }

    }

    public Map<String, Object> buscaCepCompletableFuture(String cep) {

        log.info("Buscando dados para CEP: {}", cep);
        long inicio = System.currentTimeMillis();

        CompletableFuture<String> futureCep = CompletableFuture.supplyAsync(() -> apiCep(cep));
        CompletableFuture<String> futureNacionalize = CompletableFuture.supplyAsync(() -> apiNacionalize(cep));

        CompletableFuture.allOf(futureCep, futureNacionalize).join();

        long duracao = System.currentTimeMillis() - inicio;
        log.info("Consulta paralela concluída em {}ms", duracao);

        return Map.of(
            "viaCep", futureCep.join(),
            "nationalize", futureNacionalize.join(),
            "tempoMs", duracao,
                "metodo", "CompletableFuture"
        );
    }

    private String apiNacionalize(String name) {
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
