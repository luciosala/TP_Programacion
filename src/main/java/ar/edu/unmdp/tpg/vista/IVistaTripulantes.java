package ar.edu.unmdp.tpg.vista;

import ar.edu.unmdp.tpg.haberes.Haber;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;

import java.awt.event.ActionListener;
import java.util.List;

/** Sección de Tripulantes: listado con la información detallada y el salario de cada uno. */
public interface IVistaTripulantes {

    String ACTUALIZAR = "ACTUALIZAR";
    String REGISTRAR_CONSEJO = "REGISTRAR_CONSEJO";

    void setActionListener(ActionListener controlador);

    /**
     * Muestra los tripulantes con su haber del mes.
     *
     * Precondición: las dos listas tienen el mismo tamaño y se corresponden por posición.
     */
    void mostrarTripulantes(List<Tripulante> tripulantes, List<Haber> haberes, String periodo);

    /** @return id del tripulante seleccionado en la tabla, o null si no hay selección */
    String getTripulanteSeleccionado();

    /** Pide al usuario la descripción del consejo. @return la descripción, o null si canceló */
    String pedirDescripcionConsejo();

    void mostrarError(String mensaje);
}
