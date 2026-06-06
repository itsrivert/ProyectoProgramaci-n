package src.dao;

import java.sql.SQLException;
import java.util.List;

import src.model.Disco;

public interface DiscoDAO {
    void insertar(Disco disco) throws SQLException;
    void actualizar(Disco disco) throws SQLException;
    List<Disco> listarTodos() throws SQLException;
    void eliminar(int id) throws SQLException;
    Disco buscarPorId(int id) throws SQLException;
}
