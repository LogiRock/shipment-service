package com.logirock.shipment_service.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logirock.shipment_service.models.HistorialEstadoEnvio;

public interface HistorialEstadoEnvioRepository
        extends JpaRepository<HistorialEstadoEnvio, UUID> {

    List<HistorialEstadoEnvio> findByEnvio_IdOrderByFechaCambioDesc(UUID envioId);
}
