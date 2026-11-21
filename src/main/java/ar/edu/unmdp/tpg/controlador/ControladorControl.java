package ar.edu.unmdp.tpg.controlador;

import ar.edu.unmdp.tpg.aplicacion.ModeloAplicacion;
import ar.edu.unmdp.tpg.excepciones.LimiteRecursoExcedidoException;
import ar.edu.unmdp.tpg.excepciones.MisionNoViableException;
import ar.edu.unmdp.tpg.vista.IVistaControl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador de la sección de Control de la Nave (MVC, flujo del Enunciado):
 * el ActionListener captura el botón, el controlador invoca la operación en el
 * modelo y, si el modelo la rechaza, coordina la respuesta con la vista. Los
 * cambios del modelo llegan a la vista por Observer, no por el controlador.
 */
public class ControladorControl implements ActionListener {

    private final IVistaControl vista;
    private final ModeloAplicacion modelo;

    public ControladorControl(IVistaControl vista, ModeloAplicacion modelo) {
        this.vista = vista;
        this.modelo = modelo;
        vista.setActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        if (!modelo.hayNaveActiva()) {
            vista.mostrarError("Primero cree una nave en la sección de Creación.");
            return;
        }
        String comando = evento.getActionCommand();
        try {
            if (comando.equals(IVistaControl.INICIAR_M01)) {
                modelo.iniciarIntercepcion();
            } else if (comando.equals(IVistaControl.INICIAR_M02)) {
                modelo.iniciarRecoleccion();
            } else if (comando.equals(IVistaControl.INICIAR_M03)) {
                modelo.iniciarRetorno();
            } else if (!modelo.naveEnLaBase()) {
                vista.mostrarError("La nave tiene que estar en la base de origen para cargar recursos "
                    + "o hacer mantenimiento. Para volver, iniciar M-03.");
            } else if (comando.equals(IVistaControl.MANTENIMIENTO)) {
                modelo.realizarMantenimiento();
            } else {
                cargar(comando);
            }
        } catch (MisionNoViableException error) {
            vista.mostrarError(error.getMessage());
        } catch (LimiteRecursoExcedidoException error) {
            vista.mostrarError(error.getMessage());
        }
    }

    private void cargar(String comando) throws LimiteRecursoExcedidoException {
        String errorDeFormato = vista.validarFormatoCantidad();
        if (errorDeFormato != null) {
            vista.mostrarError(errorDeFormato);
            return;
        }
        if (comando.equals(IVistaControl.CARGAR_COMBUSTIBLE)) {
            modelo.cargarCombustible(vista.getCantidad());
        } else if (comando.equals(IVistaControl.CARGAR_ENERGIA)) {
            modelo.cargarEnergia(vista.getCantidad());
        }
    }
}
