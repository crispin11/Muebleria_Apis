package com.example.Muebleria.Servicio;
import com.example.Muebleria.Dto.*;
import com.example.Muebleria.Modelo.*;
import com.example.Muebleria.Repositorio.CompraRepo;
import com.example.Muebleria.Repositorio.DetalleCompraRepo;
import com.example.Muebleria.Repositorio.ProductoRepo;
import com.example.Muebleria.Repositorio.ProveedorRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompraServi {
    @Autowired
    private CompraRepo repo;
    @Autowired
    private DetalleCompraRepo detalleRepo;
    @Autowired
    private ProductoRepo productoRepo;
    @Autowired
    private ProveedorRepo proveedorRepo;

    public List<CompraDto> ObtenerTodo() {
        return repo.findAll().stream().filter(Compra::isEstado)
                .map(compra -> new CompraDto(
                        compra.getId(),
                        compra.getProveedor().getRazon_social(),
                        compra.getFecha(),
                        compra.getTotal(),
                        compra.isEstado()))
                .collect(Collectors.toList());
    }

    public Optional<CompraDto> Buscar(Integer id){
        return repo.findById(id)
                .map(compra -> new CompraDto(
                        compra.getId(),
                        compra.getProveedor().getRazon_social(),
                        compra.getFecha(),
                        compra.getTotal(),
                        compra.isEstado()));
    }

    // Buscar compra con sus detalles
    public Optional<CompraCompletaResponseDto> BuscarCompraCompleta(Integer id) {
        return repo.findById(id)
                .filter(Compra::isEstado)
                .map(compra -> {
                    CompraCompletaResponseDto response = new CompraCompletaResponseDto();
                    response.setIdCompra(compra.getId());
                    response.setProveedor(compra.getProveedor().getRazon_social());
                    response.setFecha(compra.getFecha());
                    response.setTotal(compra.getTotal());

                    List<DetalleCompraDto> detalles = detalleRepo.findByCompraId(id)
                            .stream()
                            .filter(DetalleCompra::isEstado)
                            .map(det -> new DetalleCompraDto(
                                    det.getId(),
                                    det.getCompra().getId(),
                                    det.getProducto().getNombre(),
                                    det.getCantidad(),
                                    det.getPrecioCompra(),
                                    det.getSubtotal(),
                                    det.isEstado()))
                            .collect(Collectors.toList());

                    response.setDetalles(detalles);
                    return response;
                });
    }

    public void Guardar_o_Modificar(Compra compra){
        compra.setEstado(true);
        repo.save(compra);
    }
    // Método unificado para guardar compra con detalles
    @Transactional
    public CompraCompletaResponseDto guardarCompraCompleta(CompraCompletaDto dto) {
        // Validar proveedor
        Proveedor proveedor = proveedorRepo.findById(dto.getIdProveedor())
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));

        // Crear y guardar la compra
        Compra compra = new Compra();
        compra.setProveedor(proveedor);
        compra.setFecha(LocalDateTime.now());
        compra.setTotal(0.0);
        compra.setEstado(true);
        compra = repo.save(compra);

        // Procesar cada detalle
        List<DetalleCompraDto> detallesResponse = new ArrayList<>();
        double totalCompra = 0.0;

        for (DetalleCompraRequestDto detalleDto : dto.getDetalles()) {
            // Validar producto
            Producto producto = productoRepo.findById(detalleDto.getIdProducto())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Producto no encontrado: " + detalleDto.getIdProducto()));

            // Crear detalle
            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDto.getCantidad());
            detalle.setPrecioCompra(detalleDto.getPrecioCompra());
            detalle.setSubtotal(detalleDto.getCantidad() * detalleDto.getPrecioCompra());
            detalle.setEstado(true);
            detalle = detalleRepo.save(detalle);

            // Actualizar inventario del producto
            producto.setCantidad(producto.getCantidad() + detalleDto.getCantidad());
            productoRepo.save(producto);

            // Acumular total
            totalCompra += detalle.getSubtotal();

            // Agregar a respuesta
            detallesResponse.add(new DetalleCompraDto(
                    detalle.getId(),
                    detalle.getCompra().getId(),
                    detalle.getProducto().getNombre(),
                    detalle.getCantidad(),
                    detalle.getPrecioCompra(),
                    detalle.getSubtotal(),
                    detalle.isEstado()));
        }

        // Actualizar total de la compra
        compra.setTotal(totalCompra);
        compra = repo.save(compra);

        // Preparar respuesta
        CompraCompletaResponseDto response = new CompraCompletaResponseDto();
        response.setIdCompra(compra.getId());
        response.setProveedor(compra.getProveedor().getRazon_social());
        response.setFecha(compra.getFecha());
        response.setTotal(compra.getTotal());
        response.setDetalles(detallesResponse);

        return response;
    }
}