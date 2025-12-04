import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardController {

    @FXML private Label lblTotalProductos;
    @FXML private Label lblTotalClientes;

    @FXML
    public void initialize() {
        actualizarContadores();
    }

    private void actualizarContadores() {
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Contar Productos
            ResultSet rsProd = stmt.executeQuery("SELECT COUNT(*) FROM Producto");
            if (rsProd.next()) {
                lblTotalProductos.setText(String.valueOf(rsProd.getInt(1)));
            }
            rsProd.close();

            // 2. Contar Clientes
            ResultSet rsCli = stmt.executeQuery("SELECT COUNT(*) FROM Cliente");
            if (rsCli.next()) {
                lblTotalClientes.setText(String.valueOf(rsCli.getInt(1)));
            }
            rsCli.close();

        } catch (Exception e) {
            e.printStackTrace();
            lblTotalProductos.setText("-");
            lblTotalClientes.setText("-");
        }
    }
}