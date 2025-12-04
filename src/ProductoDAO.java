import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT p.id_producto, p.nombre, p.marca, pre.precio, p.imagen " +
                "FROM producto p " +
                "JOIN presentacion pre ON p.id_producto = pre.id_producto";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("marca"),
                        rs.getDouble("precio"),
                        rs.getString("imagen")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}
