package com.logirock.shipment_service.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.logirock.shipment_service.models.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    Optional<Cliente> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByTelefono(String telefono);
}