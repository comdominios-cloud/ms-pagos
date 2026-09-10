package pe.edu.utec.condominio.pagos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cuotas", uniqueConstraints = {
        @UniqueConstraint(name = "uq_cuotas_unidad_periodo", columnNames = {"unidad_id", "periodo", "concepto"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuota {

    public enum Estado {
        PENDIENTE, PARCIAL, PAGADA, VENCIDA, ANULADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unidad_id", nullable = false)
    private Long unidadId;

    @Column(name = "residente_id")
    private Long residenteId;

    @Column(name = "periodo", nullable = false, columnDefinition = "CHAR(7)")
    private String periodo; // formato 'YYYY-MM'

    @Column(name = "concepto", nullable = false, length = 160)
    private String concepto;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencim", nullable = false)
    private LocalDate fechaVencim;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private Estado estado = Estado.PENDIENTE;

    @Column(name = "creado_en", nullable = false, updatable = false, insertable = false)
    private LocalDateTime creadoEn;

    @OneToMany(mappedBy = "cuota", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<Pago> pagos = new ArrayList<>();
}
