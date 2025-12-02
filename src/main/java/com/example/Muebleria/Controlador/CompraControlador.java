package com.example.Muebleria.Controlador;

import com.example.Muebleria.Dto.CompraCompletaDto;
import com.example.Muebleria.Dto.CompraCompletaResponseDto;
import com.example.Muebleria.Dto.CompraDto;
import com.example.Muebleria.Dto.DetalleCompraRequestDto;
import com.example.Muebleria.Modelo.Cliente;
import com.example.Muebleria.Modelo.Compra;
import com.example.Muebleria.Servicio.ClienteServi;
import com.example.Muebleria.Servicio.CompraServi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/compra")
@CrossOrigin(origins = "*")
public class CompraControlador {
    @Autowired
    private CompraServi servi;

    @GetMapping("/ver")
    public List<CompraDto> ver(){
        return servi.ObtenerTodo();
    }

    @GetMapping("/buscarid/{id}")
    public Optional<CompraDto> Buscar(@PathVariable("id") Integer id){
        return servi.Buscar(id);
    }

    // Endpoint para ver compra con sus detalles
    @GetMapping("/completa/{id}")
    public ResponseEntity<CompraCompletaResponseDto> verCompraCompleta(@PathVariable("id") Integer id){
        Optional<CompraCompletaResponseDto> compra = servi.BuscarCompraCompleta(id);
        return compra.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint unificado para crear compra con detalles
    @PostMapping("/agregar")
    public ResponseEntity<CompraCompletaResponseDto> agregarCompraCompleta(
            @RequestBody CompraCompletaDto compraCompletaDto) {
        try {
            // LOGS PARA DEBUGGING
            System.out.println("=== DATOS RECIBIDOS EN CONTROLADOR ===");
            System.out.println("CompraCompletaDto: " + compraCompletaDto);
            System.out.println("idProveedor: " + compraCompletaDto.getIdProveedor());
            System.out.println("Cantidad de detalles: " + (compraCompletaDto.getDetalles() != null ? compraCompletaDto.getDetalles().size() : "null"));

            if (compraCompletaDto.getDetalles() != null) {
                for (DetalleCompraRequestDto det : compraCompletaDto.getDetalles()) {
                    System.out.println("  - Producto ID: " + det.getIdProducto() +
                            ", Cantidad: " + det.getCantidad() +
                            ", Precio: " + det.getPrecioCompra());
                }
            }

            CompraCompletaResponseDto response = servi.guardarCompraCompleta(compraCompletaDto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }


}