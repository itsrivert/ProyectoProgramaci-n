package src.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import src.db.ConexionDB;
import src.model.Cliente;
import src.model.Empleado;
import src.model.Usuario;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario validar(String username, String password) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        Usuario u = null;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setEmail(rs.getString("email"));
                    u.setNombre(rs.getString("nombre"));
                    u.setApellidos(rs.getString("apellidos"));
                    u.setDni(rs.getString("dni"));
                    u.setRol(rs.getString("rol"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
        return u;
    }
    
    @Override
    public void registrar(Usuario usuario) throws SQLException {
        String sqlUsuario = "INSERT INTO usuarios (username, password, email, nombre, apellidos, dni, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int nuevoId;

                // Insertar en usuarios y obtener el id generado
                try (PreparedStatement ps = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, usuario.getUsername());
                    ps.setString(2, usuario.getPassword());
                    ps.setString(3, usuario.getEmail());
                    ps.setString(4, usuario.getNombre());
                    ps.setString(5, usuario.getApellidos());
                    ps.setString(6, usuario.getDni());
                    ps.setString(7, usuario.getRol());
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            nuevoId = keys.getInt(1);
                        } else {
                            throw new SQLException("No se obtuvo el ID generado para el nuevo usuario.");
                        }
                    }
                }

                // Insertar en la tabla hija según el tipo
                if (usuario instanceof Cliente c) {
                    String sqlCliente = "INSERT INTO clientes (usuario_id, telefono, direccion, fecha_registro) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlCliente)) {
                        ps2.setInt(1, nuevoId);
                        ps2.setString(2, c.getTelefono());
                        ps2.setString(3, c.getDireccion());
                        ps2.setDate(4, c.getFechaRegistro() != null
                                ? java.sql.Date.valueOf(c.getFechaRegistro()) : null);
                        ps2.executeUpdate();
                    }
                } else if (usuario instanceof Empleado emp) {
                    String sqlEmpleado = "INSERT INTO empleados (usuario_id, cargo, salario, fecha_alta) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlEmpleado)) {
                        ps2.setInt(1, nuevoId);
                        ps2.setString(2, emp.getCargo());
                        ps2.setDouble(3, emp.getSalario() != null ? emp.getSalario() : 0.0);
                        ps2.setDate(4, emp.getFechaAlta() != null
                                ? java.sql.Date.valueOf(emp.getFechaAlta()) : null);
                        ps2.executeUpdate();
                    }
                }

                conn.commit();
                System.out.println("Usuario correctamente registrado (id=" + nuevoId + ")");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT * FROM usuarios";
        List<Usuario> lista = new ArrayList<>();
        System.out.println("Listado de usuarios: ");

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setEmail(rs.getString("email"));
                u.setNombre(rs.getString("nombre"));
                u.setApellidos(rs.getString("apellidos"));
                u.setDni(rs.getString("dni"));
                u.setRol(rs.getString("rol"));

                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    @Override
    public void actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET username = ?, email = ?, nombre = ?, apellidos = ?, dni = ?, rol = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getApellidos());
            ps.setString(5, usuario.getDni());
            ps.setString(6, usuario.getRol());
            ps.setInt(7, usuario.getId());

            ps.executeUpdate();
            System.out.println("Usuario correctamente actualizado");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Usuario correctamente eliminado");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void cambiarPassword(int id, String nuevaPassword) throws SQLException {
        String sql = "UPDATE usuarios SET password = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nuevaPassword);
            ps.setInt(2, id);

            ps.executeUpdate();
            System.out.println("Contraseña correctamente modificada");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }
}