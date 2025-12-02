package com.example.Muebleria.Dto;

import lombok.Data;

import java.util.List;
@Data
public class CompraCompletaDto {
    private Integer idProveedor;
    private List<DetalleCompraRequestDto> detalles;
}
