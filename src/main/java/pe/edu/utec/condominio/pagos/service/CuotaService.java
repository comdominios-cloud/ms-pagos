package pe.edu.utec.condominio.pagos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utec.condominio.pagos.dto.CuotaRequest;
import pe.edu.utec.condominio.pagos.dto.CuotaResponse;
import pe.edu.utec.condominio.pagos.dto.EstadoCuentaResponse;
import pe.edu.utec.condominio.pagos.exception.ResourceNotFoundException;
import pe.edu.utec.condominio.pagos.model.Cuota;
import pe.edu.utec.condominio.pagos.model.Pago;
import pe.edu.utec.condominio.pagos.repository.CuotaRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CuotaService {

    private final CuotaRepository cuotaRepository;

    @Transactional
    public CuotaResponse crear(CuotaRequest request) {
        Cuota cuota = Cuota.builder()
                .unidadId(request.unidadId())
                .residenteId(request.residenteId())
                .periodo(request.periodo())
                .concepto(request.concepto())
                .monto(request.monto())
                .fechaEmision(request.fechaEmision())
                .fechaVencim(request.fechaVencim())
                .estado(Cuota.Estado.PENDIENTE)
                .build();
        Cuota guardada = cuotaRepository.save(cuota);
        return CuotaResponse.fromEntitySinPagos(guardada);
    }

    @Transactional(readOnly = true)
    public List<CuotaResponse> listar(Long unidadId, Cuota.Estado estado) {
        List<Cuota> cuotas;
        if (unidadId != null && estado != null) {
            cuotas = cuotaRepository.findByUnidadIdAndEstado(unidadId, estado);
        } else if (unidadId != null) {
            cuotas = cuotaRepository.findByUnidadId(unidadId);
        } else if (estado != null) {
            cuotas = cuotaRepository.findByEstado(estado);
        } else {
            cuotas = cuotaRepository.findAll();
        }
        return cuotas.stream().map(CuotaResponse::fromEntitySinPagos).toList();
    }

    @Transactional(readOnly = true)
    public CuotaResponse obtenerDetalle(Long cuotaId) {
        Cuota cuota = cuotaRepository.findById(cuotaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuota no encontrada: " + cuotaId));
        return CuotaResponse.fromEntity(cuota);
    }

    @Transactional(readOnly = true)
    public EstadoCuentaResponse estadoCuenta(Long unidadId) {
        List<Cuota> pendientes = cuotaRepository.findByUnidadIdAndEstadoIn(
                unidadId, List.of(Cuota.Estado.PENDIENTE, Cuota.Estado.PARCIAL, Cuota.Estado.VENCIDA));

        BigDecimal deudaTotal = pendientes.stream()
                .map(this::saldoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CuotaResponse> cuotasResp = pendientes.stream().map(CuotaResponse::fromEntitySinPagos).toList();
        return new EstadoCuentaResponse(unidadId, deudaTotal, cuotasResp);
    }

    private BigDecimal saldoPendiente(Cuota cuota) {
        BigDecimal pagado = cuota.getPagos().stream()
                .map(Pago::getMontoPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return cuota.getMonto().subtract(pagado);
    }
}
