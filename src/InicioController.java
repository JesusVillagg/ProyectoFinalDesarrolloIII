import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class InicioController {
    @FXML
    void irALoginCliente(ActionEvent event) {
        abrirLogin(event, "logincliente.fxml", "Acceso Cliente", false);    }

    @FXML
    void irALoginAdmin(ActionEvent event) {
        abrirLogin(event, "loginadmin.fxml", "Acceso Administrativo", true);    }

    private void abrirLogin(ActionEvent event, String fxmlFile, String titulo, boolean esAdmin) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // AQUÍ PASA LA MAGIA: Obtenemos el controlador y configuramos el modo
            LoginController controller = loader.getController();
            controller.setModoAdmin(esAdmin); // Le decimos: "¿Eres admin? Sí/No"

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Casa de Jade - " + titulo);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar: " + fxmlFile);
        }
    }
}
