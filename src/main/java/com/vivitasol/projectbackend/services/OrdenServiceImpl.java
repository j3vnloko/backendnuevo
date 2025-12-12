package com.vivitasol.projectbackend.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.vivitasol.projectbackend.entities.*;
import com.vivitasol.projectbackend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    private static final double IVA_RATE = 0.19;  // 19% IVA

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

        // Generar número de boleta correlativo
        orden.setNumeroBoleta(generarNumeroBoleta());

        List<OrdenItem> items = new ArrayList<>();
        int subtotal = 0;

        for (CarritoItem item : carrito.getItems()) {
            OrdenItem oItem = new OrdenItem();
            oItem.setOrden(orden);
            oItem.setProducto(item.getProducto());
            oItem.setCantidad(item.getCantidad());
            oItem.setPrecioUnitario(item.getProducto().getPrecio().intValue());

            subtotal += item.getCantidad() * item.getProducto().getPrecio().intValue();
            items.add(oItem);
        }

        // Calcular IVA y total
        int iva = (int) Math.round(subtotal * IVA_RATE);
        int total = subtotal + iva;

        orden.setSubtotal(subtotal);
        orden.setIva(iva);
        orden.setTotal(total);
        orden.setItems(items);

        ordenRepo.save(orden);
        ordenItemRepo.saveAll(items);

        // Vaciar carrito
        carrito.getItems().clear();
        carritoRepo.save(carrito);

        return orden;
    }

    private String generarNumeroBoleta() {
        Long count = ordenRepo.count();
        return String.format("BOL-%07d", count + 1);
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

            // ===== ENCABEZADO =====
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("BOLETA ELECTRÓNICA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            document.add(new Paragraph(" "));

            // ===== INFORMACIÓN DE LA EMPRESA =====
            document.add(new Paragraph("Sativamente SpA"));
            document.add(new Paragraph("RUT: 77.123.456-7"));
            document.add(new Paragraph("Dirección: Av. Principal 123, Santiago"));
            document.add(new Paragraph("Teléfono: +56 9 1234 5678"));
            document.add(new Paragraph(" "));

            // ===== INFORMACIÓN DE LA BOLETA =====
            document.add(new Paragraph("------------------------------------------------------------"));
            document.add(new Paragraph("Número de Boleta: " + orden.getNumeroBoleta()));
            document.add(new Paragraph("Fecha: " + formatFecha(orden.getFecha())));
            document.add(new Paragraph("Cliente: " + orden.getUsuario().getNombre()));
            document.add(new Paragraph("Email: " + orden.getUsuario().getEmail()));
            document.add(new Paragraph("------------------------------------------------------------"));
            document.add(new Paragraph(" "));

            // ===== DETALLE DE PRODUCTOS =====
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            document.add(new Paragraph("DETALLE DE PRODUCTOS", boldFont));
            document.add(new Paragraph(" "));

            for (OrdenItem item : orden.getItems()) {
                String linea = String.format(
                    "%s\n  Cantidad: %d | Precio Unit: $%,d | Subtotal: $%,d",
                    item.getProducto().getNombre(),
                    item.getCantidad(),
                    item.getPrecioUnitario(),
                    item.getCantidad() * item.getPrecioUnitario()
                );
                document.add(new Paragraph(linea));
                document.add(new Paragraph(" "));
            }

            document.add(new Paragraph("------------------------------------------------------------"));

            // ===== TOTALES =====
            document.add(new Paragraph(String.format("Subtotal: $%,d", orden.getSubtotal())));
            document.add(new Paragraph(String.format("IVA (19%%): $%,d", orden.getIva())));
            document.add(new Paragraph(" "));

            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph totalPara = new Paragraph(
                String.format("TOTAL: $%,d", orden.getTotal()),
                totalFont
            );
            document.add(totalPara);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("------------------------------------------------------------"));
            document.add(new Paragraph("Gracias por su compra"));
            document.add(new Paragraph("Sativamente - Productos de Calidad"));

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar PDF de la boleta", e);
        }

        return baos.toByteArray();
    }

    @Override
    public List<Orden> obtenerHistorialUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ordenRepo.findByUsuarioOrderByFechaDesc(usuario);
    }

    @Override
    public List<Orden> obtenerTodasLasOrdenes() {
        return ordenRepo.findAllByOrderByFechaDesc();
    }

    private String formatFecha(LocalDateTime fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return fecha.format(formatter);
    }
}