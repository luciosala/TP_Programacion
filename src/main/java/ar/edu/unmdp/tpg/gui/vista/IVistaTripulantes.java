package ar.edu.unmdp.tpg.gui.vista;

import java.awt.event.ActionListener;
import java.util.List;

/**
 * Seccion de tripulantes: lista la tripulacion de la nave elegida con el salario de cada uno.
 */
public interface IVistaTripulantes {

    String MOSTRAR = "MOSTRAR_TRIPULANTES";

    void setActionListener(ActionListener listener);

    String getNaveSeleccionada();

    void mostrarNaves(List<String> ids);

    /**
     * Muestra la tabla de tripulantes. Cada fila es: id, nombre y apellido, cargo,
     * origen, antiguedad y salario del periodo.
     */
    void mostrarTripulantes(List<String[]> filas, String periodo);

    void mostrarError(String mensaje);
}
