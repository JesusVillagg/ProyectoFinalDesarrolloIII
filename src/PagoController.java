import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PagoController {
    @FXML private RadioButton rbEfectivo;
    @FXML private RadioButton rbTarjeta;
    @FXML private ToggleGroup grupoPago;

    @FXML private VBox boxTarjeta;
    @FXML private TextField txtTitular;
    @FXML private TextField txtNumero;
    @FXML private TextField txtVencimiento;
    @FXML private TextField txtCVV;

    @FXML private Label lblTotalPagar;
    @FXML private Button btnPagar;

    @FXML
    public void initialize() {
        double total = ServicioCarrito.calcularTotal() + 150.00;
        lblTotalPagar.setText(String.format("Total a Pagar: $%.2f", total));

        grupoPago.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == rbTarjeta) {
                boxTarjeta.setVisible(true);
            } else {
                boxTarjeta.setVisible(false);
            }
        });
    }

    @FXML
    void procesarPago(ActionEvent event) {
        if (grupoPago.getSelectedToggle() == null) {
            mostrarAlerta("Selecciona un método de pago.", Alert.AlertType.WARNING);
            return;
        }

        if (rbTarjeta.isSelected()) {
            if (txtTitular.getText().isEmpty() || txtNumero.getText().isEmpty() || txtCVV.getText().isEmpty()) {
                mostrarAlerta("Por favor llena los datos de la tarjeta (Simulados).", Alert.AlertType.WARNING);
                return;
            }
        }

        if (guardarVentaEnBD()) {
            if (rbEfectivo.isSelected()) {
                mostrarAlerta("¡Pedido Registrado!\n\nAcude a cualquier OXXO/Banco y deposita con este folio.\nTu ID de Venta es tu referencia.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("¡Pago Aprobado!\n\nTu tarjeta ha sido procesada exitosamente (Simulación).", Alert.AlertType.INFORMATION);
            }

            ServicioCarrito.vaciarCarrito(Sesion.getIdCliente());
            cerrarVentana();
        }
    }

    @FXML
    void cancelar(ActionEvent event) {
        cerrarVentana();
    }

    private boolean guardarVentaEnBD() {
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            String sqlVenta = "INSERT INTO Venta (fecha, metodo_pago, direccion_entrega, id_cliente) VALUES (?, ?, ?, ?)";
            PreparedStatement pstVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);

            pstVenta.setString(1, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pstVenta.setString(2, rbEfectivo.isSelected() ? "Efectivo/Depósito" : "Tarjeta Crédito/Débito");
            pstVenta.setString(3, "Dirección Registrada");
            pstVenta.setInt(4, Sesion.getIdCliente());

            int affected = pstVenta.executeUpdate();
            if (affected == 0) throw new SQLException("Falló crear la venta");

            ResultSet generatedKeys = pstVenta.getGeneratedKeys();
            int idVenta = 0;
            if (generatedKeys.next()) {
                idVenta = generatedKeys.getInt(1);
            }

            for (ProductoCarrito p : ServicioCarrito.obtenerLista()) {

                int idPresentacion = buscarIdProducto(conn, p.getNombre());

                if (idPresentacion == 0) {
                    throw new SQLException("Producto no encontrado o sin presentación: " + p.getNombre());
                }

                String sqlDetalle = "INSERT INTO Detalle_Venta (cantidad, precio_unitario, id_venta, id_presentacion) VALUES (?, ?, ?, ?)";
                PreparedStatement pstDetalle = conn.prepareStatement(sqlDetalle);
                pstDetalle.setInt(1, p.getCantidad());
                pstDetalle.setDouble(2, p.getPrecio());
                pstDetalle.setInt(3, idVenta);
                pstDetalle.setInt(4, idPresentacion);
                pstDetalle.executeUpdate();

                String sqlStock = "UPDATE Presentacion SET stock = stock - ? WHERE id_presentacion = ?";
                PreparedStatement pstStock = conn.prepareStatement(sqlStock);
                pstStock.setInt(1, p.getCantidad());
                pstStock.setInt(2, idPresentacion);
                pstStock.executeUpdate();
            }

            conn.commit();
            System.out.println("Venta " + idVenta + " registrada con éxito.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            mostrarAlerta("Error al procesar el pedido: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private int buscarIdProducto(Connection conn, String nombreProducto) throws SQLException {
        String sql = "SELECT pre.id_presentacion FROM Presentacion pre " +
                "JOIN Producto p ON pre.id_producto = p.id_producto " +
                "WHERE p.nombre = ? LIMIT 1";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nombreProducto);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("id_presentacion");
        return 0;
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnPagar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
