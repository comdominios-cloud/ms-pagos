# Integrante responsable

| | |
|---|---|
| **Repositorio** | `ms-pagos` |
| **Integrante** | [@sebastianperez72](https://github.com/sebastianperez72) |
| **Rol** | API con base de datos (Java) |

## Alcance

ms-pagos: API de cuotas de mantenimiento y pagos en Java/Spring Boot sobre PostgreSQL.

## Como trabajamos

Cada repositorio del proyecto pertenece a un integrante y se desarrolla de forma
**independiente**: las APIs con base de datos no se llaman entre si. La unica
integracion entre microservicios vive en `ms-ficha-residente`, y la del lado del
usuario en `web-condominio`.

Los cambios a este repositorio los define su responsable. Si otro integrante
necesita algo de esta API, se pide via issue en vez de tocar el codigo.

## Equipo

| Repositorio | Integrante | Rol |
|---|---|---|
| [ms-residentes](https://github.com/comdominios-cloud/ms-residentes) | @Osomar1705 | API con BD - Python |
| [ms-pagos](https://github.com/comdominios-cloud/ms-pagos) | @sebastianperez72 | API con BD - Java |
| [ms-incidencias](https://github.com/comdominios-cloud/ms-incidencias) | @fabianbot1331 | API con BD - lenguaje por definir |
| [ms-ficha-residente](https://github.com/comdominios-cloud/ms-ficha-residente) | @Brisseth-raton | Backend / Infraestructura |
| [web-condominio](https://github.com/comdominios-cloud/web-condominio) | @alxgr-08 | Frontend |
| [ms-analitico](https://github.com/comdominios-cloud/ms-analitico) | @carloscondor1610 | Data Science |
| [ingesta-datos](https://github.com/comdominios-cloud/ingesta-datos) | @carloscondor1610 | Data Science |

> CS2032 Cloud Computing - UTEC | Sistema de Administracion de Condominios
