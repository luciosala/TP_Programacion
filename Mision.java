    abstract public  Class Mision {
    protected String nombre;
    protected String descripcion;

    public Mision(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /*metodos protected para que los hijos puedan accedrr */
  
    protected abstract void preparar() 

    protected abstract void ejecutar() 

   
    protected abstract boolean evaluarResultado();

   
    protected abstract InformeMision cerrar(boolean exito);

}