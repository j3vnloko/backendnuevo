package com.vivitasol.projectbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivitasol.projectbackend.entities.Carrito;
import com.vivitasol.projectbackend.entities.CarritoItem;
import com.vivitasol.projectbackend.entities.Producto;
import com.vivitasol.projectbackend.entities.Usuario;
import com.vivitasol.projectbackend.repositories.CarritoItemRepository;
import com.vivitasol.projectbackend.repositories.CarritoRepository;
import com.vivitasol.projectbackend.repositories.ProductoRepository;
import com.vivitasol.projectbackend.repositories.UsuarioRepository;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepo;

    @Autowired
    private CarritoItemRepository itemRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ProductoRepository productoRepo;

    @Override
    public Carrito obtenerCarrito(Long usuarioId) {
        Carrito carrito = carritoRepo.findByUsuarioId(usuarioId);

        if (carrito == null) {
            Usuario usuario = usuarioRepo.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            carrito = new Carrito();
            carrito.setUsuario(usuario);
            carritoRepo.save(carrito);
        }

        return carrito;
    }

    @Override
    public Carrito agregarProducto(Long usuarioId, Long productoId, Integer cantidad) {
        Carrito carrito = obtenerCarrito(usuarioId);

        Producto producto = productoRepo.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar si el producto ya existe en el carrito
        CarritoItem itemExistente = carrito.getItems().stream()
                .filter(i -> i.getProducto().getId().equals(productoId))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            // Si existe, sumar la cantidad
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
            itemRepo.save(itemExistente);
        } else {
            // Si no existe, crear nuevo item
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);
            itemRepo.save(nuevoItem);
            carrito.getItems().add(nuevoItem);
        }

        return obtenerCarrito(usuarioId);
    }

    @Override
    public Carrito actualizarCantidad(Long usuarioId, Long itemId, Integer cantidad) {
        CarritoItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        if (!item.getCarrito().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No autorizado");
        }

        if (cantidad <= 0) {
            itemRepo.delete(item);
        } else {
            item.setCantidad(cantidad);
            itemRepo.save(item);
        }

        return obtenerCarrito(usuarioId);
    }

    @Override
    public Carrito eliminarItem(Long usuarioId, Long itemId) {
        CarritoItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        if (!item.getCarrito().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No autorizado");
        }

        itemRepo.delete(item);
        return obtenerCarrito(usuarioId);
    }

    @Override
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = obtenerCarrito(usuarioId);
        carrito.getItems().clear();
        carritoRepo.save(carrito);
    }
}