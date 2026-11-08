package ar.edu.unmdp.tpg.visor.controlador;

import ar.edu.unmdp.tpg.visor.modelo.UniversoParalelo;
import ar.edu.unmdp.tpg.visor.vista.IVistaVisor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador del visor (MVC): atiende los botones de la vista y le pide al modelo
 * que cree naves o generadoras. Al cerrar la vista, detiene todos los hilos.
 *
 * Las actualizaciones automaticas (los eventos de la simulacion) no pasan por aca:
 * llegan del modelo a la vista directamente por Observer.
 */
public class ControladorVisor implements ActionListener {

    private final IVistaVisor vista;
    private final UniversoParalelo universo;

    public ControladorVisor(IVistaVisor vista, UniversoParalelo universo) {
        this.vista = vista;
        this.universo = universo;
        vista.setActionListener(this);
        vista.alCerrar(universo::detenerTodo);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (IVistaVisor.CREAR_NAVE.equals(e.getActionCommand())) {
            universo.crearNave();
        } else if (IVistaVisor.CREAR_GENERADORA.equals(e.getActionCommand())) {
            universo.crearGeneradora();
        }
        vista.mostrarCantidades(universo.getCantidadNaves(), universo.getCantidadGeneradoras());
    }
}
