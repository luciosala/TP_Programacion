package ar.edu.unmdp.tpg.vista;

import ar.edu.unmdp.tpg.tripulacion.Origen;

import java.awt.event.ActionListener;
import java.util.List;

/** Sección de Creación: elegir el tipo de nave e instanciarla, y crear tripulantes y asignarlos. */
public interface IVistaCreacion {

    String CREAR_NAVE = "CREAR_NAVE";
    String SELECCIONAR_NAVE = "SELECCIONAR_NAVE";
    String AGREGAR_TRIPULANTE = "AGREGAR_TRIPULANTE";

    void setActionListener(ActionListener controlador);

    String getIdNave();
    String getTipoNave();
    String getNaveElegida();

    String getIdTripulante();
    String getNombreTripulante();
    String getApellidoTripulante();
    String getCargo();
    Origen getOrigen();

    /** Precondición: validarFormatoTripulante() devolvió null. @return antigüedad en años */
    int getAntiguedad();

    /** @return el motivo por el que el formato de la nave no es válido, o null si es válido */
    String validarFormatoNave();

    /** @return el motivo por el que el formato del tripulante no es válido, o null si es válido */
    String validarFormatoTripulante();

    /** Muestra las naves registradas y cuál es la activa. */
    void mostrarNaves(List<String> ids, String activa);

    /** Muestra un resumen de la nave activa. */
    void mostrarResumen(String resumen);

    void limpiarTripulante();

    /** Habilita o no las acciones (durante una misión no se crean ni cambian naves). */
    void habilitar(boolean habilitado);

    void mostrarError(String mensaje);
}
