package src.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import src.db.ConexionDB;
import src.model.Cliente;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public List<Cliente> listarTodos() throws SQLException {
        // JOIN obligatorio para juntar los datos de ambas tablas
        String sql = "SELECT u.id, u.username, u.password, u.email, u.nombre, u.apellidos, u.dni, u.rol, " +
                     "c.telefono, c.direccion, c.fecha_registro " +
                     "FROM clientes c " +
                     "JOIN usuarios u ON c.id_usuario = u.id";
                     
        List<Cliente> lista = new ArrayList<>();
        System.out.println("Listado de clientes mediante JOIN: ");

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Cliente c = new Cliente();
                // Datos que vienen de la tabla usuarios
                c.setId(rs.getInt("id"));
                c.setUsername(rs.getString("username"));
                c.setPassword(rs.getString("password"));
                c.setEmail(rs.getString("email"));
                c.setNombre(rs.getString("nombre"));
                c.setApellidos(rs.getString("apellidos"));
                c.setDni(rs.getString("dni"));
                c.setRol(rs.getString("rol"));
                
                // Datos que vienen de la tabla clientes
                c.setTelefono(rs.getString("telefono"));
                c.setDireccion(rs.getString("direccion"));
                
                if (rs.getDate("fecha_registro") != null) {
                    c.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
                }
                
                lista.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error en listarTodos de Clientes: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    @Override
    public void actualizar(Cliente cliente) throws SQLException {
        // Primero actualiza los datos comunes en la tabla usuarios
        String sqlUsuario = "UPDATE usuarios SET username=?, email=?, nombre=?, apellidos=?, dni=? WHERE id=?";
        // Luego actualiza los datos específicos en la tabla clientes
        String sqlCliente = "UPDATE clientes SET telefono=?, direccion=? WHERE id_usuario=?";

        try (Connection conn = ConexionDB.getConnection()) {
            conn.setAutoCommit(false); // Activamos transacción
            
            try (PreparedStatement psU = conn.prepareStatement(sqlUsuario);
                 PreparedStatement psC = conn.prepareStatement(sqlCliente)) {
                
                // Parámetros de usuario
                psU.setString(1, cliente.getUsername());
                psU.setString(2, cliente.getEmail());
                psU.setString(3, cliente.getNombre());
                psU.setString(4, cliente.getApellidos());
                psU.setString(5, cliente.getDni());
                psU.setInt(6, cliente.getId());
                psU.executeUpdate();
                
                // Parámetros de cliente
                psC.setString(1, cliente.getTelefono());
                psC.setString(2, cliente.getDireccion());
                psC.setInt(3, cliente.getId());
                psC.executeUpdate();
                
                conn.commit(); // Guardamos los cambios de ambas tablas
                System.out.println("Cliente y Usuario correctamente actualizados");
            } catch (SQLException e) {
                conn.rollback(); // Por si algo falla
                throw e;
            }
        }
    }

    @Override
    public void eliminar(int usuarioId) throws SQLException {
        // Primero borramos de la tabla hija (clientes) y luego de la padre (usuarios), pq están vinculadas
        String sqlCliente = "DELETE FROM clientes WHERE id_usuario = ?";
        String sqlUsuario = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement psC = conn.prepareStatement(sqlCliente);
                 PreparedStatement psU = conn.prepareStatement(sqlUsuario)) {
                
                psC.setInt(1, usuarioId);
                psC.executeUpdate();
                
                psU.setInt(1, usuarioId);
                psU.executeUpdate();
                
                conn.commit();
                System.out.println("Cliente correctamente eliminado de ambas tablas");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public Cliente buscarPorId(int usuarioId) throws SQLException {
        String sql = "SELECT u.id, u.username, u.password, u.email, u.nombre, u.apellidos, u.dni, u.rol, " +
                     "c.telefono, c.direccion, c.fecha_registro " +
                     "FROM clientes c " +
                     "JOIN usuarios u ON c.id_usuario = u.id " +
                     "WHERE u.id = ?";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente();
                    c.setId(rs.getInt("id"));
                    c.setUsername(rs.getString("username"));
                    c.setPassword(rs.getString("password"));
                    c.setEmail(rs.getString("email"));
                    c.setNombre(rs.getString("nombre"));
                    c.setApellidos(rs.getString("apellidos"));
                    c.setDni(rs.getString("dni"));
                    c.setRol(rs.getString("rol"));
                    c.setTelefono(rs.getString("telefono"));
                    c.setDireccion(rs.getString("direccion"));
                    if (rs.getDate("fecha_registro") != null) {
                        c.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
                    }
                    return c;
                }
            }
        }
        return null;
    }
}