package ar.edu.unmdp.tpg.visor.vista;

import java.awt.event.ActionListener;

/**
 * Lo que el controlador del visor puede pedirle a la vista (MVC).
 * El controlador depende de esta interfaz y no de una ventana concreta.
 */
public interface IVistaVisor {

    String CREAR_NAVE = "CREAR_NAVE";
    String CREAR_GENERADORA = "CREAR_GENERADORA";

    /** Registra al controlador como oyente de los dos botones. */
    void setActionListener(ActionListener listener);

    /** Accion a ejecutar cuando el usuario cierra el visor (detener los hilos). */
    void alCerrar(Runnable accion);

    /** Muestra cuantas naves y generadoras hay en el universo paralelo. */
    void mostrarCantidades(int naves, int generadoras);

    void mostrar();
}
