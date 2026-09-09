package pe.edu.utec.condominio.pagos.dto;

import pe.edu.utec.condominio.pagos.model.Pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        Long cuotaId,
        BigDecimal montoPagado,
        LocalDateTime fechaPago,
        String medioPago,
        String referencia,
        LocalDateTime creadoEn
) {
    public static PagoResponse fromEntity(Pago p) {
        return new PagoResponse(
                p.getId(),
                p.getCuota() != null ? p.getCuota().getId() : null,
                p.getMontoPagado(),
                p.getFechaPago(),
                p.getMedioPago().name(),
                p.getReferencia(),
                p.getCreadoEn()
        );
    }
}
