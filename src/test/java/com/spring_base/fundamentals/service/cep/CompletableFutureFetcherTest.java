package com.spring_base.fundamentals.service.cep;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompletableFutureFetcherTest {

    @Mock
    private CepApiClient cepApiClient;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @InjectMocks
    private CompletableFutureFetcher completableFutureFetcher;

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter(anyString(), any(String[].class))).thenReturn(counter);
    }

    @Test
    @DisplayName("CompletableFuture: should return CEP data")
    void shouldReturnCepDataWithCompletableFuture() {
        // ARRANGE
        when(cepApiClient.fetchViaCep("83402220")).thenReturn("fake cep response");
        when(cepApiClient.fetchNationalize("83402220")).thenReturn("fake nationalize response");

        // ACT
        Map<String, Object> result = completableFutureFetcher.fetch("83402220");

        // ASSERT
        assertEquals("fake cep response", result.get("viaCep"));
        assertEquals("fake nationalize response", result.get("nationalize"));
        assertEquals("CompletableFuture", result.get("method"));
    }

    @Test
    @DisplayName("CompletableFuture: should contain execution time")
    void shouldContainExecutionTimeInResult() {
        // ARRANGE
        when(cepApiClient.fetchViaCep("83402220")).thenReturn("fake cep response");
        when(cepApiClient.fetchNationalize("83402220")).thenReturn("fake nationalize response");

        // ACT
        Map<String, Object> result = completableFutureFetcher.fetch("83402220");

        // ASSERT
        assertNotNull(result.get("elapsedMs"));
    }

}
