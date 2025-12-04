import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.List;

public class CatalogoClienteController {

    @FXML private GridPane gridProductos;


    public void initialize() {
        cargarProductosDinamicos();
    }

    private void cargarProductosDinamicos() {

        gridProductos.setHgap(20);
        gridProductos.setVgap(30);
        gridProductos.setAlignment(javafx.geometry.Pos.CENTER);
        gridProductos.getChildren().clear();


        ProductoDAO dao = new ProductoDAO();
        List<Producto> lista = dao.listarProductos();

        int columna = 0;
        int fila = 0;


        for (Producto p : lista) {
            VBox tarjeta = crearTarjetaGrafica(p);


            gridProductos.add(tarjeta, columna, fila);

            columna++;

            if (columna == 4) {
                columna = 0;
                fila++;
            }
        }
    }
        private VBox crearTarjetaGrafica (Producto p){

            VBox tarjeta = new VBox(5);
            tarjeta.setAlignment(javafx.geometry.Pos.CENTER);


            tarjeta.setPrefWidth(150);
            tarjeta.setMaxWidth(180);
            tarjeta.setPrefHeight(300);
            tarjeta.setStyle("-fx-border-color: #ddd; " +
                    "-fx-background-color: white; " +
                    "-fx-border-radius: 12; " +
                    "-fx-background-radius: 12; " +
                    "-fx-padding: 15; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 0);");


            ImageView img = new ImageView();
            img.setFitWidth(120);
            img.setFitHeight(120);
            img.setPreserveRatio(true);
            try {
                String ruta = "/imagenes/" + p.getImagen();
                img.setImage(new Image(getClass().getResourceAsStream(ruta)));
            } catch (Exception e) {
                System.out.println("No se encontró imagen para: " + p.getNombre());
            }


            Label lblNombre = new Label(p.getNombre());
            lblNombre.setWrapText(true);
            lblNombre.setMaxWidth(160);
            lblNombre.setAlignment(javafx.geometry.Pos.CENTER);
            lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #333333;");

            Label lblMarca = new Label(p.getMarca());
            lblMarca.setWrapText(true);
            lblMarca.setAlignment(javafx.geometry.Pos.CENTER);
            Label lblPrecio = new Label(String.format("$%.2f", p.getPrecio()));
            lblPrecio.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #007700;");


            Button btn = new Button("Comprar");
            btn.setPrefWidth(160);
            btn.setStyle("-fx-background-color: #4a6b5a; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");


            btn.setOnAction(event -> {

                agregarAlCarrito(p.getNombre(), p.getPrecio());
            });

            tarjeta.getChildren().addAll(img, lblNombre, lblMarca, lblPrecio, btn);
            return tarjeta;
        }

    private void agregarAlCarrito(String nombre, double precio) {
        ProductoCarrito nuevo = new ProductoCarrito(nombre, precio, 1);
        ServicioCarrito.agregarProducto(nuevo, Sesion.getIdCliente());
        System.out.println("Agregado al carrito: " + nombre);

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Éxito");
        alerta.setHeaderText(null);
        alerta.setContentText(nombre + " ha sido agregado al carrito.");
        Stage stage = (Stage) gridProductos.getScene().getWindow();
        alerta.initOwner(stage);
        alerta.show();

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
    }

