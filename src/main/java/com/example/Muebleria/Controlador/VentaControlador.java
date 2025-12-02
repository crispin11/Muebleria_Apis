package com.example.Muebleria.Controlador;
import com.example.Muebleria.Dto.VentaDetalleDto;
import com.example.Muebleria.Dto.VentaDto;
import com.example.Muebleria.Modelo.DetalleVenta;
import com.example.Muebleria.Modelo.Kardex;
import com.example.Muebleria.Modelo.Venta;

import com.example.Muebleria.Repositorio.KardexRepo;
import com.example.Muebleria.Servicio.ProductoServi;
import com.example.Muebleria.Servicio.VentaServi;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/venta")
@CrossOrigin(origins = "*")
public class VentaControlador {
    @Autowired
    private VentaServi ventaServi;

    @GetMapping("/ver")
    public List<VentaDto> ver() {
        return ventaServi.ObtenerTodo();
    }

    @GetMapping("/buscarid/{id}")
    public Optional<VentaDto> Buscar(@PathVariable("id") Integer id) {
        return ventaServi.Buscar(id);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Integer> Agregar(@RequestBody VentaDetalleDto ventaDetalleDto) {
        Venta venta = ventaDetalleDto.getVenta();
        List<DetalleVenta> detalles = ventaDetalleDto.getDetalles();

        Venta ventaGuardada = ventaServi.GuardarConDetalle(venta, detalles);

        return ResponseEntity.ok(ventaGuardada.getId());
    }
}

