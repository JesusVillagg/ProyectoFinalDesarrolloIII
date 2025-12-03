
public class ProductoCarrito {
    private String nombre;
    private double precio;
    private int cantidad;

    public ProductoCarrito(String nombre, double precio, int cantidad) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre=nombre;
    }
    public Double getPrecio(){
        return precio;
    }
    public void setPrecio(Double precio){
        this.precio=precio;
    }
    public int getCantidad(){
        return cantidad;
    }
    public void setCantidad(int cantidad){
        this.cantidad=cantidad;
    }

    public double getTotal() {
        return precio * cantidad;
    }


}
