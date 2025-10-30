package com.jose.sicov.service.interfaces;

import com.jose.sicov.dto.DetalleCompraDTO;
import com.jose.sicov.model.Lote;

public interface ILoteService {
    /**
     * Módulo COMPRAS: Registra la entrada de stock (Lógica UPSERT: Update or Insert).
     * @param detalleDTO Datos de la línea de compra, incluyendo numeroLote y fechaVencimiento.
     * @param almacenId El ID del almacén de destino.
     * @return El Lote creado o actualizado.
     */
    Lote registrarEntrada(DetalleCompraDTO detalleDTO, Long almacenId);
}
