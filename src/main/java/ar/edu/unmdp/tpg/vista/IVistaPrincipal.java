package ar.edu.unmdp.tpg.vista;

/** Ventana principal, que se abre cuando el operador inicia sesión. */
public interface IVistaPrincipal {

    /**
     * Muestra la ventana principal.
     *
     * @param usuario nombre del operador que inició sesión
     */
    void mostrar(String usuario);
}
