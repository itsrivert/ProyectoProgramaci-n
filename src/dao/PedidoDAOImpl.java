package src.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import src.db.ConexionDB;
import src.dto.PedidoDTO;
import src.model.Pedido;

public class PedidoDAOImpl implements PedidoDAO {

    @Override
    public void insertar(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (cliente_id, disco_id, fecha, cantidad, total, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, pedido.getClienteId());
            ps.setInt(2, pedido.getDiscoId());
            
            ps.setDate(3, pedido.getFecha() != null ? java.sql.Date.valueOf(pedido.getFecha()) : null);
            ps.setInt(4, pedido.getCantidad());
            ps.setDouble(5, pedido.getTotal());
            ps.setString(6, pedido.getEstado());

            ps.executeUpdate();
            System.out.println("Pedido correctamente añadido");
        } catch (SQLException e) {
            System.out.println("Error al insertar pedido: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<PedidoDTO> listarTodos() throws SQLException {
        // JOIN para ir directo a la tabla usuarios, evitando buscar nombre en clientes
        String sql = "SELECT p.id, u.nombre, u.apellidos, u.username, d.titulo, d.artista, p.fecha, p.cantidad, p.total, p.estado " +
                     "FROM pedidos p " +
                     "JOIN usuarios u ON p.cliente_id = u.id " +
                     "JOIN discos d ON p.disco_id = d.id";
                     
        List<PedidoDTO> lista = new ArrayList<>();

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                PedidoDTO dto = new PedidoDTO();
                dto.setPedidoId(rs.getInt("id"));
                
                // Une Nombre + Apellidos para rellenar el campo del cliente en el DTO
                String nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellidos");
                dto.setClienteNombre(nombreCompleto);
                
                dto.setClienteUsername(rs.getString("username"));
                dto.setDiscoTitulo(rs.getString("titulo"));
                dto.setDiscoArtista(rs.getString("artista"));
                
                if (rs.getDate("fecha") != null) {
                    dto.setFecha(rs.getDate("fecha").toLocalDate());
                }
                
                dto.setCantidad(rs.getInt("cantidad"));
                dto.setTotal(rs.getDouble("total"));
                dto.setEstado(rs.getString("estado"));

                lista.add(dto);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pedidos: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Pedido correctamente eliminado");
        } catch (SQLException e) {
            System.out.println("Error al eliminar pedido: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void actualizarEstado(int id, String nuevoEstado) throws SQLException {
        String sql = "UPDATE pedidos SET estado = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);

            ps.executeUpdate();
            System.out.println("Estado del pedido correctamente actualizado");
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del pedido: " + e.getMessage());
            throw e;
        }
    }
}