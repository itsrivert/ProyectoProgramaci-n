package src.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    
    private static final String URL = "jdbc:mysql://localhost:3306/miload?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "root";

    private ConexionDB() {}

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado: " + e.getMessage(), e);
        }
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    public static void iniciarTransaccion(Connection con) throws SQLException {
        con.setAutoCommit(false);
    }

    public static void confirmar(Connection con) throws SQLException {
        con.commit();
        con.setAutoCommit(true);
    }

    public static void revertir(Connection con) {
        if (con != null) {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
    }
}
