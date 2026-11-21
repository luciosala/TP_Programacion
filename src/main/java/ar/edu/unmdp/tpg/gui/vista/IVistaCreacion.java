package ar.edu.unmdp.tpg.gui.vista;

import java.awt.event.ActionListener;
import java.util.List;

/**
 * Seccion de creacion: crear naves (por tipo) y crear tripulantes para asignarlos.
 * La vista solo muestra y lee datos; el controlador habla con el modelo.
 */
public interface IVistaCreacion {

    String CREAR_NAVE = "CREAR_NAVE";
    String CREAR_TRIPULANTE = "CREAR_TRIPULANTE";

    void setActionListener(ActionListener listener);

    // --- Datos de la nave a crear ---
    String getIdNave();
    String getTipoNave();

    // --- Datos del tripulante a crear ---
    String getIdTripulante();
    String getNombre();
    String getApellido();
    String getCargo();
    String getOrigen();

    /**
     * Antiguedad en anios. La vista valida el formato (que sea un numero entero
     * no negativo); si no lo es, avisa y devuelve null.
     */
    Integer getAntiguedad();

    /** Nave a la que se asigna el tripulante (id elegido en el combo), o null si no hay. */
    String getNaveSeleccionada();

    /** Actualiza el combo de naves disponibles. */
    void mostrarNaves(List<String> ids);

    void mostrarError(String mensaje);
    void mostrarAviso(String mensaje);

    /** Limpia los campos del tripulante despues de crearlo. */
    void limpiarTripulante();
}
