# Diagrama Entidad-Relacion - ms-pagos

> PLACEHOLDER. Reemplazar este archivo (o agregar `der.png` / `der.drawio`)
> con el diagrama ER definitivo antes de la primera entrega.

## Entidades previstas

- **cuotas**: cuotas de mantenimiento emitidas para una unidad en un periodo.
- **pagos**: abonos realizados contra una cuota (puede haber pagos parciales).

## Relaciones

```
cuotas (1) ──< (N) pagos
```

- Una cuota puede recibir varios pagos (`pagos.cuota_id` -> `cuotas.id`).
- La relacion obligatoria del curso (minimo 2 tablas relacionadas) es
  **cuotas <- pagos**.

## Nota sobre `unidad_id` / `residente_id`

Son **identificadores logicos**: guardan el id de la unidad y del residente tal
como existen en ms-residentes, pero **no** hay clave foranea ni llamada HTTP
hacia ese microservicio. Cada API con base de datos es autonoma; el cruce de
informacion entre microservicios lo hace **ms-ficha-residente**.

## Imagen

<!-- ![Diagrama ER](der.png) -->
