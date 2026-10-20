package com.logirock.shipment_service.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass 
@EntityListeners(AuditingEntityListener.class)
@Getter 
@Setter 
public abstract class Auditable {

	@CreatedDate 
	@Column (name = "fecha_creacion", updatable = false)
	private LocalDateTime fechaCreacion;
	@LastModifiedDate 
	@Column(name = "fecha_ultima_actualizacion")
	private LocalDateTime fechaUltimaActualizacion;
	
}