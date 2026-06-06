package src.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import src.db.ConexionDB;
import src.model.Disco;

public class DiscoDAOImpl implements DiscoDAO {

    @Override
    public void insertar(Disco disco) throws SQLException {
        String sql = "INSERT INTO discos (titulo, artista, genero, anio, formato, precio, stock, descripcion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
                ps.setString(1, disco.getTitulo());
                ps.setString(2, disco.getArtista());
                ps.setString(3, disco.getGenero());
                ps.setInt(4, disco.getAnio());
                ps.setString(5, disco.getFormato());
                ps.setDouble(6, disco.getPrecio());
                ps.setInt(7, disco.getStock());
                ps.setString(8, disco.getDescripcion());

            ps.executeUpdate();
            System.out.println("Disco correctamente añadido");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void actualizar(Disco disco) throws SQLException {
        String sql = "UPDATE discos SET titulo = ?, artista = ?, genero = ?, anio = ?, formato = ?, precio = ?, stock = ?, descripcion = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
                ps.setString(1, disco.getTitulo());
                ps.setString(2, disco.getArtista());
                ps.setString(3, disco.getGenero());
                ps.setInt(4, disco.getAnio());
                ps.setString(5, disco.getFormato());
                ps.setDouble(6, disco.getPrecio());
                ps.setInt(7, disco.getStock());
                ps.setString(8, disco.getDescripcion());
                ps.setInt(9, disco.getId());

            ps.executeUpdate();
            System.out.println("Disco correctamente actualizado");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Disco> listarTodos() throws SQLException {
        String sql = "SELECT * FROM discos";
        List<Disco> lista = new ArrayList<>();
        System.out.println("Listado de discos: ");

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Disco d = new Disco();
                d.setId(rs.getInt("id"));
                d.setTitulo(rs.getString("titulo"));
                d.setArtista(rs.getString("artista"));
                d.setGenero(rs.getString("genero"));
                d.setAnio(rs.getInt("anio"));
                d.setFormato(rs.getString("formato"));
                d.setPrecio(rs.getDouble("precio"));
                d.setStock(rs.getInt("stock"));
                d.setDescripcion(rs.getString("descripcion"));

                lista.add(d);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM discos WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            ps.executeUpdate();
            System.out.println("Disco correctamente eliminado");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Disco buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM discos WHERE id = ?";
        Disco d = null;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    d = new Disco();
                    d.setId(rs.getInt("id"));
                    d.setTitulo(rs.getString("titulo"));
                    d.setArtista(rs.getString("artista"));
                    d.setGenero(rs.getString("genero"));
                    d.setAnio(rs.getInt("anio"));
                    d.setFormato(rs.getString("formato"));
                    d.setPrecio(rs.getDouble("precio"));
                    d.setStock(rs.getInt("stock"));
                    d.setDescripcion(rs.getString("descripcion"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
        return d;
    }
}
