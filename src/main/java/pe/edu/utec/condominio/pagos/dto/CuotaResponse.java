package pe.edu.utec.condominio.pagos.dto;

import pe.edu.utec.condominio.pagos.model.Cuota;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CuotaResponse(
        Long id,
        Long unidadId,
        Long residenteId,
        String periodo,
        String concepto,
        BigDecimal monto,
        LocalDate fechaEmision,
        LocalDate fechaVencim,
        String estado,
        LocalDateTime creadoEn,
        List<PagoResponse> pagos
) {
    public static CuotaResponse fromEntity(Cuota c) {
        return new CuotaResponse(
                c.getId(),
                c.getUnidadId(),
                c.getResidenteId(),
                c.getPeriodo(),
                c.getConcepto(),
                c.getMonto(),
                c.getFechaEmision(),
                c.getFechaVencim(),
                c.getEstado().name(),
                c.getCreadoEn(),
                c.getPagos() == null ? List.of() : c.getPagos().stream().map(PagoResponse::fromEntity).toList()
        );
    }

    public static CuotaResponse fromEntitySinPagos(Cuota c) {
        return new CuotaResponse(
                c.getId(), c.getUnidadId(), c.getResidenteId(), c.getPeriodo(), c.getConcepto(),
                c.getMonto(), c.getFechaEmision(), c.getFechaVencim(), c.getEstado().name(),
                c.getCreadoEn(), null
        );
    }
}
