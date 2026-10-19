package com.logirock.shipment_service.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logirock.shipment_service.models.Repartidor;

/**
 * Repositorio de acceso a datos para la entidad Repartidor.
 * Proporciona las operaciones CRUD base heredadas de JpaRepository.
 */
public interface RepartidorRepository extends JpaRepository<Repartidor, UUID> {

}