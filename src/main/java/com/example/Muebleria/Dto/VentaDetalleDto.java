package com.example.Muebleria.Dto;

import com.example.Muebleria.Modelo.DetalleVenta;
import com.example.Muebleria.Modelo.Venta;
import lombok.Data;

import java.util.List;

@Data
public class VentaDetalleDto {
    private Venta venta;
    private List<DetalleVenta> detalles;
}
