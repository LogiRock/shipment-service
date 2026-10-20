
package com.logirock.shipment_service.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.models.Cliente;
import com.logirock.shipment_service.models.Direccion;
import com.logirock.shipment_service.models.Envio;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class EnvioRepositoryTest {

    @Autowired
    private EnvioRepository envioRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void guardarEnvioYBuscarPorId() {
        Envio envio = crearEnvio();

        Envio guardado = envioRepository.saveAndFlush(envio);

        Optional<Envio> encontrado = envioRepository.findById(guardado.getId());

        assertTrue(encontrado.isPresent());
        assertEquals(guardado.getId(), encontrado.get().getId());
        assertEquals("TRK-TEST-001", encontrado.get().getCodigoSeguimiento());
    }

    @Test
    void buscarPorCodigoSeguimiento() {
        Envio envio = crearEnvio();
        envioRepository.saveAndFlush(envio);

        Optional<Envio> encontrado = envioRepository.findByCodigoSeguimiento("TRK-TEST-001");

        assertTrue(encontrado.isPresent());
        assertEquals("TRK-TEST-001",
                encontrado.get().getCodigoSeguimiento());
    }

    @Test
    void buscarPorEstado() {
        Envio envio = crearEnvio();
        envio.setEstado(EstadoEnvio.PENDIENTE);
        envioRepository.saveAndFlush(envio);

        List<Envio> encontrados = envioRepository.findByEstado(EstadoEnvio.PENDIENTE);

        assertFalse(encontrados.isEmpty());
        assertTrue(encontrados.stream()
                .anyMatch(e -> "TRK-TEST-001".equals(e.getCodigoSeguimiento())));
    }

    @Test
    void verificarSiExisteCodigoSeguimiento() {
        envioRepository.saveAndFlush(crearEnvio());

        assertTrue(
                envioRepository.existsByCodigoSeguimiento("TRK-TEST-001"));
        assertFalse(
                envioRepository.existsByCodigoSeguimiento("TRK-NO-EXISTE"));
    }

    @Test
    void buscarPorCodigoInexistenteDevuelveOptionalVacio() {
        Optional<Envio> encontrado = envioRepository.findByCodigoSeguimiento("TRK-NO-EXISTE");

        assertTrue(encontrado.isEmpty());
    }

    private Envio crearEnvio() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente de prueba");
        cliente.setEmail("cliente.test@example.com");
        cliente.setTelefono("3884000000");

        cliente = clienteRepository.saveAndFlush(cliente);

        Direccion origen = Direccion.builder()
                .calle("Belgrano")
                .numero("123")
                .ciudad("San Salvador de Jujuy")
                .codigoPostal("4600")
                .build();

        Direccion destino = Direccion.builder()
                .calle("Sarmiento")
                .numero("456")
                .ciudad("San Salvador de Jujuy")
                .codigoPostal("4600")
                .build();

        Envio envio = new Envio();
        envio.setCodigoSeguimiento("TRK-TEST-001");
        envio.setPeso(2.0);
        envio.setAncho(10.0);
        envio.setAlto(20.0);
        envio.setLargo(30.0);
        envio.setEstado(EstadoEnvio.PENDIENTE);
        envio.setCliente(cliente);
        envio.setOrigen(origen);
        envio.setDestino(destino);

        return envio;
    }
}
