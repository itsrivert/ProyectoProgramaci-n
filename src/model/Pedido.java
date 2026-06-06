package src.model;

import java.time.LocalDate;

public class Pedido {
    private int        id;
    private int        clienteId;
    private int        discoId;
    private LocalDate  fecha;
    private int        cantidad;
    private Double     total;
    private String     estado;   // pendiente | completado | cancelado 
    
    public Pedido() {
    }

    public Pedido(int id, int clienteId, int discoId, LocalDate fecha, int cantidad, Double total, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.discoId = discoId;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.total = total;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public int getDiscoId() {
        return discoId;
    }

    public void setDiscoId(int discoId) {
        this.discoId = discoId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


}
