package pe.edu.utec.condominio.pagos.repository;

import pe.edu.utec.condominio.pagos.model.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CuotaRepository extends JpaRepository<Cuota, Long> {

    List<Cuota> findByUnidadId(Long unidadId);

    List<Cuota> findByEstado(Cuota.Estado estado);

    List<Cuota> findByUnidadIdAndEstado(Long unidadId, Cuota.Estado estado);

    @Query("SELECT COALESCE(SUM(c.monto), 0) FROM Cuota c " +
           "WHERE c.unidadId = :unidadId AND c.estado IN ('PENDIENTE','PARCIAL','VENCIDA')")
    BigDecimal sumMontoPendienteByUnidadId(@Param("unidadId") Long unidadId);

    List<Cuota> findByUnidadIdAndEstadoIn(Long unidadId, List<Cuota.Estado> estados);
}
