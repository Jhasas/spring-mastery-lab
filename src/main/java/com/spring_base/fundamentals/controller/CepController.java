package com.spring_base.fundamentals.controller;

import com.spring_base.fundamentals.service.CepService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/cep")
@RequiredArgsConstructor
public class CepController {

    private final CepService cepService;

    @GetMapping("/v1/{cep}")
    public Map<String, Object> getCepCompletableFuture(@PathVariable String cep) {
        return cepService.buscaCepCompletableFuture(cep);
    }

    @GetMapping("/v2/{cep}")
    public Map<String, Object> getCepVirtualThreads(@PathVariable String cep) {
        return cepService.buscaCepVirtualThreads(cep);
    }
}
