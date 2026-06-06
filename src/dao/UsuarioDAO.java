package src.dao;

import src.model.Usuario;
import java.sql.SQLException;
import java.util.List;

public interface UsuarioDAO {
    Usuario validar(String username, String password) throws SQLException;     /** Comprueba credenciales y devuelve el Usuario si son correctas, null si no. */
    void registrar(Usuario usuario) throws SQLException;     /** Inserta en usuarios + tabla hija en una sola transacción. */
    List<Usuario> listarTodos() throws SQLException;
    void actualizar(Usuario usuario) throws SQLException;
    void eliminar(int id) throws SQLException;
    void cambiarPassword(int id, String nuevaPassword) throws SQLException;     /** Cambia la contraseña de un usuario por su id. */
}