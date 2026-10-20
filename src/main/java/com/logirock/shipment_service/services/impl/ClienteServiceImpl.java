package com.logirock.shipment_service.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logirock.shipment_service.dto.ClienteRequestDto;
import com.logirock.shipment_service.dto.ClienteResponseDto;
import com.logirock.shipment_service.models.Cliente;
import com.logirock.shipment_service.repositories.ClienteRepository;
import com.logirock.shipment_service.services.ClienteService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public ClienteResponseDto registrarCliente(ClienteRequestDto request) {
        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ingresado ya se encuentra registrado.");
        }
        if (clienteRepository.existsByTelefono(request.getTelefono())) {
            throw new IllegalArgumentException("El teléfono ingresado ya se encuentra registrado.");
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());

        cliente = clienteRepository.save(cliente);
        return mapearAResponseDto(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDto obtenerCliente(UUID id) {
        Cliente cliente = buscarClientePorId(id);
        return mapearAResponseDto(cliente);
    }

    @Override
    @Transactional
    public ClienteResponseDto modificarCliente(UUID id, ClienteRequestDto request) {
        Cliente cliente = buscarClientePorId(id);

        // Validar si quiere cambiar el email y si el nuevo ya está en uso por otro
        // usuario
        if (!cliente.getEmail().equals(request.getEmail()) && clienteRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El nuevo email ya se encuentra registrado por otro cliente.");
        }

        cliente.setNombre(request.getNombre());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());

        cliente = clienteRepository.save(cliente);
        return mapearAResponseDto(cliente);
    }

    @Override
    @Transactional
    public void eliminarCliente(UUID id) {
        Cliente cliente = buscarClientePorId(id);
        clienteRepository.delete(cliente);
    }

    // Métodos privados auxiliares para no repetir código
    private Cliente buscarClientePorId(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró ningún cliente con el ID: " + id));
    }

    private ClienteResponseDto mapearAResponseDto(Cliente cliente) {
        return new ClienteResponseDto(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getEmail(),
                cliente.getTelefono());
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscarEntidadPorId(UUID id) {
        return buscarClientePorId(id);
    }
}