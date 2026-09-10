package pe.edu.utec.condominio.pagos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.utec.condominio.pagos.dto.EstadoCuentaResponse;
import pe.edu.utec.condominio.pagos.service.CuotaService;

@RestController
@RequestMapping("/unidades")
@RequiredArgsConstructor
public class UnidadController {

    private final CuotaService cuotaService;

    @GetMapping("/{unidad_id}/estado-cuenta")
    public EstadoCuentaResponse estadoCuenta(@PathVariable("unidad_id") Long unidadId) {
        return cuotaService.estadoCuenta(unidadId);
    }
}
