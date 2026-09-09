package pe.edu.utec.condominio.pagos.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuotaRequest(
        @NotNull Long unidadId,
        Long residenteId,
        @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}", message = "periodo debe tener formato YYYY-MM") String periodo,
        @NotBlank @Size(max = 160) String concepto,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal monto,
        @NotNull LocalDate fechaEmision,
        @NotNull LocalDate fechaVencim
) {
}
