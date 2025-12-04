public class Cliente {
    private int idCliente;
    private String nombre;
    private String direccion;
    private String correo;

    public Cliente(int idCliente, String nombre, String direccion, String correo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.direccion = direccion;
        this.correo = correo;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}