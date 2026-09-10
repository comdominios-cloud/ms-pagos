package pe.edu.utec.condominio.pagos.repository;

import pe.edu.utec.condominio.pagos.model.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuotaRepository extends JpaRepository<Cuota, Long> {

    List<Cuota> findByUnidadId(Long unidadId);

    List<Cuota> findByEstado(Cuota.Estado estado);

    List<Cuota> findByUnidadIdAndEstado(Long unidadId, Cuota.Estado estado);

    List<Cuota> findByUnidadIdAndEstadoIn(Long unidadId, List<Cuota.Estado> estados);
}
