public class Producto {
    private int idProducto;
    private String nombre;
    private String marca;
    private double precio;
    private String imagen;

    public Producto(int idProducto, String nombre, String marca, double precio, String imagen) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.marca = marca;
        this.precio = precio;
        this.imagen = imagen;
    }


    public String getNombre() { return nombre; }
    public String getMarca() { return marca; }
    public double getPrecio() { return precio; }
    public String getImagen() { return imagen; }
}
