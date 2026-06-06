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
        String sql = "SELECT * FROM clientes";
        List<Cliente> lista = new ArrayList<>();
        System.out.println("Listado de clientes: ");

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setUsername(rs.getString("username"));
                c.setPassword(rs.getString("password"));
                c.setEmail(rs.getString("email"));
                c.setNombre(rs.getString("nombre"));
                c.setApellidos(rs.getString("apellidos"));
                c.setDni(rs.getString("dni"));
                c.setTelefono(rs.getString("telefono"));
                c.setDireccion(rs.getString("direccion"));
                
                if (rs.getDate("fecha_registro") != null) {
                    c.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
                }

                lista.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    @Override
    public Cliente buscarPorId(int usuarioId) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        Cliente c = null;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c = new Cliente();
                    c.setId(rs.getInt("id"));
                    c.setUsername(rs.getString("username"));
                    c.setPassword(rs.getString("password"));
                    c.setEmail(rs.getString("email"));
                    c.setNombre(rs.getString("nombre"));
                    c.setApellidos(rs.getString("apellidos"));
                    c.setDni(rs.getString("dni"));
                    c.setTelefono(rs.getString("telefono"));
                    c.setDireccion(rs.getString("direccion"));
                    
                    if (rs.getDate("fecha_registro") != null) {
                        c.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
        return c;
    }

    @Override
    public void actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET username=?, email=?, nombre=?, apellidos=?, dni=?, telefono=?, direccion=? WHERE id=?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cliente.getUsername());
            ps.setString(2, cliente.getEmail());
            ps.setString(3, cliente.getNombre());
            ps.setString(4, cliente.getApellidos());
            ps.setString(5, cliente.getDni());
            ps.setString(6, cliente.getTelefono());
            ps.setString(7, cliente.getDireccion());
            ps.setInt(8, cliente.getId());

            ps.executeUpdate();
            System.out.println("Cliente correctamente actualizado");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void eliminar(int usuarioId) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            
            ps.executeUpdate();
            System.out.println("Cliente correctamente eliminado");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }
    
}