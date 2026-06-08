package src.dao;

import java.sql.SQLException;
import java.util.List;

import src.dto.PedidoDTO;
import src.model.Pedido;

public interface PedidoDAO {
    void insertar(Pedido pedido) throws SQLException;
    List<PedidoDTO> listarTodos() throws SQLException;
    void eliminar(int id) throws SQLException;
}
