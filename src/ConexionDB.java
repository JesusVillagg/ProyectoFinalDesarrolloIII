import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class ConexionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/Casa_de_Jade?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        Connection conexion = null;
        try {
            // 1. Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Intentar conectar
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("¡Conexión exitosa a la Base de Datos!");

        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el Driver de MySQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error de SQL: No se pudo conectar a la base de datos.");
            e.printStackTrace();
        }
        return conexion;
    }
    // Método para contar cuántos registros hay en una tabla
    public static int contarRegistros(String nombreTabla) {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM " + nombreTabla;

        try {
            Connection con = getConnection(); // Usamos tu misma conexión
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql);

                if (rs.next()) {
                    total = rs.getInt(1); // Obtiene el número del conteo
                }
                con.close(); // Cerramos para no dejar basura
            }
        } catch (SQLException e) {
            System.err.println("Error al contar en la tabla: " + nombreTabla);
            e.printStackTrace();
        }
        return total;
    }

}
