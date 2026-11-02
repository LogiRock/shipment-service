package com.logirock.shipment_service.dto;

import com.logirock.shipment_service.models.Envio;
import org.springframework.stereotype.Component;

@Component
public class EnvioMapper {
    public EnvioResponseDto toDto(Envio envio) {
        if (envio == null) {
            return null;
        }

        return new EnvioResponseDto(
                envio.getId(),
                envio.getCodigoSeguimiento(),
                envio.getPeso(),
                envio.getAncho(),
                envio.getAlto(),
                envio.getLargo(),
                envio.getEstado(),
                envio.getCourierId(),
                envio.getCliente() != null ? envio.getCliente().getId() : null,
                envio.getCliente() != null ? envio.getCliente().getNombre() : null,
                envio.getOrigen(),
                envio.getDestino()
        );
    }
}
