package com.spring_base.fundamentals.controller;

import com.spring_base.fundamentals.service.cep.CepFetcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/cep")
public class CepController {

    private final CepFetcher v1Fetcher;
    private final CepFetcher v2Fetcher;

    public CepController(
        @Qualifier("v1") CepFetcher v1Fetcher,
        @Qualifier("v2") CepFetcher v2Fetcher
    ){
        this.v1Fetcher = v1Fetcher;
        this.v2Fetcher = v2Fetcher;
    }


    @GetMapping("/v1/{cep}")
    public Map<String, Object> getCepCompletableFuture(@PathVariable String cep) {
        return v1Fetcher.fetch(cep);
    }

    @GetMapping("/v2/{cep}")
    public Map<String, Object> getCepVirtualThreads(@PathVariable String cep) {
        return v2Fetcher.fetch(cep);
    }
}
