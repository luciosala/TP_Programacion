package ar.edu.unmdp.tpg.gui.controlador;

import ar.edu.unmdp.tpg.acceso.AccesoException;
import ar.edu.unmdp.tpg.acceso.ServicioDeAcceso;
import ar.edu.unmdp.tpg.acceso.Sesion;
import ar.edu.unmdp.tpg.gui.vista.IVistaAcceso;
import ar.edu.unmdp.tpg.persistencia.PersistenciaException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador del acceso (MVC): atiende los botones de la pantalla de login y
 * le pide al ServicioDeAcceso que registre o valide al usuario.
 *
 * Cuando el login sale bien, avisa a quien lo creo (el main) con el objeto Runnable
 * recibido, que es el que abre la ventana principal. Asi este controlador no conoce
 * a la ventana principal.
 */
public class ControladorAcceso implements ActionListener {

    private final IVistaAcceso vista;
    private final ServicioDeAcceso servicio;
    private final Runnable alIngresar;
    private Sesion sesion;

    public ControladorAcceso(IVistaAcceso vista, ServicioDeAcceso servicio, Runnable alIngresar) {
        this.vista = vista;
        this.servicio = servicio;
        this.alIngresar = alIngresar;
        vista.setActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String usuario = vista.getUsuario();
        String contrasena = vista.getContrasena();
        // Validacion de formato: es responsabilidad de la vista/controlador, no del modelo
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            vista.mostrarError("Complete el usuario y la contraseña");
            return;
        }
        try {
            if (IVistaAcceso.REGISTRAR.equals(e.getActionCommand())) {
                servicio.registrar(usuario, contrasena);
                vista.mostrarAviso("Usuario " + usuario + " registrado. Ahora puede ingresar.");
            } else if (IVistaAcceso.INGRESAR.equals(e.getActionCommand())) {
                sesion = servicio.iniciarSesion(usuario, contrasena);
                vista.cerrar();
                alIngresar.run();
            }
        } catch (AccesoException error) {
            vista.mostrarError(error.getMessage());          // credenciales invalidas, usuario existente
        } catch (PersistenciaException error) {
            vista.mostrarError("Problema con la base de datos: " + error.getMessage());
        }
    }

    public Sesion getSesion() {
        return sesion;
    }
}
