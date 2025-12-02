public class Sesion {
    private static int idClienteActual = -1;

    public static void setIdCliente(int id){
        idClienteActual = id;
    }

    public static int getIdCliente(){
        return idClienteActual;
    }

}
