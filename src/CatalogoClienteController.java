import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class CatalogoClienteController {
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
    @FXML
    void clicPerfume1(ActionEvent event) {
        agregarAlCarrito("Perfume1 ", 1200.00);
    }

    @FXML
    void clicPerfume2(ActionEvent event) {
        agregarAlCarrito("Perfume2", 350.50);
    }

    @FXML
    void clicPerfume3(ActionEvent event) {
        agregarAlCarrito("Perfume3", 800.00);
    }
    private void agregarAlCarrito(String nombre, double precio) {

        ProductoCarrito nuevo = new ProductoCarrito(nombre, precio, 1);

        ServicioCarrito.agregarProducto(nuevo);

        System.out.println("Agregado: " + nombre);
    }

}

