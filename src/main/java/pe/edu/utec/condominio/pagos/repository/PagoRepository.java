package pe.edu.utec.condominio.pagos.repository;

import pe.edu.utec.condominio.pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByCuotaId(Long cuotaId);

    List<Pago> findByCuota_UnidadId(Long unidadId);
}
