package com.vivitasol.projectbackend.services;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.vivitasol.projectbackend.entities.Carrito;
import com.vivitasol.projectbackend.entities.CarritoItem;
import com.vivitasol.projectbackend.entities.Orden;
import com.vivitasol.projectbackend.entities.OrdenItem;
import com.vivitasol.projectbackend.entities.Usuario;
import com.vivitasol.projectbackend.repositories.CarritoRepository;
import com.vivitasol.projectbackend.repositories.OrdenItemRepository;
import com.vivitasol.projectbackend.repositories.OrdenRepository;
import com.vivitasol.projectbackend.repositories.UsuarioRepository;

@Service
public class OrdenServiceImpl implements OrdenService {

    @Autowired
    private OrdenRepository ordenRepo;

    @Autowired
    private OrdenItemRepository ordenItemRepo;

    @Autowired
    private CarritoRepository carritoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Override
    public Orden crearOrden(Long usuarioId) {

        Carrito carrito = carritoRepo.findByUsuarioId(usuarioId);

        if (carrito == null || carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setFecha(LocalDateTime.now());

        List<OrdenItem> items = new ArrayList<>();
        int total = 0;

        for (CarritoItem item : carrito.getItems()) {
            OrdenItem oItem = new OrdenItem();
            oItem.setOrden(orden);
            oItem.setProducto(item.getProducto());
            oItem.setCantidad(item.getCantidad());
            oItem.setPrecioUnitario(item.getProducto().getPrecio().intValue());

            total += item.getCantidad() * item.getProducto().getPrecio().intValue();

            items.add(oItem);
        }

        orden.setItems(items);
        orden.setTotal(total);

        ordenRepo.save(orden);
        ordenItemRepo.saveAll(items);

        carrito.getItems().clear();
        carritoRepo.save(carrito);

        return orden;
    }

    @Override
    public byte[] generarBoletaPDF(Long ordenId) {
        Orden orden = ordenRepo.findById(ordenId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph(
                    "BOLETA DE COMPRA",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)
            ));
            document.add(new Paragraph(""));

            document.add(new Paragraph("Cliente: " + orden.getUsuario().getNombre()));
            document.add(new Paragraph("Fecha: " + orden.getFecha().toString()));
            document.add(new Paragraph("------------------------------------------------------------"));
            document.add(new Paragraph(""));

            for (OrdenItem item : orden.getItems()) {
                document.add(new Paragraph(
                        item.getProducto().getNombre()
                                + " | Cant: " + item.getCantidad()
                                + " | Precio: $" + item.getPrecioUnitario()
                ));
            }

            document.add(new Paragraph(""));
            document.add(new Paragraph("------------------------------------------------------------"));
            document.add(new Paragraph(
                    "TOTAL: $" + orden.getTotal(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)
            ));

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar PDF de la boleta", e);
        }

        return baos.toByteArray();
    }
}
