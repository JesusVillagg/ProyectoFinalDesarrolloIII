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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;

public class CarritoController {
       @FXML private TableView<ProductoCarrito> tablaCarrito;
       @FXML private TableColumn<ProductoCarrito, String> colProducto;
       @FXML private TableColumn<ProductoCarrito, Double> colPrecio;
       @FXML private TableColumn<ProductoCarrito, Integer> colCantidad;
       @FXML private TableColumn<ProductoCarrito, Double> colTotal;

       @FXML private Label lblSubtotal;
       @FXML private Label lblEnvio;
       @FXML private Label lblTotalFinal;

       private final double ENVIO_FIJO = 150.00;
       private ObservableList<ProductoCarrito> misProductos = FXCollections.observableArrayList();

       @FXML
        public void initialize(){
           misProductos = ServicioCarrito.obtenerLista();
           tablaCarrito.setItems(misProductos);

           colProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
           colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
           colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
           colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

           tablaCarrito.setEditable(true);
           colCantidad.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

           colCantidad.setOnEditCommit(event ->{
               //obtener producto
               ProductoCarrito producto = event.getRowValue();
               if (event.getNewValue() > 0) {
                   producto.setCantidad(event.getNewValue());
                   calcularTotales(); //
                   tablaCarrito.refresh(); //
               } else {
                   tablaCarrito.refresh();
               }
           });
           lblEnvio.setText(String.format("$%.2f", ENVIO_FIJO));
           calcularTotales();

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
    public void btnEliminar(){
           ProductoCarrito productoSeleccionado = tablaCarrito.getSelectionModel().getSelectedItem();

           if(productoSeleccionado != null){
               misProductos.remove(productoSeleccionado);
               calcularTotales();
           }else{
               Alert alerta = new Alert(Alert.AlertType.WARNING);
               alerta.setTitle("Cuidado");
               alerta.setContentText("Selecciona un producto");
               alerta.show();
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
