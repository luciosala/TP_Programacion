package ar.edu.unmdp.tpg.vista;

import java.awt.event.ActionListener;

/**
 * Vista de acceso al sistema: Login y Registro (MVC, Clase 13).
 * El controlador trabaja con esta interfaz y no con la ventana concreta.
 */
public interface IVistaAcceso {

    String INGRESAR = "INGRESAR";
    String REGISTRAR = "REGISTRAR";

    /** Vincula los botones con el controlador. */
    void setActionListener(ActionListener controlador);

    String getUsuarioLogin();
    String getContrasenaLogin();
    String getUsuarioRegistro();
    String getContrasenaRegistro();

    /**
     * Validación de formato del registro, propia de la vista.
     *
     * @return el motivo por el que los datos no tienen un formato válido, o null si son válidos
     */
    String validarFormatoRegistro();

    void limpiarRegistro();
    void mostrarMensaje(String mensaje);
    void mostrarError(String mensaje);
    void mostrar();
    void cerrar();
}
