package pe.edu.utec.condominio.pagos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utec.condominio.pagos.dto.PagoRequest;
import pe.edu.utec.condominio.pagos.dto.PagoResponse;
import pe.edu.utec.condominio.pagos.exception.ResourceNotFoundException;
import pe.edu.utec.condominio.pagos.model.Cuota;
import pe.edu.utec.condominio.pagos.model.Pago;
import pe.edu.utec.condominio.pagos.repository.CuotaRepository;
import pe.edu.utec.condominio.pagos.repository.PagoRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final CuotaRepository cuotaRepository;

    @Transactional
    public PagoResponse registrar(PagoRequest request) {
        Cuota cuota = cuotaRepository.findById(request.cuotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuota no encontrada: " + request.cuotaId()));

        if (cuota.getEstado() == Cuota.Estado.ANULADA) {
            throw new IllegalArgumentException("No se puede registrar un pago sobre una cuota ANULADA");
        }

        Pago pago = Pago.builder()
                .cuota(cuota)
                .montoPagado(request.montoPagado())
                .medioPago(request.medioPago() != null ? request.medioPago() : Pago.MedioPago.TRANSFERENCIA)
                .referencia(request.referencia())
                .build();
        Pago guardado = pagoRepository.save(pago);

        actualizarEstadoCuota(cuota);

        return PagoResponse.fromEntity(guardado);
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> listar(Long unidadId) {
        List<Pago> pagos = unidadId != null
                ? pagoRepository.findByCuota_UnidadId(unidadId)
                : pagoRepository.findAll();
        return pagos.stream().map(PagoResponse::fromEntity).toList();
    }

    private void actualizarEstadoCuota(Cuota cuota) {
        BigDecimal totalPagado = cuota.getPagos().stream()
                .map(Pago::getMontoPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPagado.compareTo(cuota.getMonto()) >= 0) {
            cuota.setEstado(Cuota.Estado.PAGADA);
        } else if (totalPagado.compareTo(BigDecimal.ZERO) > 0) {
            cuota.setEstado(Cuota.Estado.PARCIAL);
        }
        cuotaRepository.save(cuota);
    }
}
