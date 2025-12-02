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

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    protected void ingresar() {
        String correoInput = usuarioText.getText();
        String passwordInput = contrasenaText.getText();

        if (correoInput.isEmpty()) {
            mostrarAlerta("Por favor ingresa tu correo.", "Campo vacío", Alert.AlertType.WARNING);
            usuarioText.requestFocus();
            return;
        }
        if (passwordInput.isEmpty()) {
            mostrarAlerta("Por favor ingresa tu contraseña.", "Campo vacío", Alert.AlertType.WARNING);
            contrasenaText.requestFocus();
            return;
        }

        try {
            Connection conn = ConexionDB.getConnection();

            if (conn != null) {
                String sql = "SELECT * FROM Cliente WHERE correo = ? AND password = ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, correoInput);
                statement.setString(2, passwordInput);

                ResultSet resultado = statement.executeQuery();

                if (resultado.next()) {
                    abrirCatalogo();
                } else {
                    mostrarAlerta("Correo o contraseña incorrectos.", "Error de acceso", Alert.AlertType.ERROR);
                }

                resultado.close();
                statement.close();
                conn.close();
            } else {
                mostrarAlerta("No hay conexión con la base de datos.", "Error Crítico", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de SQL: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
        }
    }

    private void abrirCatalogo() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("catalogoCliente.fxml"));
            Scene mainScene = new Scene(fxmlLoader.load());

            Stage mainStage = new Stage();
            mainStage.setTitle("Casa de Jade - Catálogo");
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
