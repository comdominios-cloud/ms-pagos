# Integrante responsable

| | |
|---|---|
| **Repositorio** | `ms-pagos` |
| **Integrante** | [@sebastianperez72](https://github.com/sebastianperez72) |
| **Rol** | API con base de datos (Java / MySQL) |
| **Puerto** | 9002 publicado, 8080 interno |

## Alcance

`ms-pagos`: cuotas de mantenimiento y pagos, en Java/Spring Boot sobre MySQL.

> ### Por que importa para el avance
>
> El punto de Back-End exige **dos microservicios funcionando**, probados con
> Postman. `ms-residentes` es uno; **este es el otro**. Sin el no se completa
> ese punto.

## Avance del 50% — entrega del 6 al 12 de septiembre

- [ ] Contenedor de **MySQL 3306** levantado en la VM de base de datos, con volumen
- [ ] Tablas `cuotas` y `pagos` creadas ([docs/schema.sql](docs/schema.sql))
- [ ] Algo de data cargada
- [ ] Al menos 4 endpoints respondiendo contra la BD
- [ ] Microservicio publicado en el puerto **9002** de la VM de produccion
- [ ] Persistencia real: reiniciar el contenedor y que los datos sigan
- [ ] **Coleccion de Postman** lista para la demo
- [ ] Imagen en **Docker Hub**

---

## Como trabajamos

Cada repositorio pertenece a un integrante y se desarrolla de forma
**independiente**: las APIs con base de datos no se llaman entre si. La unica
integracion entre microservicios vive en `ms-ficha-residente`, y la del lado del
usuario en `web-condominio`.

Los cambios a este repositorio los define su responsable. Si otro integrante
necesita algo de esta API, se pide via issue en vez de tocar el codigo.

## Equipo

| Repositorio | Integrante | Rol | Puerto |
|---|---|---|---|
| [ms-residentes](https://github.com/comdominios-cloud/ms-residentes) | @Osomar1705 | API con BD - Python / PostgreSQL | 9001 |
| [ms-pagos](https://github.com/comdominios-cloud/ms-pagos) | @sebastianperez72 | API con BD - Java / MySQL | 9002 |
| [ms-incidencias](https://github.com/comdominios-cloud/ms-incidencias) | @fabianbot1331 | API con BD - lenguaje por definir / MongoDB | 9003 |
| [ms-ficha-residente](https://github.com/comdominios-cloud/ms-ficha-residente) | @Brisseth-raton | Backend / Infraestructura | 9004 |
| [web-condominio](https://github.com/comdominios-cloud/web-condominio) | @alxgr-08 | Frontend / Amplify | 5173 (dev) |
| [ms-analitico](https://github.com/comdominios-cloud/ms-analitico) | @carloscondor1610 | Data Science | 9005 |
| [ingesta-datos](https://github.com/comdominios-cloud/ingesta-datos) | @carloscondor1610 | Data Science | — |

> CS2032 Cloud Computing - UTEC | Sistema de Administracion de Condominios
