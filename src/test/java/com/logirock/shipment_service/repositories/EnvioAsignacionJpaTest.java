package com.logirock.shipment_service.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.enums.EstadoRepartidor;
import com.logirock.shipment_service.models.Direccion;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.models.Repartidor;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:shipment-assignment;DB_CLOSE_DELAY=-1",
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
    private RepartidorRepository repartidorRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void unRepartidorPuedeTenerVariosEnviosYLaAsignacionSePersiste() {
        Repartidor repartidor = new Repartidor();
        repartidor.setNombre("Juan Pérez");
        repartidor.setTipoVehiculo("Motocicleta");
        repartidor.setEstado(EstadoRepartidor.ACTIVO);
        repartidor = repartidorRepository.saveAndFlush(repartidor);

        Envio primerEnvio = crearEnvio("ENV-AAAA0001", repartidor);
        Envio segundoEnvio = crearEnvio("ENV-AAAA0002", repartidor);
        envioRepository.saveAndFlush(primerEnvio);
        envioRepository.saveAndFlush(segundoEnvio);
        UUID repartidorId = repartidor.getId();
        UUID primerEnvioId = primerEnvio.getId();
        UUID segundoEnvioId = segundoEnvio.getId();
        entityManager.clear();

        Envio envioPersistido = envioRepository.findById(primerEnvioId).orElseThrow();
        Envio segundoEnvioPersistido = envioRepository.findById(segundoEnvioId).orElseThrow();

        assertNotNull(envioPersistido.getRepartidor());
        assertEquals(repartidorId, envioPersistido.getRepartidor().getId());
        assertEquals(repartidorId, segundoEnvioPersistido.getRepartidor().getId());
    }

    private Envio crearEnvio(String codigo, Repartidor repartidor) {
        Envio envio = new Envio();
        envio.setCodigoSeguimiento(codigo);
        envio.setPeso(2.0);
        envio.setAncho(10.0);
        envio.setAlto(20.0);
        envio.setLargo(30.0);
        envio.setEstado(EstadoEnvio.PENDIENTE);
        envio.setOrigen(direccion());
        envio.setDestino(direccion());
        envio.setRepartidor(repartidor);
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
