import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServicioCarrito {
    private static ObservableList<ProductoCarrito> productosEnCarrito = FXCollections.observableArrayList();

    public static void agregarProducto(ProductoCarrito producto) {
        productosEnCarrito.add(producto);
    }

    // recuperar la lista desde el Carrito
    public static ObservableList<ProductoCarrito> obtenerLista() {
        return productosEnCarrito;
    }

    // limpiar el carrito después de pagar
    public static void vaciarCarrito() {
        productosEnCarrito.clear();
    }
}

