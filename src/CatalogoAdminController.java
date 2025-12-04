import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.sql.*;

public class CatalogoAdminController {

    @FXML private TableView<ProductoTabla> tablaProductos;
    @FXML private TableColumn<ProductoTabla, Integer> colId;
    @FXML private TableColumn<ProductoTabla, String> colNombre;
    @FXML private TableColumn<ProductoTabla, String> colMarca;
    @FXML private TableColumn<ProductoTabla, Double> colPrecio;
    @FXML private TableColumn<ProductoTabla, Integer> colStock;
    @FXML private TableColumn<ProductoTabla, Integer> colProveedor;

    @FXML private TextField txtNombre;
    @FXML private TextField txtMarca;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private ComboBox<String> comboProveedor;

    private ObservableList<ProductoTabla> listaProductos = FXCollections.observableArrayList();
    private ObservableList<String> listaProveedores = FXCollections.observableArrayList();
    private ProductoTabla productoSeleccionado;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarProveedores();
        cargarProductos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
    }

    private void cargarProveedores() {
        listaProveedores.clear();
        String sql = "SELECT id_proveedor, nombre FROM Proveedor";
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                listaProveedores.add(rs.getInt("id_proveedor") + " - " + rs.getString("nombre"));
            }
            comboProveedor.setItems(listaProveedores);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarProductos() {
        listaProductos.clear();
        // CORRECCIÓN AQUÍ: Usamos LEFT JOIN para traer productos aunque no tengan presentación
        String sql = "SELECT p.id_producto, p.nombre, p.marca, p.id_proveedor, " +
                "IFNULL(pre.id_presentacion, 0) as id_presentacion, " +
                "IFNULL(pre.precio, 0) as precio, " +
                "IFNULL(pre.stock, 0) as stock " +
                "FROM Producto p " +
                "LEFT JOIN Presentacion pre ON p.id_producto = pre.id_producto";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                listaProductos.add(new ProductoTabla(
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("marca"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getInt("id_proveedor"),
                        rs.getInt("id_presentacion")
                ));
            }
            tablaProductos.setItems(listaProductos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void agregarProducto(ActionEvent event) {
        if (!validarCampos()) return;

        int idProv = Integer.parseInt(comboProveedor.getValue().split(" - ")[0]);
        String nombre = txtNombre.getText();
        String marca = txtMarca.getText();
        double precio = Double.parseDouble(txtPrecio.getText());
        int stock = Integer.parseInt(txtStock.getText());

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            String sqlProd = "INSERT INTO Producto (nombre, marca, id_proveedor) VALUES (?, ?, ?)";
            PreparedStatement stmtProd = conn.prepareStatement(sqlProd, Statement.RETURN_GENERATED_KEYS);
            stmtProd.setString(1, nombre);
            stmtProd.setString(2, marca);
            stmtProd.setInt(3, idProv);
            stmtProd.executeUpdate();

            ResultSet rsKeys = stmtProd.getGeneratedKeys();
            int idGenerado = 0;
            if (rsKeys.next()) {
                idGenerado = rsKeys.getInt(1);
            }

            String sqlPre = "INSERT INTO Presentacion (stock, precio, tamano_ml, id_producto) VALUES (?, ?, 100, ?)";
            PreparedStatement stmtPre = conn.prepareStatement(sqlPre);
            stmtPre.setInt(1, stock);
            stmtPre.setDouble(2, precio);
            stmtPre.setInt(3, idGenerado);
            stmtPre.executeUpdate();

            conn.commit();
            mostrarAlerta("Producto agregado exitosamente.", Alert.AlertType.INFORMATION);
            limpiarFormulario(null);
            cargarProductos();

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            mostrarAlerta("Error al agregar: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @FXML
    void modificarProducto(ActionEvent event) {
        if (productoSeleccionado == null) {
            mostrarAlerta("Seleccione un producto para modificar.", Alert.AlertType.WARNING);
            return;
        }
        if (!validarCampos()) return;

        int idProv = Integer.parseInt(comboProveedor.getValue().split(" - ")[0]);

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            // 1. Actualizar Producto
            String sqlProd = "UPDATE Producto SET nombre=?, marca=?, id_proveedor=? WHERE id_producto=?";
            PreparedStatement stmtProd = conn.prepareStatement(sqlProd);
            stmtProd.setString(1, txtNombre.getText());
            stmtProd.setString(2, txtMarca.getText());
            stmtProd.setInt(3, idProv);
            stmtProd.setInt(4, productoSeleccionado.getIdProducto());
            stmtProd.executeUpdate();

            // 2. Actualizar Presentación
            // Nota: Si el producto no tenía presentación (id_presentacion = 0), aquí deberíamos hacer un INSERT,
            // pero por simplicidad asumimos UPDATE. Si falla, el usuario debería borrar y crear de nuevo.
            if (productoSeleccionado.getIdPresentacion() != 0) {
                String sqlPre = "UPDATE Presentacion SET stock=?, precio=? WHERE id_presentacion=?";
                PreparedStatement stmtPre = conn.prepareStatement(sqlPre);
                stmtPre.setInt(1, Integer.parseInt(txtStock.getText()));
                stmtPre.setDouble(2, Double.parseDouble(txtPrecio.getText()));
                stmtPre.setInt(3, productoSeleccionado.getIdPresentacion());
                stmtPre.executeUpdate();
            } else {
                // Lógica opcional: Insertar si no existía (Opcional para productos viejos corruptos)
                String sqlPre = "INSERT INTO Presentacion (stock, precio, tamano_ml, id_producto) VALUES (?, ?, 100, ?)";
                PreparedStatement stmtPre = conn.prepareStatement(sqlPre);
                stmtPre.setInt(1, Integer.parseInt(txtStock.getText()));
                stmtPre.setDouble(2, Double.parseDouble(txtPrecio.getText()));
                stmtPre.setInt(3, productoSeleccionado.getIdProducto());
                stmtPre.executeUpdate();
            }

            conn.commit();
            mostrarAlerta("Producto modificado correctamente.", Alert.AlertType.INFORMATION);
            limpiarFormulario(null);
            cargarProductos();

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            mostrarAlerta("Error al modificar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void eliminarProducto(ActionEvent event) {
        if (productoSeleccionado == null) {
            mostrarAlerta("Seleccione un producto para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        String sql = "DELETE FROM Producto WHERE id_producto = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productoSeleccionado.getIdProducto());
            stmt.executeUpdate();

            mostrarAlerta("Producto eliminado.", Alert.AlertType.INFORMATION);
            limpiarFormulario(null);
            cargarProductos();

        } catch (SQLException e) {
            mostrarAlerta("No se puede eliminar (probablemente tiene ventas asociadas).", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void seleccionarProducto(MouseEvent event) {
        productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            txtNombre.setText(productoSeleccionado.getNombre());
            txtMarca.setText(productoSeleccionado.getMarca());
            txtPrecio.setText(String.valueOf(productoSeleccionado.getPrecio()));
            txtStock.setText(String.valueOf(productoSeleccionado.getStock()));

            for (String item : comboProveedor.getItems()) {
                if (item.startsWith(productoSeleccionado.getIdProveedor() + " -")) {
                    comboProveedor.setValue(item);
                    break;
                }
            }
        }
    }

    @FXML
    void limpiarFormulario(ActionEvent event) {
        txtNombre.clear();
        txtMarca.clear();
        txtPrecio.clear();
        txtStock.clear();
        comboProveedor.setValue(null);
        productoSeleccionado = null;
        tablaProductos.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isEmpty() || txtPrecio.getText().isEmpty() ||
                txtStock.getText().isEmpty() || comboProveedor.getValue() == null) {
            mostrarAlerta("Por favor llene todos los campos.", Alert.AlertType.WARNING);
            return false;
        }
        try {
            Double.parseDouble(txtPrecio.getText());
            Integer.parseInt(txtStock.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("El precio y stock deben ser números válidos.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Gestión de Productos");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}