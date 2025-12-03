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

    @FXML
    void irACarrito(ActionEvent event) {
        cargarEnPanel("carrito.fxml");
    }

    private void cargarEnPanel(String fxml) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent vista = loader.load();

            panelContenido.getChildren().clear();
            panelContenido.getChildren().add(vista);

            if (vista instanceof javafx.scene.layout.Region) {
                javafx.scene.layout.Region regionVista = (javafx.scene.layout.Region) vista;

                regionVista.prefWidthProperty().bind(panelContenido.widthProperty());
                regionVista.prefHeightProperty().bind(panelContenido.heightProperty());
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar vista: " + fxml);
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
