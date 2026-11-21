package ar.edu.unmdp.tpg.gui.vista;

import java.awt.event.ActionListener;

/**
 * Lo que el controlador de acceso puede pedirle a la pantalla de login y registro (MVC).
 * El controlador depende de esta interfaz y no de la ventana concreta.
 */
public interface IVistaAcceso {

    String INGRESAR = "INGRESAR";
    String REGISTRAR = "REGISTRAR";

    /** Registra al controlador como oyente de los botones. */
    void setActionListener(ActionListener listener);

    String getUsuario();

    String getContrasena();

    /** Muestra un mensaje de error (credenciales invalidas, usuario existente, etc.). */
    void mostrarError(String mensaje);

    /** Muestra un aviso (por ejemplo, "usuario registrado"). */
    void mostrarAviso(String mensaje);

    void mostrar();

    /** Cierra la pantalla de acceso (cuando el login salio bien). */
    void cerrar();
}
