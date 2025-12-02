package com.example.Muebleria.Servicio;
import com.example.Muebleria.Dto.VentaDto;
import com.example.Muebleria.Modelo.DetalleVenta;
import com.example.Muebleria.Modelo.Producto;
import com.example.Muebleria.Modelo.Venta;
import com.example.Muebleria.Repositorio.DetalleVentaRepo;
import com.example.Muebleria.Repositorio.ProductoRepo;
import com.example.Muebleria.Repositorio.VentaRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class VentaServi {
    @Autowired
    private VentaRepo ventaRepo;
    @Autowired
    private DetalleVentaRepo detalleVentaRepo;
    @Autowired
    private ProductoRepo productoRepo;

    public List<VentaDto> ObtenerTodo() {
        return ventaRepo.findAll().stream().filter(Venta::isEstado)
                .map(venta -> new VentaDto(
                        venta.getId(),
                        venta.getCliente().getDni(),
                        venta.getUsuario().getCorreo(),
                        venta.getMetodoPago().getNombre(),
                        venta.getFecha(),
                        venta.getTotal(),
                        venta.isEstado()))
                .collect(Collectors.toList());
    }

    public Optional<VentaDto> Buscar(Integer id) {
        return ventaRepo.findById(id).map(venta -> new VentaDto(
                venta.getId(),
                venta.getCliente().getDni(),
                venta.getUsuario().getCorreo(),
                venta.getMetodoPago().getNombre(),
                venta.getFecha(),
                venta.getTotal(),
                venta.isEstado()));
    }

    @Transactional
    public Venta GuardarConDetalle(Venta venta, List<DetalleVenta> detalles) {
        venta.setEstado(true);
        Venta ventaGuardada = ventaRepo.save(venta);

        double totalVenta = 0;
        for (DetalleVenta detalle : detalles) {
            Producto producto = productoRepo.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            if (producto.getCantidad() < detalle.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto.");
            }

            detalle.setVenta(ventaGuardada);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(detalle.getCantidad() * producto.getPrecio());
            producto.setCantidad(producto.getCantidad() - detalle.getCantidad());
            productoRepo.save(producto);

            detalleVentaRepo.save(detalle);
            totalVenta += detalle.getSubtotal();
        }

        ventaGuardada.setTotal(totalVenta);
        return ventaRepo.save(ventaGuardada);
    }
}
