package src.model;

import java.time.LocalDate;

public class Empleado extends Usuario{
    
    private String     cargo;
    private Double salario;
    private LocalDate  fechaAlta;
    
    public Empleado() {
    }

    public Empleado(int id, String username, String password, String email, String nombre, String apellidos, String dni,
            String rol, String cargo, Double salario, LocalDate fechaAlta) {
        super(id, username, password, email, nombre, apellidos, dni, rol);
        this.cargo = cargo;
        this.salario = salario;
        this.fechaAlta = fechaAlta;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

}
