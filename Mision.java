abstract Mision {
    protected String nombre;
    protected String descripcion;

    public Mision(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public abstract void ejecutarMision();
}