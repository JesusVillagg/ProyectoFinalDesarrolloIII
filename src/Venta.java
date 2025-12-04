import java.util.ArrayList;
import java.util.List;

public class Venta {
    private int idVenta;
    private String fecha;
    private double total;
    private String metodoPago;
    private String direccionEntrega;
    private int idCliente;
    private String productosResumen;

    public Venta(int idVenta, String fecha, double total, String metodoPago, String direccionEntrega, int idCliente,String productosResumen) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.total = total;
        this.metodoPago = metodoPago;
        this.direccionEntrega = direccionEntrega;
        this.idCliente = idCliente;
        this.productosResumen = productosResumen;
    }

    public int getIdVenta() { return idVenta; }
    public String getFecha() { return fecha; }
    public double getTotal() { return total; }
    public String getMetodoPago() { return metodoPago; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public int getIdCliente() { return idCliente; }
    public String getProductosResumen() {
        return productosResumen;
    }

    public void setProductosResumen(String productosResumen) {
        this.productosResumen = productosResumen;
    }
}