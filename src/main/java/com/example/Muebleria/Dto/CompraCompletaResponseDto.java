package com.example.Muebleria.Dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class CompraCompletaResponseDto {
    private Integer idCompra;
    private String proveedor;
    private LocalDateTime fecha;
    private Double total;
    private List<DetalleCompraDto> detalles;
}
