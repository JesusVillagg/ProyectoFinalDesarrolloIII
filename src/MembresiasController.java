import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MembresiasController {

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;

    @FXML private TextField txtNombre;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtEmail;

    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private Cliente clienteSeleccionado;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarClientes();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("correo"));
    }

    private void cargarClientes() {
        listaClientes.clear();
        String sql = "SELECT * FROM Cliente";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                listaClientes.add(new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("correo")
                ));
            }
            tablaClientes.setItems(listaClientes);

        } catch (SQLException e) {
            mostrarAlerta("Error al cargar clientes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void agregarCliente(ActionEvent event) {
        String nombre = txtNombre.getText();
        String direccion = txtDireccion.getText();
        String email = txtEmail.getText();

        if (nombre.isEmpty() || email.isEmpty()) {
            mostrarAlerta("El nombre y el email son obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        // Asignamos una contraseña por defecto ya que este formulario no pide password
        String sql = "INSERT INTO Cliente (nombre, direccion, correo, password) VALUES (?, ?, ?, '12345')";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setString(2, direccion);
            stmt.setString(3, email);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                mostrarAlerta("Cliente agregado correctamente.", Alert.AlertType.INFORMATION);
                limpiarFormulario(null);
                cargarClientes();
            }

        } catch (SQLException e) {
            mostrarAlerta("Error al agregar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void modificarCliente(ActionEvent event) {
        if (clienteSeleccionado == null) {
            mostrarAlerta("Selecciona un cliente de la tabla primero.", Alert.AlertType.WARNING);
            return;
        }

        String sql = "UPDATE Cliente SET nombre = ?, direccion = ?, correo = ? WHERE id_cliente = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, txtNombre.getText());
            stmt.setString(2, txtDireccion.getText());
            stmt.setString(3, txtEmail.getText());
            stmt.setInt(4, clienteSeleccionado.getIdCliente());

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                mostrarAlerta("Cliente modificado correctamente.", Alert.AlertType.INFORMATION);
                limpiarFormulario(null);
                cargarClientes();
            }

        } catch (SQLException e) {
            mostrarAlerta("Error al modificar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void eliminarCliente(ActionEvent event) {
        if (clienteSeleccionado == null) {
            mostrarAlerta("Selecciona un cliente de la tabla primero.", Alert.AlertType.WARNING);
            return;
        }

        String sql = "DELETE FROM Cliente WHERE id_cliente = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clienteSeleccionado.getIdCliente());

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                mostrarAlerta("Cliente eliminado.", Alert.AlertType.INFORMATION);
                limpiarFormulario(null);
                cargarClientes();
            }

        } catch (SQLException e) { // Seguramente fallará si el cliente tiene ventas por las FK
            mostrarAlerta("No se puede eliminar: El cliente tiene historial de compras.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void seleccionarCliente(MouseEvent event) {
        clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado != null) {
            txtNombre.setText(clienteSeleccionado.getNombre());
            txtDireccion.setText(clienteSeleccionado.getDireccion());
            txtEmail.setText(clienteSeleccionado.getCorreo());
        }
    }

    @FXML
    void limpiarFormulario(ActionEvent event) {
        txtNombre.clear();
        txtDireccion.clear();
        txtEmail.clear();
        clienteSeleccionado = null;
        tablaClientes.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Gestión de Clientes");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}