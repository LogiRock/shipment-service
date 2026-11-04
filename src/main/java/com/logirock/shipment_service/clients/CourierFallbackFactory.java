package com.logirock.shipment_service.clients;

import java.util.UUID;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.logirock.shipment_service.dto.CourierDto;
import com.logirock.shipment_service.dto.DisponibilidadRepartidorResponseDto;
import com.logirock.shipment_service.exceptions.CourierServiceNoDisponibleException;
import com.logirock.shipment_service.exceptions.RepartidorNoEncontradoException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CourierFallbackFactory implements FallbackFactory<CourierClient> {
    @Override
    public CourierClient create(Throwable cause) {
        log.warn("No se pudo consultar courier-service: {}", cause.toString());
        return new CourierClient() {
            private RuntimeException mappedFailure() {
                if (cause instanceof RepartidorNoEncontradoException notFound) {
                    return notFound;
                }
                if (cause instanceof CourierServiceNoDisponibleException unavailable) {
                    return unavailable;
                }
                return new CourierServiceNoDisponibleException("Courier Service no está disponible.", cause);
            }
            @Override public CourierDto obtenerRepartidor(UUID id) { throw mappedFailure(); }
            @Override public CourierDto obtenerRepartidorAutenticado() { throw mappedFailure(); }
            @Override public DisponibilidadRepartidorResponseDto  validarDisponibilidad(UUID id) { throw mappedFailure(); }
        };
    }
}
