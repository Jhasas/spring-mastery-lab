package com.spring_base.fundamentals.service.cep;

import org.mockito.Mock;
import org.springframework.web.reactive.function.client.WebClient;

public class CepApiClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

}
