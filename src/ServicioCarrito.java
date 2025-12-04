import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServicioCarrito {
    private static ObservableList<ProductoCarrito> productosEnCarrito = FXCollections.observableArrayList();

    public static void cargarCarritoUsuario(int idCliente) {
        productosEnCarrito.clear();
        String sql = "SELECT p.nombre, p.precio, c.cantidad, pre.id_presentacion " +
                "FROM Carrito c " +
                "JOIN Presentacion pre ON c.id_presentacion = pre.id_presentacion " +
                "JOIN Producto p ON pre.id_producto = p.id_producto " +
                "WHERE c.id_cliente = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                ProductoCarrito pc = new ProductoCarrito(
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad")

                );
                productosEnCarrito.add(pc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void agregarProducto(ProductoCarrito producto, int idCliente) {
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();

            int idPresentacion = buscarIdPresentacion(conn, producto.getNombre());

            if (idPresentacion > 0) {
                if (yaExisteEnCarritoBD(conn, idCliente, idPresentacion)) {
                    String sqlUpdate = "UPDATE Carrito SET cantidad = cantidad + ? WHERE id_cliente = ? AND id_presentacion = ?";
                    PreparedStatement ps = conn.prepareStatement(sqlUpdate);
                    ps.setInt(1, producto.getCantidad());
                    ps.setInt(2, idCliente);
                    ps.setInt(3, idPresentacion);
                    ps.executeUpdate();
                } else {
                    String sqlInsert = "INSERT INTO Carrito (id_cliente, id_presentacion, cantidad) VALUES (?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(sqlInsert);
                    ps.setInt(1, idCliente);
                    ps.setInt(2, idPresentacion);
                    ps.setInt(3, producto.getCantidad());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (ProductoCarrito p : productosEnCarrito) {
            if (p.getNombre().equals(producto.getNombre())) {
                p.setCantidad(p.getCantidad() + producto.getCantidad());
                return;
            }
        }
        productosEnCarrito.add(producto);
    }

    public static ObservableList<ProductoCarrito> obtenerLista() {
        return productosEnCarrito;
    }

    public static void vaciarCarrito(int idCliente) {
        String sql = "DELETE FROM Carrito WHERE id_cliente = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        productosEnCarrito.clear();
    }

    public static double calcularTotal(){
        double total = 0;
        for (ProductoCarrito p : productosEnCarrito) {
            total += p.getTotal();
        }
        return total;
    }

    private static int buscarIdPresentacion(Connection conn, String nombre) throws SQLException {
        String sql = "SELECT pre.id_presentacion FROM Presentacion pre " +
                "JOIN Producto p ON pre.id_producto = p.id_producto " +
                "WHERE p.nombre = ? LIMIT 1";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nombre);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("id_presentacion");
        return 0;
    }

    private static boolean yaExisteEnCarritoBD(Connection conn, int idCliente, int idPresentacion) throws SQLException {
        String sql = "SELECT id_carrito FROM Carrito WHERE id_cliente = ? AND id_presentacion = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idCliente);
        ps.setInt(2, idPresentacion);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }
}

