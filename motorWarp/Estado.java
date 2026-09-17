public abstract class Estado {

    abstract Estado prepararSalto();

    abstract Estado iniciarSalto();

    abstract Estado finalizarSalto();

    abstract Estado completarEnfriamiento();

    abstract String nombre();
}