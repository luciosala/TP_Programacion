package ar.edu.unmdp.tpg.controlador;

import ar.edu.unmdp.tpg.aplicacion.ModeloAplicacion;
import ar.edu.unmdp.tpg.excepciones.TripulacionInvalidaException;
import ar.edu.unmdp.tpg.haberes.Haber;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;
import ar.edu.unmdp.tpg.vista.IVistaTripulantes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la sección de Tripulantes (MVC): liquida el haber de cada
 * tripulante de la nave activa y se lo pasa a la vista; registra consejos.
 */
public class ControladorTripulantes implements ActionListener {

    private final IVistaTripulantes vista;
    private final ModeloAplicacion modelo;

    public ControladorTripulantes(IVistaTripulantes vista, ModeloAplicacion modelo) {
        this.vista = vista;
        this.modelo = modelo;
        vista.setActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        if (evento.getActionCommand().equals(IVistaTripulantes.ACTUALIZAR)) {
            actualizarVista();
        } else if (evento.getActionCommand().equals(IVistaTripulantes.REGISTRAR_CONSEJO)) {
            registrarConsejo();
        }
    }

    /** Liquida los haberes del mes de la tripulación de la nave activa y los muestra. */
    public void actualizarVista() {
        if (!modelo.hayNaveActiva()) {
            return;
        }
        List<Tripulante> tripulantes = modelo.getNaveActiva().consultarTripulacion();
        List<Haber> haberes = new ArrayList<>();
        for (Tripulante tripulante : tripulantes) {
            haberes.add(modelo.liquidarHaber(tripulante));
        }
        vista.mostrarTripulantes(tripulantes, haberes, YearMonth.now().toString());
    }

    private void registrarConsejo() {
        String id = vista.getTripulanteSeleccionado();
        if (id == null) {
            vista.mostrarError("Seleccione un consejero en la tabla.");
            return;
        }
        String descripcion = vista.pedirDescripcionConsejo();
        if (descripcion == null || descripcion.isBlank()) {
            return;   // el usuario canceló
        }
        try {
            modelo.registrarConsejo(id, descripcion.trim());
            actualizarVista();
        } catch (TripulacionInvalidaException error) {
            vista.mostrarError(error.getMessage());
        }
    }
}
