import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServicioCarrito {
    private static ObservableList<ProductoCarrito> productosEnCarrito = FXCollections.observableArrayList();

    public static void agregarProducto(ProductoCarrito producto) {
        for (ProductoCarrito p : productosEnCarrito) {
            if (p.getNombre().equals(producto.getNombre())) {
                p.setCantidad(p.getCantidad() + producto.getCantidad());
                return;
            }
        }
        productosEnCarrito.add(producto);    }

    // recuperar la lista desde el Carrito
    public static ObservableList<ProductoCarrito> obtenerLista() {
        return productosEnCarrito;
    }

    // limpiar el carrito después de pagar
    public static void vaciarCarrito() {
        productosEnCarrito.clear();
    }

    public static double calcularTotal(){
        double total = 0;
        for (ProductoCarrito p : productosEnCarrito) {
            total += p.getTotal();
        }
        return total;
    }
}

