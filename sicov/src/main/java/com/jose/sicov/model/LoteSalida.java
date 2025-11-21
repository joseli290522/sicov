package com.jose.sicov.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import com.jose.sicov.dto.LoteSalidaDTO;
import com.jose.sicov.util.IMapper;

@Entity
@Table(name = "lotes_salida")
@Getter @Setter
public class LoteSalida extends Base implements IMapper<LoteSalidaDTO> { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote; // Lote de donde salió la cantidad

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_venta_id", nullable = false)
    private DetalleVenta detalleVenta;

    @Column(name = "cantidad_consumida", nullable = false)
    private Integer cantidadConsumida;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDateTime fechaSalida = LocalDateTime.now();

    @Override
    public LoteSalidaDTO getDto() {
        return LoteSalidaDTO.builder()
            .id(this.id)
            .lote(this.lote.getDto())
            .cantidadConsumida(this.cantidadConsumida)
            .fechaSalida(this.fechaSalida)
            .build();
    }

    @Override
    public void setData(LoteSalidaDTO t) {
        throw new UnsupportedOperationException("Unimplemented method 'setData'");
    }
}