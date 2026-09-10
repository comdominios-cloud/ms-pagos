package pe.edu.utec.condominio.pagos.dto;

import java.math.BigDecimal;
import java.util.List;

public record EstadoCuentaResponse(
        Long unidadId,
        BigDecimal deudaTotal,
        List<CuotaResponse> cuotasPendientes
) {
}
