public class ProductoTabla {
    private int idProducto;
    private String nombre;
    private String marca;
    private double precio;
    private int stock;
    private int idProveedor;
    private int idPresentacion;

    public ProductoTabla(int idProducto, String nombre, String marca, double precio, int stock, int idProveedor, int idPresentacion) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.marca = marca;
        this.precio = precio;
        this.stock = stock;
        this.idProveedor = idProveedor;
        this.idPresentacion = idPresentacion;
    }

    public int getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public String getMarca() { return marca; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public int getIdProveedor() { return idProveedor; }
    public int getIdPresentacion() { return idPresentacion; }


    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setStock(int stock) { this.stock = stock; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }
}