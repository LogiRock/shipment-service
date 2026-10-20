# 📦 Shipment Service — LogiTrack

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-green.svg)](http://localhost:8080/swagger-ui.html)

Microservicio REST transaccional perteneciente a la plataforma **LogiTrack**. Se encarga de administrar clientes, repartidores, la creación y ciclo de vida de los envíos, la asignación de personal de entrega y el historial de auditoría de los cambios de estado.

---

## 📋 Tabla de Contenidos
- [Requisitos Previos](#-requisitos-previos)
- [Ejecución con Docker (Recomendado)](#-ejecución-con-docker-recomendado)
- [Ejecución Local Manual](#-ejecución-local-manual)
- [Variables de Entorno](#-variables-de-entorno)
- [Documentación de la API (Swagger)](#-documentación-de-la-api-swagger)
- [Endpoints Principales](#-endpoints-principales)
- [Ejecución de Pruebas](#-ejecución-de-pruebas)
- [Decisiones de Arquitectura](#-decisiones-de-arquitectura)

---

## ⚙️ Requisitos Previos

- **Java 25** + **Spring Boot 3.x**
- **MySQL 8.0** (en caso de ejecución manual)
- **Docker & Docker Compose** (opcional para entornos contenedorizados)
- **Git**

---

## 🐳 Ejecución con Docker 

El proyecto incluye soporte multi-stage build para empaquetar el microservicio y su base de datos MySQL en un solo comando:

```powershell
docker-compose up --build -d 
```
---

## 💻 Ejecución Local Manual

Clonar el repositorio:

```powershell
git clone [https://github.com/LogiRock/shipment-service.git](https://github.com/LogiRock/shipment-service.git)
cd shipment-service
```
Crear la Base de Datos:
Crear en MySQL un esquema vacío llamado shipment_service
Configurar Variables de Entorno (PowerShell)

```PowerShell
$env:DB_SUBPROTOCOL = "mysql"
$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_DATABASE_NAME = "shipment_service"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "tu_password"
```
Iniciar la aplicación:
```PowerShell
.\mvnw.cmd spring-boot:run
```

---

## 🔐 Variables de Entorno

| Variable | Descripción | Ejemplo Local |
| --- | --- | --- |
| `DB_SUBPROTOCOL` | Motor JDBC | `mysql` |
| `DB_HOST` | Host de la Base de Datos | `localhost` |
| `DB_PORT` | Puerto de MySQL | `3306` |
| `DB_DATABASE_NAME` | Nombre de la BD | `shipment_service` |
| `DB_USERNAME` | Usuario | `root` |
| `DB_PASSWORD` | Contraseña | `******` |


---

## 📖 Documentación de la API (Swagger / OpenAPI)

Una vez iniciada la aplicación, podés acceder a la interfaz interactiva para explorar y probar los endpoints:

* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
* **OpenAPI Spec (JSON):** `http://localhost:8080/v3/api-docs`

--- 

## 🚀 Endpoints Principales (API REST)

### 👤 Clientes (`/api/v1/clientes`)
- `POST /api/v1/clientes` — Registrar cliente.
- `GET /api/v1/clientes/{id}` — Consultar cliente por UUID.
- `PUT /api/v1/clientes/{id}` — Actualizar cliente.
- `DELETE /api/v1/clientes/{id}` — Eliminar cliente.

### 🛵 Repartidores (`/api/v1/repartidores`)
- `POST /api/v1/repartidores` — Registrar repartidor (`ACTIVO` / `INACTIVO`).
- `GET /api/v1/repartidores` — Listar todos los repartidores.
- `GET /api/v1/repartidores/{id}` — Consultar por UUID.

### 📦 Envíos (`/api/envios`)
- `POST /api/envios` — Crear envío (Estado inicial: `PENDIENTE`, calcula tarifa y genera Tracking Code).
- `GET /api/envios/{id}` — Consultar envío por UUID.
- `GET /api/envios/seguimiento/{codigo}` — Consultar por Tracking Code.
- `PUT /api/envios/{envioId}/repartidor/{repartidorId}` — Asignar repartidor activo.
- `PATCH /api/envios/{id}/estado` — Transición de estado (`PENDIENTE` ➔ `EN_RUTA` ➔ `ENTREGADO` / `CANCELADO`).
- `GET /api/envios/{id}/historial` — Historial de auditoría del envío.

---

## 🧪 Ejecución de Pruebas

Las pruebas ejecutan de forma aislada sobre una base de datos **H2 en memoria**:

```powershell
.\mvnw.cmd test
```
Incluye cobertura de:

Pruebas unitarias para la máquina de estados y cálculo de tarifa (JUnit 5 + Mockito).
Pruebas de integración para la capa de datos (@DataJpaTest).

---

## 🏛️ Decisiones de Arquitectura y Buenas Prácticas

- **Diseño en capas:** Separation of Concerns (`Controller`, `Service`, `Repository`, `Entity`, `DTO`).
- **Desacoplamiento:** Uso obligatorio de DTOs para no exponer entidades del dominio.
- **Identificadores:** Uso de `UUID` en todas las entidades principales.
- **Auditoría:** Guardado automático de historial ante cambios en el ciclo de vida de los envíos.
- **Gestión de Errores:** Manejo centralizado mediante `@RestControllerAdvice` retornando códigos HTTP estandarizados (`400`, `404`, `409`, etc.).

