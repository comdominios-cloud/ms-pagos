# ms-pagos

Microservicio de **cuotas de mantenimiento y pagos** del Sistema de
Administracion de Condominios.

> CS2032 Cloud Computing - UTEC | Proyecto: Sistema de Administracion de Condominios

## Responsable

[@sebastianperez72](https://github.com/sebastianperez72) — API con base de datos (Java).
Ver [INTEGRANTE.md](INTEGRANTE.md).

Repositorio **autonomo**: se desarrolla, prueba y despliega sin depender del
avance de los demas microservicios. Es el **segundo microservicio** que se
demuestra con Postman en el avance del 50%.

## Dominio

Emite las cuotas de mantenimiento de cada unidad por periodo y registra los
pagos que se hacen contra ellas (incluyendo pagos parciales), manteniendo el
estado de cada cuota: `PENDIENTE`, `PARCIAL`, `PAGADA`, `VENCIDA` o `ANULADA`.

`unidad_id` y `residente_id` se guardan como **identificadores logicos** (el id
que tiene ese registro en ms-residentes). Este microservicio **no llama** a
ningun otro: el cruce de informacion entre servicios lo resuelve
**ms-ficha-residente**, que es el consumidor de APIs del proyecto.

```
web-condominio ──> balanceador ──> ms-pagos :9002 ──> MySQL :3306
                                       ^              (VM de base de datos)
                                       ├── ms-ficha-residente (consume esta API)
                                       └── ingesta02 (lee la BD y la vuelca a S3)
```

## Stack

| Elemento    | Tecnologia                          |
|-------------|-------------------------------------|
| Lenguaje    | Java 21                             |
| Framework   | Spring Boot 3.3 (Web + Data JPA)    |
| Base de datos | **MySQL 8** (SQL)                 |
| Documentacion | Swagger-UI en `/swagger-ui.html` (springdoc-openapi) |
| Build       | Maven                               |
| Contenedor  | Docker (multi-stage)                |

**Tablas relacionadas:** `cuotas` <- `pagos`.
Ver [docs/schema.sql](docs/schema.sql) y [docs/der.md](docs/der.md).

## Puerto asignado

**9002** publicado · **8080** dentro del contenedor (default de Spring Boot).

El curso asigno el rango **9000-12000** para los microservicios; ese es el puerto
que se habilita en el Security Group.

| Microservicio | Publicado | Interno |
|---------------|-----------|---------|
| ms-residentes | 9001      | 8000    |
| ms-pagos      | **9002**  | 8080    |
| ms-incidencias| 9003      | 3003    |
| ms-ficha-residente | 9004 | 8004    |
| ms-analitico  | 9005      | 8005    |
| web-condominio (dev) | 5173 | —     |

Las bases de datos **no** entran en ese rango: PostgreSQL 5432, MySQL 3306,
MongoDB 27017, alcanzables solo desde los Security Groups de la VM de produccion
y la VM de ingesta.

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

Documentacion interactiva: `http://<ip-vm-produccion>:9002/swagger-ui.html`.

## Variables de entorno

Copiar [.env.example](.env.example) a `.env` y completar. **Nunca** commitear `.env`.

| Variable | Descripcion | Ejemplo |
|----------|-------------|---------|
| `APP_NAME` | Nombre del servicio | `ms-pagos` |
| `APP_PORT` | Puerto dentro del contenedor | `8080` |
| `PUBLISHED_PORT` | Puerto publicado en la VM | `9002` |
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring | `dev` / `prod` |
| `MYSQL_HOST` | IP privada de la VM de base de datos | *(sin valor en el repo)* |
| `MYSQL_PORT` | Puerto de MySQL | `3306` |
| `MYSQL_DATABASE` | Nombre de la base | `condominio_pagos` |
| `MYSQL_USER` | Usuario de la base | *(sin valor en el repo)* |
| `MYSQL_PASSWORD` | Password del usuario | *(sin valor en el repo)* |
| `SPRING_DATASOURCE_URL` | Cadena JDBC completa | `jdbc:mysql://<ip-vm-bd>:3306/condominio_pagos` |

## Como levantar con Docker

### Solo el microservicio

```bash
cp .env.example .env      # completar credenciales
docker build -t ms-pagos .
docker run --rm -p 9002:8080 --env-file .env ms-pagos
```

Luego abrir `http://localhost:9002/swagger-ui.html`.

### Con MySQL incluido (desarrollo local)

```yaml
services:
  mysql:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_PASSWORD}
      MYSQL_DATABASE: ${MYSQL_DATABASE}
      MYSQL_USER: ${MYSQL_USER}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    ports: ["3306:3306"]
    volumes:
      - mysql_data:/var/lib/mysql
      - ./docs/schema.sql:/docker-entrypoint-initdb.d/01-schema.sql

  ms-pagos:
    build: .
    ports: ["9002:8080"]
    env_file: .env
    depends_on: [mysql]

volumes:
  mysql_data:
```

```bash
docker compose up --build
```

### En AWS

MySQL corre como contenedor en la **VM de base de datos** (uno de los 3
contenedores de esa maquina) y el microservicio en la **VM de produccion**, asi
que `MYSQL_HOST` apunta a la **IP privada** de la VM de base de datos.

La imagen se publica en **Docker Hub** para que las 2 VM de produccion gemelas
hagan `pull` de la misma version:

```bash
docker build -t <usuario>/ms-pagos:0.1.0 .
docker push <usuario>/ms-pagos:0.1.0
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

Microservicio funcional: 7 endpoints implementados y probados contra MySQL,
con persistencia real. Imagen publicada en Docker Hub como
[sebpecar75/ms-pagos](https://hub.docker.com/r/sebpecar75/ms-pagos).
Coleccion de Postman disponible en [docs/ms-pagos-postman-collection_1.json](docs/ms-pagos-postman-collection_1.json).

## Coleccion de Postman

Coleccion lista para la demo en [docs/ms-pagos-postman-collection_1.json](docs/ms-pagos-postman-collection_1.json).
Importar en Postman y ajustar la variable `base_url` segun donde este corriendo el microservicio.

