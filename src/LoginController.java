import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usuarioText;
    @FXML
    private TextField contrasenaText;

    private Stage primaryStage;

    private boolean esModoAdmin = false;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setModoAdmin(boolean activo) {
        this.esModoAdmin = activo;
        System.out.println("Modo Admin activado: " + activo);
    }

    @FXML
    protected void ingresar() {
        String usuario = usuarioText.getText();
        String password= contrasenaText.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Por favor llena todos los campos.", "Campos vacíos", Alert.AlertType.WARNING);
            return;
        }

        try (Connection conn = ConexionDB.getConnection()) {
            if (conn != null) {
                String sql = "";
                String columnaId = "";

                if (esModoAdmin) {
                    sql = "SELECT id_administrador FROM Administrador WHERE usuario = ? AND password = ?";
                    columnaId = "id_administrador"; // Para el Administrador
                } else {
                    sql = "SELECT id_cliente FROM Cliente WHERE correo = ? AND password = ?";
                    columnaId = "id_cliente"; // Para el Cliente
                }

                try (PreparedStatement statement = conn.prepareStatement(sql)) {
                    statement.setString(1, usuario);
                    statement.setString(2, password);

                    ResultSet resultado = statement.executeQuery();

                    if (resultado.next()) {
                        int idEncontrado = resultado.getInt(columnaId);
                        Sesion.setIdCliente(idEncontrado);

                        if (esModoAdmin) {
                            abrirMenuAdmin();
                        } else {
                            abrirMenuCliente();
                            Sesion.setIdCliente(idEncontrado);
                            ServicioCarrito.cargarCarritoUsuario(idEncontrado);
                        }
                    } else {
                        mostrarAlerta("Usuario/Correo o contraseña incorrectos.", "Error de acceso", Alert.AlertType.ERROR);
                    }
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de SQL: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void salir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("loginMain.fxml"));
            javafx.scene.Parent root = loader.load();

            Stage stage = (Stage) usuarioText.getScene().getWindow();

            stage.setTitle("Bienvenido a Casa de Jade");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al intentar regresar al inicio.");
        }
    }

    private void abrirMenuCliente() {
        try {
            if (primaryStage == null) {
                primaryStage = (Stage) usuarioText.getScene().getWindow();
            }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menucliente.fxml"));
            Scene mainScene = new Scene(fxmlLoader.load());
            Stage mainStage = new Stage();
            mainStage.setTitle("Casa de Jade - Menú");
            mainStage.setScene(mainScene);

            mainStage.setOnCloseRequest((WindowEvent event) -> {
                primaryStage.show();
            });

            mainStage.initOwner(primaryStage);
            mainStage.initModality(Modality.WINDOW_MODAL);

            primaryStage.hide();
            mainStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("No se pudo abrir el catálogo: " + e.getMessage(), "Error de Interfaz", Alert.AlertType.ERROR);
        }
    }

    private void abrirMenuAdmin() {
        abrirVentana("menuadmin.fxml", "Casa de Jade - Panel de Administración");
    }

    private void abrirVentana(String fxml, String titulo) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
            Scene newScene = new Scene(fxmlLoader.load());
            Stage newStage = new Stage();
            newStage.setTitle(titulo);
            newStage.setScene(newScene);
            newStage.initModality(Modality.WINDOW_MODAL);

            Stage currentStage = (Stage) usuarioText.getScene().getWindow();
            currentStage.close();

            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("No se pudo abrir la siguiente ventana: " + fxml, "Error", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        if (primaryStage != null) {
            alerta.initOwner(primaryStage);
        }
        alerta.showAndWait();
    }

}
