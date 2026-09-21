"""Carga masiva de datos ficticios para ms-pagos (MySQL).

El curso pide minimo 20,000 registros en al menos una tabla de cada base.
Aca la tabla masiva es `pagos`, y se generan las `cuotas` que los sostienen.

Uso:
    python docs/seed_fake_data.py                  # 20,000 pagos
    python docs/seed_fake_data.py --total 50000
    python docs/seed_fake_data.py --limpiar

Lee la conexion de las variables de entorno (o del .env):
    MYSQL_HOST, MYSQL_PORT, MYSQL_DATABASE, MYSQL_USER, MYSQL_PASSWORD

Dependencias:
    pip install faker pymysql python-dotenv
"""

import argparse
import os
import random
import sys
from datetime import date, datetime, timedelta

try:
    import pymysql
    from dotenv import load_dotenv
    from faker import Faker
except ImportError as e:
    sys.exit(f"Falta una dependencia: {e}\n  pip install faker pymysql python-dotenv")

TOTAL_POR_DEFECTO = 20_000
LOTE = 2_000

# Cuantas unidades del condominio se facturan y cuantos periodos se emiten.
UNIDADES = 8_000
MESES_DE_HISTORIA = 24

CONCEPTOS = (
    "Mantenimiento mensual",
    "Fondo de reserva",
    "Cuota extraordinaria - ascensor",
    "Cuota extraordinaria - pintura",
    "Consumo de agua comun",
)
MEDIOS = ("EFECTIVO", "TRANSFERENCIA", "TARJETA", "YAPE", "PLIN")

fake = Faker("es_ES")
Faker.seed(2026)
random.seed(2026)


def conexion():
    load_dotenv()
    faltantes = [
        v for v in ("MYSQL_HOST", "MYSQL_DATABASE", "MYSQL_USER", "MYSQL_PASSWORD")
        if not os.getenv(v)
    ]
    if faltantes:
        sys.exit(f"Faltan variables de entorno: {', '.join(faltantes)}")

    return pymysql.connect(
        host=os.getenv("MYSQL_HOST"),
        port=int(os.getenv("MYSQL_PORT", "3306")),
        database=os.getenv("MYSQL_DATABASE"),
        user=os.getenv("MYSQL_USER"),
        password=os.getenv("MYSQL_PASSWORD"),
        charset="utf8mb4",
        autocommit=False,
    )


def limpiar(conn) -> None:
    with conn.cursor() as cur:
        # Se desactiva la verificacion de claves foraneas para poder truncar:
        # TRUNCATE es mucho mas rapido que DELETE y reinicia el AUTO_INCREMENT.
        cur.execute("SET FOREIGN_KEY_CHECKS = 0")
        cur.execute("TRUNCATE TABLE pagos")
        cur.execute("TRUNCATE TABLE cuotas")
        cur.execute("SET FOREIGN_KEY_CHECKS = 1")
    conn.commit()
    print("  Tablas vaciadas")


def periodos(cantidad: int) -> list[str]:
    hoy = date.today().replace(day=1)
    salida = []
    for i in range(cantidad):
        mes = hoy.month - i
        anio = hoy.year
        while mes <= 0:
            mes += 12
            anio -= 1
        salida.append(f"{anio:04d}-{mes:02d}")
    return sorted(salida)


def insertar_cuotas(conn, cantidad: int) -> list[tuple[int, float]]:
    meses = periodos(MESES_DE_HISTORIA)
    combinaciones: set[tuple[int, str, str]] = set()
    insertadas = 0

    sql = (
        "INSERT INTO cuotas "
        "(unidad_id, residente_id, periodo, concepto, monto, fecha_emision, "
        " fecha_vencim, estado) VALUES (%s,%s,%s,%s,%s,%s,%s,%s)"
    )

    with conn.cursor() as cur:
        while insertadas < cantidad:
            n = min(LOTE, cantidad - insertadas)
            lote = []

            while len(lote) < n:
                unidad = random.randint(1, UNIDADES)
                periodo = random.choice(meses)
                concepto = random.choice(CONCEPTOS)

                if (unidad, periodo, concepto) in combinaciones:
                    continue
                combinaciones.add((unidad, periodo, concepto))

                anio, mes = int(periodo[:4]), int(periodo[5:])
                emision = date(anio, mes, 1)
                lote.append(
                    (
                        unidad,
                        random.randint(1, 20_000) if random.random() > 0.1 else None,
                        periodo,
                        concepto,
                        round(random.uniform(80, 650), 2),
                        emision,
                        emision + timedelta(days=15),
                        random.choices(
                            ("PENDIENTE", "PARCIAL", "PAGADA", "VENCIDA", "ANULADA"),
                            weights=(20, 10, 55, 13, 2),
                        )[0],
                    )
                )

            cur.executemany(sql, lote)
            conn.commit()
            insertadas += n
            print(f"  cuotas: {insertadas:>7,} / {cantidad:,}", end="\r", flush=True)

        cur.execute("SELECT id, monto FROM cuotas WHERE estado <> 'ANULADA'")
        cuotas = [(r[0], float(r[1])) for r in cur.fetchall()]

    print(f"  cuotas: {insertadas:>7,} insertadas          ")
    return cuotas


def insertar_pagos(conn, cuotas: list[tuple[int, float]], total: int) -> None:
    sql = (
        "INSERT INTO pagos (cuota_id, monto_pagado, fecha_pago, medio_pago, referencia) "
        "VALUES (%s,%s,%s,%s,%s)"
    )
    hoy = datetime.now()
    insertados = 0

    with conn.cursor() as cur:
        while insertados < total:
            n = min(LOTE, total - insertados)
            lote = []

            for _ in range(n):
                cuota_id, monto = random.choice(cuotas)
                # Algunos pagos son parciales: el estado de cuenta tiene que
                # reflejar saldos, no solo pagado / no pagado.
                parcial = random.random() < 0.25
                pagado = round(monto * random.uniform(0.2, 0.7), 2) if parcial else monto

                lote.append(
                    (
                        cuota_id,
                        pagado,
                        hoy - timedelta(minutes=random.randint(0, 1_051_200)),
                        random.choices(MEDIOS, weights=(10, 45, 20, 15, 10))[0],
                        f"OP-{random.randint(100000, 999999)}",
                    )
                )

            cur.executemany(sql, lote)
            conn.commit()
            insertados += n
            print(f"  pagos:  {insertados:>7,} / {total:,}", end="\r", flush=True)

    print(f"  pagos:  {insertados:>7,} insertados          ")


def main() -> None:
    parser = argparse.ArgumentParser(description="Carga masiva para ms-pagos")
    parser.add_argument("--total", type=int, default=TOTAL_POR_DEFECTO,
                        help=f"Pagos a generar (por defecto {TOTAL_POR_DEFECTO:,})")
    parser.add_argument("--limpiar", action="store_true")
    args = parser.parse_args()

    # Mas cuotas que pagos: algunas quedan sin pagar, que es lo realista y lo
    # que hace que la consulta de morosidad tenga sentido.
    n_cuotas = int(args.total * 1.4)

    print(f"Generando {n_cuotas:,} cuotas y {args.total:,} pagos")

    conn = conexion()
    try:
        if args.limpiar:
            limpiar(conn)

        with conn.cursor() as cur:
            cur.execute("SELECT COUNT(*) FROM pagos")
            existentes = cur.fetchone()[0]

        if existentes and not args.limpiar:
            print(f"  Aviso: ya hay {existentes:,} pagos. Se agregan encima.")

        cuotas = insertar_cuotas(conn, n_cuotas)
        insertar_pagos(conn, cuotas, args.total)

        with conn.cursor() as cur:
            cur.execute("SELECT COUNT(*) FROM cuotas")
            c = cur.fetchone()[0]
            cur.execute("SELECT COUNT(*) FROM pagos")
            p = cur.fetchone()[0]
            print(f"\nTotales: cuotas {c:,} | pagos {p:,}")
    finally:
        conn.close()


if __name__ == "__main__":
    main()
