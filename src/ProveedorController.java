import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class ProveedorController {

    @FXML private javafx.scene.control.TextField nombreText;
    @FXML private javafx.scene.control.TextField direccionText;
    @FXML private javafx.scene.control.TextField correoText;
    @FXML private javafx.scene.control.TextField telefonoText;

    @FXML private TableView<Proveedor> tablaProveedores;

    @FXML private TableColumn<Proveedor, Integer> colId;
    @FXML private TableColumn<Proveedor, String> colNombre;
    @FXML private TableColumn<Proveedor, String> colDireccion;
    @FXML private TableColumn<Proveedor, String> colCorreo;
    @FXML private TableColumn<Proveedor, String> colTelefono;

    private ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarProveedores();

        tablaProveedores.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        nombreText.setText(newSelection.getNombre());
                        direccionText.setText(newSelection.getDireccion());
                        correoText.setText(newSelection.getCorreo());
                        telefonoText.setText(newSelection.getTelefono());
                    } else {
                        limpiarCampos();
                    }
                }
        );
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
    }

    private void cargarProveedores() {
        listaProveedores.clear();
        String sql = "SELECT * FROM Proveedor";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                listaProveedores.add(new Proveedor(
                        rs.getInt("id_proveedor"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("correo"),
                        rs.getString("telefono")

                ));
            }
            tablaProveedores.setItems(listaProveedores);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar proveedores: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void agregarProveedor() {
        if (nombreText.getText().trim().isEmpty() ||
                direccionText.getText().trim().isEmpty() ||
                correoText.getText().trim().isEmpty() ||
                telefonoText.getText().trim().isEmpty()) {

            mostrarAlerta("Todos los campos (Nombre, Dirección, Correo, Teléfono) son obligatorios.", Alert.AlertType.WARNING);
            return;
        }
        String sql = "INSERT INTO Proveedor (nombre, direccion, correo, telefono) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, nombreText.getText());
            pst.setString(2, direccionText.getText());
            pst.setString(3, correoText.getText());
            pst.setString(4, telefonoText.getText());

            pst.executeUpdate();
            mostrarAlerta("Proveedor agregado exitosamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarProveedores();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al agregar proveedor: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void modificarProveedor() {
        Proveedor seleccionado = tablaProveedores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Por favor selecciona un proveedor para modificar.", Alert.AlertType.WARNING);
            return;
        }
        String sql = "UPDATE Proveedor SET nombre = ?, direccion = ?, correo = ?, telefono = ? WHERE id_proveedor = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, nombreText.getText());
            pst.setString(2, direccionText.getText());
            pst.setString(3, correoText.getText());
            pst.setString(4, telefonoText.getText());
            pst.setInt(5, seleccionado.getIdProveedor());

            pst.executeUpdate();
            mostrarAlerta("Proveedor modificado exitosamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarProveedores();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al modificar proveedor: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarProveedor() {
        Proveedor seleccionado = tablaProveedores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Por favor selecciona un proveedor para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        String sql = "DELETE FROM Proveedor WHERE id_proveedor = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, seleccionado.getIdProveedor());
            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                mostrarAlerta("Proveedor eliminado exitosamente.", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarProveedores();
            } else {
                mostrarAlerta("No se pudo eliminar el proveedor.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                mostrarAlerta("No se puede eliminar: El proveedor tiene productos asociados.", Alert.AlertType.ERROR);
            } else {
                e.printStackTrace();
                mostrarAlerta("Error al eliminar proveedor: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void limpiarCampos() {
        nombreText.clear();
        direccionText.clear();
        correoText.clear();
        telefonoText.clear();
        tablaProveedores.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Gestión de Proveedores");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    @FXML
    void recargarInicio(ActionEvent event) {
        try {
            //cargar el archivo principal
            Parent root = FXMLLoader.load(getClass().getResource("/menuadmin.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}