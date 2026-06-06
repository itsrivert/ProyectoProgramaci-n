package src.dto;

import java.time.LocalDate;

public class PedidoDTO {
    private int        pedidoId;
    private String     clienteNombre;   // nombre + apellidos del cliente
    private String     clienteUsername;
    private String     discoTitulo;
    private String     discoArtista;
    private LocalDate  fecha;
    private int        cantidad;
    private Double     total;
    private String     estado;
    
    public PedidoDTO() {
    }

    public PedidoDTO(int pedidoId, String clienteNombre, String clienteUsername, String discoTitulo,
            String discoArtista, LocalDate fecha, int cantidad, Double total, String estado) {
        this.pedidoId = pedidoId;
        this.clienteNombre = clienteNombre;
        this.clienteUsername = clienteUsername;
        this.discoTitulo = discoTitulo;
        this.discoArtista = discoArtista;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.total = total;
        this.estado = estado;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteUsername() {
        return clienteUsername;
    }

    public void setClienteUsername(String clienteUsername) {
        this.clienteUsername = clienteUsername;
    }

    public String getDiscoTitulo() {
        return discoTitulo;
    }

    public void setDiscoTitulo(String discoTitulo) {
        this.discoTitulo = discoTitulo;
    }

    public String getDiscoArtista() {
        return discoArtista;
    }

    public void setDiscoArtista(String discoArtista) {
        this.discoArtista = discoArtista;
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
