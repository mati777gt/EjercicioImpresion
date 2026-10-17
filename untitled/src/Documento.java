public class Documento {

    // Los datos que tiene cada documento
    private String nombre;
    private double tamano;
    private String usuario;

    // Guarda los datos
    public Documento(String nombre, double tamano, String usuario) {
        this.nombre = nombre;
        this.tamano = tamano;
        this.usuario = usuario;
    }

    // Consultar nombre del documento
    public String getNombre() {
        return nombre;
    }

    // Datos a texto
    @Override
    public String toString() {
        return nombre + " | " + tamano + " MB | Usuario: " + usuario;
    }
}