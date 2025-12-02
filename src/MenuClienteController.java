import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.layout.Pane;

public class MenuClienteController {
    @FXML
    private Pane panelContenido;

    @FXML
    void irACatalogo(ActionEvent event) {
        cargarEnPanel("catalogoCliente.fxml");
    }

    @FXML
    void irAHistorial(ActionEvent event) {
        cargarEnPanel("historialCompras.fxml");
    }

    private void cargarEnPanel(String fxml) {
        try {
            // Cargar el archivo FXML hijo
            Parent vista = FXMLLoader.load(getClass().getResource(fxml));

            // Limpiar el panel y agregar la nueva vista
            panelContenido.getChildren().clear();
            panelContenido.getChildren().add(vista);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar vista incrustada: " + fxml);
        }
    }

    @FXML
    void regresarInicio(ActionEvent event) {
        cambiarVentana(event, "loginMain.fxml", "Bienvenido a Casa de Jade");
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
}
