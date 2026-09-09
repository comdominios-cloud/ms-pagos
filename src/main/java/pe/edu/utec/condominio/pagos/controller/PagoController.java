package pe.edu.utec.condominio.pagos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.utec.condominio.pagos.dto.PagoRequest;
import pe.edu.utec.condominio.pagos.dto.PagoResponse;
import pe.edu.utec.condominio.pagos.service.PagoService;

import java.util.List;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PagoResponse registrar(@Valid @RequestBody PagoRequest request) {
        return pagoService.registrar(request);
    }

    @GetMapping
    public List<PagoResponse> listar(@RequestParam(name = "unidad_id", required = false) Long unidadId) {
        return pagoService.listar(unidadId);
    }
}
