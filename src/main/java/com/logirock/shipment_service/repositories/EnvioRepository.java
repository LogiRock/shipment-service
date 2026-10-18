package com.logirock.shipment_service.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.models.Envio;

public interface EnvioRepository extends JpaRepository<Envio, UUID>{

    Optional<Envio> findByCodigoSeguimiento(String codigoSeguimiento);

    List<Envio> findByEstado(EstadoEnvio estado);

    boolean existsByCodigoSeguimiento(String codigoSeguimiento);
    
} 
