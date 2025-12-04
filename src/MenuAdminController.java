import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import java.io.IOException;

public class MenuAdminController {

    @FXML
    private Pane panelContenido;

    @FXML
    public void initialize() {
        irAInicio(null);
    }

    @FXML
    void irAInicio(ActionEvent event) {
        cargarEnPanel("dashboard.fxml");
    }

    @FXML
    void irACatalogoAdmin(ActionEvent event) {
        cargarEnPanel("catalogoAdmin.fxml");
    }

    @FXML
    void irAMembresias(ActionEvent event) {
        cargarEnPanel("membresias.fxml");
    }

    @FXML
    void irAGestionProveedores(ActionEvent event) {
        cargarEnPanel("proveedores.fxml");
    }

    private void cargarEnPanel(String fxml) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource(fxml));
            panelContenido.getChildren().clear();
            panelContenido.getChildren().add(vista);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar vista incrustada: " + fxml);
        }
    }

    @FXML
    void regresarInicio(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("loginMain.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Bienvenido a Casa de Jade");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}