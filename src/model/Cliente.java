package src.model;

import java.time.LocalDate;

public class Cliente extends Usuario{
    private String    telefono;
    private String    direccion;
    private LocalDate fechaRegistro;
    
    public Cliente() {
    }

    public Cliente(int id, String username, String password, String email, String nombre, String apellidos, String dni,
            String rol, String telefono, String direccion, LocalDate fechaRegistro) {
        super(id, username, password, email, nombre, apellidos, dni, rol);
        this.telefono = telefono;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    
}
