import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HistorialComprasController {

    @FXML private TableView<Venta> tablaPedidos;
    @FXML private TableColumn<Venta, Integer> colId;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, String> colProductos;
    @FXML private TableColumn<Venta, Double> colTotal;
    @FXML private TableColumn<Venta, String> colMetodo;
    @FXML private TableColumn<Venta, String> colDireccion;

    @FXML private TextField buscador;

    private ObservableList<Venta> listaVentas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarPedidos("");
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colProductos.setCellValueFactory(new PropertyValueFactory<>("productosResumen"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colMetodo.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccionEntrega"));
    }

    private void cargarPedidos(String filtro) {
        listaVentas.clear();
        int miId = Sesion.getIdCliente();
        String sql = "SELECT v.id_venta, v.fecha, v.metodo_pago, v.direccion_entrega, v.id_cliente, " +
                "COALESCE(GROUP_CONCAT(p.nombre SEPARATOR ', '), 'Sin productos') as resumen_productos, " +
                "COALESCE(SUM(d.cantidad * d.precio_unitario), 0) as total_calculado " +
                "FROM Venta v " +
                "LEFT JOIN Detalle_Venta d ON v.id_venta = d.id_venta " +
                "LEFT JOIN Presentacion pre ON d.id_presentacion = pre.id_presentacion " +
                "LEFT JOIN Producto p ON pre.id_producto = p.id_producto " +
                "WHERE v.id_cliente = ? ";

        if (!filtro.isEmpty()) {
            sql += " AND (v.id_venta LIKE ? OR p.nombre LIKE ?) ";
        }

        sql += " GROUP BY v.id_venta ORDER BY v.fecha DESC";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, miId);
            if (!filtro.isEmpty()) {
                stmt.setString(2, "%" + filtro + "%");
                stmt.setString(3, "%" + filtro + "%");
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                listaVentas.add(new Venta(
                        rs.getInt("id_venta"),
                        rs.getString("fecha"),
                        rs.getDouble("total_calculado"),
                        rs.getString("metodo_pago"),
                        rs.getString("direccion_entrega"),
                        rs.getInt("id_cliente"),
                        rs.getString("resumen_productos")
                ));
            }
            tablaPedidos.setItems(listaVentas);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void buscarPedido() {
        String texto = buscador.getText();
        cargarPedidos(texto);
    }

    @FXML
    void cancelarUltimoPedido(ActionEvent event) {
        if (listaVentas.isEmpty()) {
            mostrarAlerta("No hay pedidos para cancelar.");
            return;
        }

        Venta ultimoPedidoReal = listaVentas.get(0);

        Venta pedidoSeleccionado = tablaPedidos.getSelectionModel().getSelectedItem();

        if (pedidoSeleccionado == null) {
            mostrarAlerta("Por favor selecciona un pedido de la tabla.");
            return;
        }

        if (pedidoSeleccionado.getIdVenta() != ultimoPedidoReal.getIdVenta()) {
            mostrarAlerta("Acción denegada: Solo puedes cancelar tu último pedido realizado.");
            return;
        }

        eliminarVentaDB(pedidoSeleccionado.getIdVenta());
    }

    private void eliminarVentaDB(int idVenta) {
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            String sqlDetalles = "DELETE FROM Detalle_Venta WHERE id_venta = ?";
            try (PreparedStatement pstD = conn.prepareStatement(sqlDetalles)) {
                pstD.setInt(1, idVenta);
                pstD.executeUpdate();
            }

            String sqlVenta = "DELETE FROM Venta WHERE id_venta = ?";
            try (PreparedStatement pstV = conn.prepareStatement(sqlVenta)) {
                pstV.setInt(1, idVenta);
                int afectados = pstV.executeUpdate();

                if (afectados > 0) {
                    conn.commit();
                    mostrarAlerta("Pedido cancelado y eliminado correctamente.");
                    cargarPedidos("");
                } else {
                    conn.rollback();
                    mostrarAlerta("No se pudo eliminar el pedido.");
                }
            }

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            mostrarAlerta("Error de base de datos: " + e.getMessage());
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @FXML
    void btnNuevoPedido(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("catalogoCliente.fxml"));
            Parent vistaCatalogo = loader.load();

            Node boton = (Node) event.getSource();
            Scene ventanaPrincipal = boton.getScene();
            Pane panelPrincipal = (Pane) ventanaPrincipal.lookup("#panelContenido");

            if (panelPrincipal != null) {
                panelPrincipal.getChildren().clear();
                panelPrincipal.getChildren().add(vistaCatalogo);

                if (vistaCatalogo instanceof javafx.scene.layout.Region) {
                    javafx.scene.layout.Region region = (javafx.scene.layout.Region) vistaCatalogo;

                    // Amarramos ancho y alto
                    region.prefWidthProperty().bind(panelPrincipal.widthProperty());
                    region.prefHeightProperty().bind(panelPrincipal.heightProperty());
                }

            } else {
                System.out.println("Error: No encontré el panelContenido.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void cambiarVentana(ActionEvent event, String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    @FXML
    void recargarInicio(ActionEvent event) {
        try {
            //cargar el archivo principal
            Parent root = FXMLLoader.load(getClass().getResource("/menucliente.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
