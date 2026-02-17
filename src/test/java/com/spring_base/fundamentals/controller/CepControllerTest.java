package com.spring_base.fundamentals.controller;

import com.spring_base.fundamentals.service.CepService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CepController.class)
public class CepControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CepService cepService;

    @Test
    @DisplayName("GET v1: deve retornar 200 com dados do CEP via CompletableFuture")
    void deveRetornar200ComDadosDoCepViaCompletableFuture() throws Exception {
        // ARRANGE
        when(cepService.fetchCepCompletableFuture("83402220"))
                .thenReturn(Map.of("viaCep", "{}", "nationalize", "{}", "elapsedMs", 0L, "method", "CompletableFuture"));

        // ACT + ASSERT
        mockMvc.perform(get("/cep/v1/83402220"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method").value("CompletableFuture"));
    }

    @Test
    @DisplayName("GET v2: deve retornar 200 com dados do CEP via Virtual Threads")
    void deveRetornar200ComDadosDoCepViaVirtualThreads() throws Exception {
        // ARRANGE
        when(cepService.fetchCepVirtualThreads("83402220"))
                .thenReturn(Map.of("viaCep", "{}", "nationalize", "{}", "elapsedMs", 0L, "method", "Virtual Threads"));

        // ACT + ASSERT
        mockMvc.perform(get("/cep/v2/83402220"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method").value("Virtual Threads"));
    }

    @Test
    @DisplayName("GET v1: deve retornar 500 quando service falhar")
    void deveLancarExceptionQuandoServiceFalhar() throws Exception {
        // ARRANGE
        when(cepService.fetchCepCompletableFuture("83402220"))
                .thenThrow(new RuntimeException("Error fetching data"));

        // ACT + ASSERT
        mockMvc.perform(get("/cep/v1/83402220"))
                .andExpect(status().isInternalServerError());
    }

}
