package com.logirock.shipment_service.clients;

import com.logirock.shipment_service.dto.CourierDto;
import com.logirock.shipment_service.dto.DisponibilidadRepartidorResponseDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(
        name = "COURIER-SERVICE",
        fallbackFactory = CourierFallbackFactory.class,
        configuration = CourierClientConfiguration.class)
public interface CourierClient {

    @GetMapping("/api/v1/repartidores/{id}")
    CourierDto obtenerRepartidor(@PathVariable("id") UUID id);

    @GetMapping("/api/v1/repartidores/me")
    CourierDto obtenerRepartidorAutenticado();

    @GetMapping("/api/v1/repartidores/{id}/disponibilidad")
    DisponibilidadRepartidorResponseDto validarDisponibilidad(@PathVariable("id") UUID id);
}
