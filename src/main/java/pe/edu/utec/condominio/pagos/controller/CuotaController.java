package pe.edu.utec.condominio.pagos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.utec.condominio.pagos.dto.CuotaRequest;
import pe.edu.utec.condominio.pagos.dto.CuotaResponse;
import pe.edu.utec.condominio.pagos.model.Cuota;
import pe.edu.utec.condominio.pagos.service.CuotaService;

import java.util.List;

@RestController
@RequestMapping("/cuotas")
@RequiredArgsConstructor
public class CuotaController {

    private final CuotaService cuotaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CuotaResponse crear(@Valid @RequestBody CuotaRequest request) {
        return cuotaService.crear(request);
    }

    @GetMapping
    public List<CuotaResponse> listar(
            @RequestParam(name = "unidad_id", required = false) Long unidadId,
            @RequestParam(required = false) Cuota.Estado estado) {
        return cuotaService.listar(unidadId, estado);
    }

    @GetMapping("/{cuota_id}")
    public CuotaResponse detalle(@PathVariable("cuota_id") Long cuotaId) {
        return cuotaService.obtenerDetalle(cuotaId);
    }
}
