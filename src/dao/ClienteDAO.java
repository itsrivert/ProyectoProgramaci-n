package src.dao;

import java.sql.SQLException;
import java.util.List;
import src.model.Cliente;

public interface ClienteDAO {
    List<Cliente> listarTodos() throws SQLException;

    void actualizar(Cliente cliente) throws SQLException;

    void eliminar(int usuarioId) throws SQLException;

    Cliente buscarPorId(int usuarioId) throws SQLException;
}
