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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Optional;

public class CarritoController {
       @FXML private TableView<ProductoCarrito> tablaCarrito;
       @FXML private TableColumn<ProductoCarrito, String> colProducto;
       @FXML private TableColumn<ProductoCarrito, Double> colPrecio;
       @FXML private TableColumn<ProductoCarrito, Integer> colCantidad;
       @FXML private TableColumn<ProductoCarrito, Double> colTotal;

       @FXML private Label lblSubtotal;
       @FXML private Label lblEnvio;
       @FXML private Label lblTotalFinal;
       @FXML private Pane panelContenido;

       private final double ENVIO_FIJO = 150.00;
       private ObservableList<ProductoCarrito> misProductos = FXCollections.observableArrayList();

       @FXML
        public void initialize(){
           misProductos = ServicioCarrito.obtenerLista();
           tablaCarrito.setItems(misProductos);

           colProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
           colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
           colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
           colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));


       }
    @FXML
    void seguirComprando(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("catalogoCliente.fxml"));
            Parent vistaCatalogo = loader.load();

            Node boton = (Node) event.getSource();
            Scene ventanaPrincipal = boton.getScene();
            Pane panelPrincipal = (Pane) ventanaPrincipal.lookup("#panelContenido");

            if (panelPrincipal != null) {
                panelPrincipal.getChildren().clear();
                panelPrincipal.getChildren().add(vistaCatalogo);

                if (vistaCatalogo instanceof javafx.scene.layout.Region) {
                    javafx.scene.layout.Region region = (javafx.scene.layout.Region) vistaCatalogo;

                    // Amarramos ancho y alto
                    region.prefWidthProperty().bind(panelPrincipal.widthProperty());
                    region.prefHeightProperty().bind(panelPrincipal.heightProperty());
                }

            } else {
                System.out.println("Error: No encontré el panelContenido.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void calcularTotales() {
           double subtotal=0;

           for(ProductoCarrito p: misProductos){
               subtotal += p.getTotal();
           }

           double totalFinal = subtotal + ENVIO_FIJO;

        lblSubtotal.setText(String.format("$%.2f", subtotal));
        lblTotalFinal.setText(String.format("$%.2f", totalFinal));
    }

    @FXML
    public void btnEliminar(ActionEvent event){
           ProductoCarrito productoSeleccionado = tablaCarrito.getSelectionModel().getSelectedItem();

        // no se selecciono nada
        if (productoSeleccionado == null) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Aviso");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor selecciona un producto de la tabla.");

            //  centrar la alerta
            Stage stagePrincipal = (Stage) ((Node) event.getSource()).getScene().getWindow();
            alerta.initOwner(stagePrincipal);

            alerta.showAndWait();
            return;
        }

        // confirmacion
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar producto");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que quieres eliminar '" + productoSeleccionado.getNombre() + "'?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        // eliminar yei
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            String nombreProducto = productoSeleccionado.getNombre();
            misProductos.remove(productoSeleccionado);
            calcularTotales();

            Alert exito = new Alert(Alert.AlertType.INFORMATION);
            exito.setTitle("Éxito");
            exito.setHeaderText(null);
            exito.setContentText(nombreProducto + " ha sido eliminado del carrito.");

            exito.initOwner(((Stage) ((Node) event.getSource()).getScene().getWindow()));
            exito.show();
        }
    }

    @FXML
    public void btnEditarCantidad(){
           int fila = tablaCarrito.getSelectionModel().getSelectedIndex();
           if(fila>=0){
               tablaCarrito.edit(fila,colCantidad);
           }
    }
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
    void clicPagar(ActionEvent event) {
        // Validar que el carrito no esté vacío antes de pagar
        if (misProductos.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Carrito Vacío");
            alerta.setContentText("No puedes pagar si no tienes productos.");
            alerta.show();
            return;
        }

        try {
            // Cargar la vista de pago
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pago.fxml"));
            Parent root = loader.load();

            // Opción: Abrir ventana nueva
            Stage stage = new Stage();
            stage.setTitle("Pasarela de Pago - Casa de Jade");
            stage.setScene(new Scene(root));
            stage.show();

            // (Opcional) Si quieres cerrar el carrito al abrir pago, descomenta esto:
            // ((Node)(event.getSource())).getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setContentText("Error al abrir la ventana de pago: " + e.getMessage());
            error.show();
        }
    }

}
