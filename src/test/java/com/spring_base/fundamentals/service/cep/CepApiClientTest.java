package com.spring_base.fundamentals.service.cep;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class CepApiClientTest {

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
    private CepApiClient cepApiClient;

    @BeforeEach
    void setUp() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    @DisplayName("FetchViaCep should return correct response")
    void shouldReturnCorrectResponseViaFetchViaCep() {
        // ARRANGE
        when(apiProperties.viacep()).thenReturn(new ApiProperties.ViaCep("http://fake-viecep"));
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("fake response"));

        // ACT
        String result = cepApiClient.fetchViaCep("83402220");

        // ASSERT
        assertEquals("fake response", result);
    }

    @Test
    @DisplayName("Nacionalize should return correct response")
    void shouldReturnCorrectResponseViaFetchNacionalize() {
        // ARRANGE
        when(apiProperties.second()).thenReturn(new ApiProperties.Second("http://fake-nationalize"));
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("fake response"));

        // ACT
        String result = cepApiClient.fetchNationalize("83402220");

        // ASSERT
        assertEquals("fake response", result);
    }

    @Test
    @DisplayName("Should throw exception when fetchViaCep fails")
    void shouldThrowExceptionWhenFetchViaCepFails() {
        // ARRANGE
        when(apiProperties.viacep()).thenReturn(new ApiProperties.ViaCep("http://fake-viecep"));
        when(responseSpec.bodyToMono(String.class))
                .thenThrow(new RuntimeException("API down"));

        // ACT
        // ASSERT
        assertThrows(RuntimeException.class, () -> {
            cepApiClient.fetchViaCep("83402220");
        });
    }

}
