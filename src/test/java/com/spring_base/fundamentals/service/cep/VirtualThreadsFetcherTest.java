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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VirtualThreadsFetcherTest {

    @Mock
    private CepApiClient cepApiClient;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @InjectMocks
    private VirtualThreadsFetcher virtualThreadsFetcher;

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter(anyString(), any(String[].class))).thenReturn(counter);
    }

    @Test
    @DisplayName("VirtualThreads: should return CEP data")
    void shouldReturnCepDataWithVirtualThreads() {
        // ARRANGE
        when(cepApiClient.fetchViaCep("83402220")).thenReturn("fake cep response");
        when(cepApiClient.fetchNationalize("83402220")).thenReturn("fake nationalize response");

        // ACT
        Map<String, Object> result = virtualThreadsFetcher.fetch("83402220");

        // ASSERT
        assertEquals("fake cep response", result.get("viaCep"));
        assertEquals("fake nationalize response", result.get("nationalize"));
        assertEquals("Virtual Threads", result.get("method"));
    }

    @Test
    @DisplayName("VirtualThreads: should contain execution time")
    void shouldContainExecutionTimeWithVirtualThreads() {
        // ARRANGE
        when(cepApiClient.fetchViaCep("83402220")).thenReturn("fake cep response");
        when(cepApiClient.fetchNationalize("83402220")).thenReturn("fake nationalize response");

        // ACT
        Map<String, Object> result = virtualThreadsFetcher.fetch("83402220");

        // ASSERT
        assertNotNull(result.get("elapsedMs"));
    }

    @Test
    @DisplayName("Should throw exception when CEP API fails")
    void shouldThrowExceptionWhenCepApiFails() {
        // ARRANGE
        when(cepApiClient.fetchViaCep("83402220")).thenThrow(new RuntimeException("API down"));

        // ACT
        // ASSERT
        assertThrows(RuntimeException.class, () -> {
            virtualThreadsFetcher.fetch("83402220");
        });
    }

}
