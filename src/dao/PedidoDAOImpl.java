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
            
            // Convertimos LocalDate a java.sql.Date
            ps.setDate(3, pedido.getFecha() != null ? java.sql.Date.valueOf(pedido.getFecha()) : null);
            ps.setInt(4, pedido.getCantidad());
            ps.setDouble(5, pedido.getTotal());
            ps.setString(6, pedido.getEstado());

            ps.executeUpdate();
            System.out.println("Pedido correctamente añadido");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<PedidoDTO> listarTodos() throws SQLException {
        // Consulta SQL con JOIN trayendo exactamente los campos que pide tu PedidoDTO
        String sql = "SELECT p.id, p.fecha, p.cantidad, p.total, p.estado, " +
                    "c.nombre AS cliente_nombre, c.apellidos AS cliente_apellidos, c.username AS cliente_username, " +
                    "d.titulo AS disco_titulo, d.artista AS disco_artista " +
                    "FROM pedidos p " +
                    "JOIN clientes c ON p.cliente_id = c.id " +
                    "JOIN discos d ON p.disco_id = d.id";
                    
        List<PedidoDTO> lista = new ArrayList<>();
        System.out.println("Listado de pedidos (DTO): ");

        try (Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                PedidoDTO dto = new PedidoDTO();
                
                dto.setPedidoId(rs.getInt("id"));
                
                // Datos del Cliente
                String nombreCompleto = rs.getString("cliente_nombre") + " " + rs.getString("cliente_apellidos");
                dto.setClienteNombre(nombreCompleto);
                dto.setClienteUsername(rs.getString("cliente_username"));
                
                // Datos del Disco
                dto.setDiscoTitulo(rs.getString("disco_titulo"));
                dto.setDiscoArtista(rs.getString("disco_artista"));
                
                // Datos generales del Pedido
                if (rs.getDate("fecha") != null) {
                    dto.setFecha(rs.getDate("fecha").toLocalDate());
                }
                dto.setCantidad(rs.getInt("cantidad"));
                dto.setTotal(rs.getDouble("total"));
                dto.setEstado(rs.getString("estado"));

                lista.add(dto);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
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
            System.out.println("Error: " + e.getMessage());
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
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }
}