package com.vivitasol.projectbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vivitasol.projectbackend.entities.Carrito;
import com.vivitasol.projectbackend.entities.CarritoItem;
import com.vivitasol.projectbackend.entities.Producto;
import com.vivitasol.projectbackend.entities.Usuario;
import com.vivitasol.projectbackend.repositories.CarritoItemRepository;
import com.vivitasol.projectbackend.repositories.CarritoRepository;
import com.vivitasol.projectbackend.repositories.ProductoRepository;
import com.vivitasol.projectbackend.repositories.UsuarioRepository;

@Service
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

        CarritoItem item = new CarritoItem();
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setCantidad(cantidad);

        itemRepo.save(item);

        return obtenerCarrito(usuarioId);
    }

    @Override
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = obtenerCarrito(usuarioId);
        carrito.getItems().clear();
        carritoRepo.save(carrito);
    }
}
