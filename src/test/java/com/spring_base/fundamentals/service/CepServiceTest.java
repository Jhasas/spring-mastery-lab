package com.spring_base.fundamentals.service;

import com.spring_base.fundamentals.config.ApiProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CepServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ApiProperties apiProperties;

    @InjectMocks
    private CepService cepService;

    @BeforeEach
    void setUp() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(apiProperties.viacep()).thenReturn(new ApiProperties.ViaCep("http://fake-viecep"));
        when(apiProperties.second()).thenReturn(new ApiProperties.Second("http://fake-nationalize"));
    }

    @Test
    @DisplayName("CompletableFuture: deve retornar dados do CEP")
    void deveRetornarDadosDoCepComCompletableFuture() {
        // ARRANGE
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.just("resposta fake"));

        // ACT
        Map<String, Object> result = cepService.fetchCepCompletableFuture("83402220");

        // ASSERT
        assertEquals("resposta fake", result.get("viaCep"));
        assertEquals("resposta fake", result.get("nationalize"));
        assertEquals("CompletableFuture", result.get("method"));
    }

    @Test
    @DisplayName("CompletableFuture: deve conter tempo de execucao")
    void deveConterTempoDeExecucaoNoResultado() {
        // ARRANGE
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.just("resposta fake"));

        // ACT
        Map<String, Object> result = cepService.fetchCepCompletableFuture("83402220");

        // ASSERT
        assertNotNull(result.get("elapsedMs"));
    }

    @Test
    @DisplayName("VirtualThreads: deve retornar dados do CEP")
    void deveRetornarDadosDoCepComVirtualThreads() {
        // ARRANGE
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.just("resposta fake"));

        // ACT
        Map<String, Object> result = cepService.fetchCepVirtualThreads("83402220");

        // ASSERT
        assertEquals("resposta fake", result.get("viaCep"));
        assertEquals("resposta fake", result.get("nationalize"));
        assertEquals("Virtual Threads", result.get("method"));
    }

    @Test
    @DisplayName("VirtualThreads: deve conter tempo de execucao")
    void deveConterTempoDeExecucaoComVirtualThreads() {
        // ARRANGE
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.just("resposta fake"));

        // ACT
        Map<String, Object> result = cepService.fetchCepVirtualThreads("83402220");

        // ASSERT
        assertNotNull(result.get("elapsedMs"));
    }

    @Test
    @DisplayName("Deve lancar exception quando api cep falhar")
    void deveLancarExceptionQuandoApiCepFalhar() {
        // ARRANGE
        when(responseSpec.bodyToMono(String.class))
                .thenThrow(new RuntimeException("API fora"));
        // ACT
        // ASSERT
        assertThrows(RuntimeException.class, () -> {
            cepService.fetchCepVirtualThreads("83402220");
        });
    }


}
