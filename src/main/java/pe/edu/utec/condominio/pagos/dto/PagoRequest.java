package pe.edu.utec.condominio.pagos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.edu.utec.condominio.pagos.model.Pago;

import java.math.BigDecimal;

public record PagoRequest(
        @NotNull Long cuotaId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal montoPagado,
        Pago.MedioPago medioPago,
        @Size(max = 80) String referencia
) {
}
