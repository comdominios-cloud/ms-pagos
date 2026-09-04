# ms-pagos

Microservicio de **cuotas de mantenimiento y pagos** del Sistema de
Administracion de Condominios.

> CS2032 Cloud Computing - UTEC | Proyecto: Sistema de Administracion de Condominios

## Responsable

Integrante a cargo de **API con BD #2**. Este repositorio es **autonomo**: se
desarrolla, prueba y despliega sin depender del avance de los demas
microservicios.

## Dominio

Emite las cuotas de mantenimiento de cada unidad por periodo y registra los
pagos que se hacen contra ellas (incluyendo pagos parciales), manteniendo el
estado de cada cuota: `PENDIENTE`, `PARCIAL`, `PAGADA`, `VENCIDA` o `ANULADA`.

`unidad_id` y `residente_id` se guardan como **identificadores logicos** (el id
que tiene ese registro en ms-residentes). Este microservicio **no llama** a
ningun otro: el cruce de informacion entre servicios lo resuelve
**ms-ficha-residente**, que es el consumidor de APIs del proyecto.

```
web-condominio ──> API Gateway ──> ms-pagos ──> PostgreSQL
                                      ^
                                      └── ms-ficha-residente (consume esta API)
```

## Stack

| Elemento    | Tecnologia                          |
|-------------|-------------------------------------|
| Lenguaje    | Java 21                             |
| Framework   | Spring Boot 3.3 (Web + Data JPA)    |
| Base de datos | PostgreSQL 16 (SQL)               |
| Documentacion | Swagger-UI en `/swagger-ui.html` (springdoc-openapi) |
| Build       | Maven                               |
| Contenedor  | Docker (multi-stage)                |

**Tablas relacionadas:** `cuotas` <- `pagos`.
Ver [docs/schema.sql](docs/schema.sql) y [docs/der.md](docs/der.md).

## Puerto asignado

**8002**

| Microservicio       | Puerto |
|---------------------|--------|
| ms-residentes       | 8001   |
| ms-pagos            | **8002** |
| ms-incidencias      | 8003   |
| ms-ficha-residente  | 8004   |
| ms-analitico        | 8005   |
| web-condominio (dev)| 5173   |

## Endpoints REST planificados

> Andamiaje: aun no implementados.

| # | Metodo | Ruta | Descripcion | Consumido por |
|---|--------|------|-------------|---------------|
| 1 | `GET`  | `/cuotas?unidad_id=&estado=` | Lista cuotas filtrables por unidad y estado | **frontend** |
| 2 | `GET`  | `/cuotas/{cuota_id}` | Detalle de una cuota con sus pagos | frontend |
| 3 | `POST` | `/cuotas` | Emite una cuota de mantenimiento | frontend |
| 4 | `GET`  | `/pagos?unidad_id=` | Lista pagos registrados | **frontend** |
| 5 | `POST` | `/pagos` | Registra un pago contra una cuota | frontend |
| 6 | `GET`  | `/unidades/{unidad_id}/estado-cuenta` | Deuda total y cuotas pendientes de la unidad | ms-ficha-residente |
| 7 | `GET`  | `/health` | Health check del servicio | infra |

Los dos endpoints que consume directamente el **frontend** son
`GET /cuotas` y `GET /pagos`.

Documentacion interactiva: `http://localhost:8002/swagger-ui.html`.

## Variables de entorno

Copiar [.env.example](.env.example) a `.env` y completar. **Nunca** commitear `.env`.

| Variable | Descripcion | Ejemplo |
|----------|-------------|---------|
| `APP_NAME` | Nombre del servicio | `ms-pagos` |
| `APP_PORT` | Puerto de escucha | `8002` |
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring | `dev` / `prod` |
| `POSTGRES_HOST` | Host de PostgreSQL | `postgres` (nombre del servicio en Compose) |
| `POSTGRES_PORT` | Puerto de PostgreSQL | `5432` |
| `POSTGRES_DB` | Nombre de la base | `condominio_pagos` |
| `POSTGRES_USER` | Usuario de la base | *(sin valor en el repo)* |
| `POSTGRES_PASSWORD` | Password del usuario | *(sin valor en el repo)* |
| `SPRING_DATASOURCE_URL` | Cadena JDBC completa | `jdbc:postgresql://postgres:5432/condominio_pagos` |

## Como levantar con Docker

### Solo el microservicio

```bash
cp .env.example .env      # completar credenciales
docker build -t ms-pagos .
docker run --rm -p 8002:8002 --env-file .env ms-pagos
```

Luego abrir `http://localhost:8002/swagger-ui.html`.

### Con PostgreSQL incluido (docker compose)

```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    ports: ["5432:5432"]
    volumes:
      - pg_data:/var/lib/postgresql/data
      - ./docs/schema.sql:/docker-entrypoint-initdb.d/01-schema.sql

  ms-pagos:
    build: .
    ports: ["8002:8002"]
    env_file: .env
    depends_on: [postgres]

volumes:
  pg_data:
```

```bash
docker compose up --build
```

## Estructura

```
src/main/java/pe/edu/utec/condominio/pagos/
├── MsPagosApplication.java   # arranque de Spring Boot (stub)
├── controller/               # endpoints REST
├── service/                  # logica de negocio
├── repository/               # repositorios JPA
├── model/                    # entidades (cuotas, pagos)
└── config/                   # OpenAPI, CORS
src/main/resources/application.yml
docs/
├── der.md              # diagrama entidad-relacion (placeholder)
├── schema.sql          # DDL inicial
└── seed_fake_data.py   # carga masiva de 20,000 registros (placeholder)
```

## Estado

Andamiaje inicial. Sin endpoints ni logica de negocio implementados.
