package ar.edu.unmdp.tpg.controlador;

import ar.edu.unmdp.tpg.acceso.CredencialesInvalidasException;
import ar.edu.unmdp.tpg.acceso.ServicioDeAcceso;
import ar.edu.unmdp.tpg.acceso.Sesion;
import ar.edu.unmdp.tpg.acceso.UsuarioExistenteException;
import ar.edu.unmdp.tpg.persistencia.PersistenciaException;
import ar.edu.unmdp.tpg.vista.IVistaAcceso;
import ar.edu.unmdp.tpg.vista.IVistaPrincipal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador del acceso (MVC, Clase 13). Recibe los botones de la vista, le pide
 * la operación al servicio y coordina la respuesta con la vista.
 *
 * Reparto de responsabilidades del login (Guía del TPG): formato en la vista,
 * coordinación en el controlador, reglas y excepciones en el servicio
 * (ServicioDeAcceso) y consultas en el DAO (UsuarioDAO).
 */
public class ControladorAcceso implements ActionListener {

    private final IVistaAcceso vista;
    private final IVistaPrincipal principal;
    private final ServicioDeAcceso servicio;

    public ControladorAcceso(IVistaAcceso vista, IVistaPrincipal principal, ServicioDeAcceso servicio) {
        this.vista = vista;
        this.principal = principal;
        this.servicio = servicio;
        vista.setActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        if (evento.getActionCommand().equals(IVistaAcceso.INGRESAR)) {
            ingresar();
        } else if (evento.getActionCommand().equals(IVistaAcceso.REGISTRAR)) {
            registrar();
        }
    }

    private void ingresar() {
        try {
            Sesion sesion = servicio.iniciarSesion(vista.getUsuarioLogin(), vista.getContrasenaLogin());
            vista.cerrar();
            principal.mostrar(sesion.getNombreUsuario());   // el controlador selecciona la vista
        } catch (CredencialesInvalidasException error) {
            vista.mostrarError(error.getMessage());
        } catch (PersistenciaException error) {
            vista.mostrarError("No se pudo acceder a la base de datos: " + error.getMessage());
        }
    }

    private void registrar() {
        String errorDeFormato = vista.validarFormatoRegistro();
        if (errorDeFormato != null) {
            vista.mostrarError(errorDeFormato);
            return;
        }
        try {
            servicio.registrar(vista.getUsuarioRegistro(), vista.getContrasenaRegistro());
            vista.limpiarRegistro();
            vista.mostrarMensaje("Usuario " + vista.getUsuarioRegistro() + " registrado. Ya puede iniciar sesión.");
        } catch (UsuarioExistenteException error) {
            vista.mostrarError(error.getMessage());
        } catch (PersistenciaException error) {
            vista.mostrarError("No se pudo acceder a la base de datos: " + error.getMessage());
        }
    }
}
