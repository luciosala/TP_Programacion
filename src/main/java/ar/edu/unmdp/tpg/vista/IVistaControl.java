package ar.edu.unmdp.tpg.vista;

import java.awt.event.ActionListener;

/**
 * Sección de Control de la Nave: estado de los recursos, bitácora, historial de
 * informes, controles para iniciar misiones y el tablero en tiempo real.
 */
public interface IVistaControl {

    String INICIAR_M01 = "INICIAR_M01";
    String INICIAR_M02 = "INICIAR_M02";
    String INICIAR_M03 = "INICIAR_M03";
    String CARGAR_COMBUSTIBLE = "CARGAR_COMBUSTIBLE";
    String CARGAR_ENERGIA = "CARGAR_ENERGIA";
    String MANTENIMIENTO = "MANTENIMIENTO";

    void setActionListener(ActionListener controlador);

    /** @return el motivo por el que la cantidad a cargar no tiene un formato válido, o null si es válida */
    String validarFormatoCantidad();

    /** Precondición: validarFormatoCantidad() devolvió null. @return cantidad a cargar */
    int getCantidad();

    void mostrarError(String mensaje);
}
