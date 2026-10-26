package com.logirock.shipment_service.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.models.Cliente;
import com.logirock.shipment_service.models.Direccion;
import com.logirock.shipment_service.models.Envio;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:shipment-courier-refactor;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EnvioAsignacionJpaTest {

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void courierIdLogicoSePersisteSinRelacionConEntidadCourier() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Ana Pérez");
        cliente.setEmail("ana.courier@example.com");
        cliente.setTelefono("+5491100000000");
        cliente = clienteRepository.saveAndFlush(cliente);

        UUID courierId = UUID.randomUUID();
        Envio envio = crearEnvio("ENV-AAAA0001", courierId, cliente);
        envioRepository.saveAndFlush(envio);
        UUID envioId = envio.getId();
        entityManager.clear();

        Envio envioPersistido = envioRepository.findById(envioId).orElseThrow();

        assertEquals(courierId, envioPersistido.getCourierId());
    }

    private Envio crearEnvio(String codigo, UUID courierId, Cliente cliente) {
        Envio envio = new Envio();
        envio.setCodigoSeguimiento(codigo);
        envio.setPeso(2.0);
        envio.setAncho(10.0);
        envio.setAlto(20.0);
        envio.setLargo(30.0);
        envio.setEstado(EstadoEnvio.PENDIENTE);
        envio.setOrigen(direccion());
        envio.setDestino(direccion());
        envio.setCourierId(courierId);
        envio.setCliente(cliente);
        return envio;
    }

    private Direccion direccion() {
        return Direccion.builder()
                .calle("San Martín")
                .numero("123")
                .ciudad("Córdoba")
                .codigoPostal("5000")
                .build();
    }
}
