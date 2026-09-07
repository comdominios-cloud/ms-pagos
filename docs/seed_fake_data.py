"""Carga masiva de datos fake para ms-pagos.

PLACEHOLDER. Objetivo del curso: insertar al menos 20,000 registros
distribuidos entre cuotas y pagos.

Uso previsto:
    python docs/seed_fake_data.py --total 20000

Dependencias sugeridas: faker, pymysql (o sqlalchemy).
"""

TOTAL_REGISTROS = 20_000


def main() -> None:
    # TODO: 1. Leer configuracion de conexion desde variables de entorno.
    # TODO: 2. Definir el rango de unidad_id a usar (identificadores logicos).
    # TODO: 3. Generar cuotas por unidad y periodo (varios meses).
    # TODO: 4. Generar pagos asociados a las cuotas (algunos parciales, otros completos).
    # TODO: 5. Insertar por lotes (executemany / bulk_insert) hasta TOTAL_REGISTROS.
    raise NotImplementedError("Pendiente de implementar en la fase de carga de datos.")


if __name__ == "__main__":
    main()
