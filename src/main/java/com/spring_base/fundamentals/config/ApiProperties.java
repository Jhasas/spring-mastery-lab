package com.spring_base.fundamentals.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.api")
public record ApiProperties(
    @NotNull ViaCep viacep,
    @NotNull Second second
) {
    public record ViaCep(@NotBlank String url) {}
    public record Second(@NotBlank String url) {}
}
