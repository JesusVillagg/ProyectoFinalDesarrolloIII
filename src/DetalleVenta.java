public class DetalleVenta {
    private int idDetalle;
    private int cantidad;
    private double precioUnitario;
    private int idProducto;

    public DetalleVenta(int idDetalle, int cantidad, double precioUnitario, int idProducto) {
        this.idDetalle = idDetalle;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.idProducto = idProducto;
    }

    public double calcularSubtotal() {
        return cantidad * precioUnitario;
    }

    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
}
