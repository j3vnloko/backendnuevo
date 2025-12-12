package com.vivitasol.projectbackend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroBoleta;  // Formato: BOL-0000001

    private LocalDateTime fecha = LocalDateTime.now();
    
    private Integer subtotal;
    private Integer iva;  // 19% del subtotal
    private Integer total;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL)
    private List<OrdenItem> items;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroBoleta() {
        return numeroBoleta;
    }

    public void setNumeroBoleta(String numeroBoleta) {
        this.numeroBoleta = numeroBoleta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Integer subtotal) {
        this.subtotal = subtotal;
    }

    public Integer getIva() {
        return iva;
    }

    public void setIva(Integer iva) {
        this.iva = iva;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<OrdenItem> getItems() {
        return items;
    }

    public void setItems(List<OrdenItem> items) {
        this.items = items;
    }
}