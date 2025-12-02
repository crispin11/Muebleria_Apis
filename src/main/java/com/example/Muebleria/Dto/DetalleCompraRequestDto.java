package com.example.Muebleria.Dto;

import lombok.Data;

@Data
public class DetalleCompraRequestDto {
    private Integer idProducto;
    private Integer cantidad;
    private Double precioCompra;
}
