    abstract public class Mision {
    protected String nombre;
    protected String descripcion;

    public Mision(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    
    //Ejecuta el ciclo común sin permitir que las subclases alteren su orden.
     
    public final InformeMision realizarMision() {
        preparar();
        ejecutar();
        boolean exito = evaluarResultado();
        return cerrar(exito);
    }

    /*metodos protected para que los hijos puedan accedrr */
  
    protected abstract void preparar();

    protected abstract void ejecutar();

   
    protected abstract boolean evaluarResultado();

   
    protected abstract InformeMision cerrar(boolean exito);

}
