package com.logirock.shipment_service.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logirock.shipment_service.repositories.EnvioRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Pruebas unitarias para el Value Object Direccion y su integración con Envio.
 * Verifica validaciones en memoria y persistencia mediante Mockito.
 */
@ExtendWith(MockitoExtension.class)
class DireccionTest {
    /**
     * Validador de Jakarta para comprobar restricciones.
     */
    private static Validator validator;

    /**
     * Repositorio simulado para verificar la persistencia.
     */
    @Mock
    private EnvioRepository envioRepository;

    /**
     * Inicializa la fábrica de validadores de Jakarta.
     */
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Verifica que una dirección con todos sus campos completos no genere violaciones.
     */
    @Test
    void deberiaPasarValidacion_CuandoDireccionEsValida() {
        Direccion direccion = Direccion.builder()
                .calle("Av. Fascio")
                .numero("1050")
                .ciudad("San Salvador de Jujuy")
                .codigoPostal("4600")
                .build();

        Set<ConstraintViolation<Direccion>> irregularidades = validator.validate(direccion);

        assertTrue(irregularidades.isEmpty());
    }

    /**
     * Verifica que se detecten errores si un Value Object es instanciado sin sus atributos obligatorios.
     */
    @Test
    void deberiaFallarValidacion_CuandoFaltanAtributosEnDireccion() {
        Direccion direccionInvalida = new Direccion();

        Set<ConstraintViolation<Direccion>> irregularidades = validator.validate(direccionInvalida);

        assertFalse(irregularidades.isEmpty());
        assertEquals(4, irregularidades.size());
    }

    /**
     * Simula y verifica la persistencia de un Envio asegurando que sus direcciones embebidas se mantengan intactas.
     */
    @Test
    void deberiaPersistirEnvio_ConDireccionesEmbebidas() {
        Direccion origen = new Direccion("Alvear", "123", "San Salvador de Jujuy", "4600");
        Direccion destino = new Direccion("Lavalle", "456", "San Salvador de Jujuy", "4600");

        Envio envio = new Envio();
        envio.setId(UUID.randomUUID());
        envio.setOrigen(origen);
        envio.setDestino(destino);

        when(envioRepository.save(any(Envio.class))).thenReturn(envio);

        Envio envioGuardado = envioRepository.save(envio);

        assertNotNull(envioGuardado);
        assertEquals("Alvear", envioGuardado.getOrigen().getCalle());
        assertEquals("Lavalle", envioGuardado.getDestino().getCalle());
        verify(envioRepository, times(1)).save(any(Envio.class));
    }
}