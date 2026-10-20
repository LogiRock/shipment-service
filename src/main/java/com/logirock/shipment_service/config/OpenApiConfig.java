package com.logirock.shipment_service.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "LogiTrack Shipment Service API",
        version = "v1",
        description = "API REST para gestionar clientes, envíos y repartidores de LogiTrack."))
public class OpenApiConfig {
}
