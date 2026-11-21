package ar.edu.unmdp.tpg.gui.controlador;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.asistente.AsistenteDeComandoBasico;
import ar.edu.unmdp.tpg.bitacora.Bitacora;
import ar.edu.unmdp.tpg.excepciones.NaveException;
import ar.edu.unmdp.tpg.gui.vista.IVistaCreacion;
import ar.edu.unmdp.tpg.naves.NaveFactory;
import ar.edu.unmdp.tpg.tripulacion.Alferez;
import ar.edu.unmdp.tpg.tripulacion.Capitan;
import ar.edu.unmdp.tpg.tripulacion.Cargo;
import ar.edu.unmdp.tpg.tripulacion.Consejero;
import ar.edu.unmdp.tpg.tripulacion.Origen;
import ar.edu.unmdp.tpg.tripulacion.Teniente;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;
import ar.edu.unmdp.tpg.universo.Universo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador de la seccion de creacion (MVC): crea naves con la NaveFactory y las
 * registra en el Universo, y crea tripulantes y los asigna a traves del asistente.
 *
 * Cuando se crea una nave avisa a los demas controladores (con el Runnable recibido)
 * para que actualicen sus combos de naves.
 */
public class ControladorCreacion implements ActionListener {

    private final IVistaCreacion vista;
    private final Universo universo;
    private final Runnable alCambiarNaves;

    public ControladorCreacion(IVistaCreacion vista, Universo universo, Runnable alCambiarNaves) {
        this.vista = vista;
        this.universo = universo;
        this.alCambiarNaves = alCambiarNaves;
        vista.setActionListener(this);
        vista.mostrarNaves(universo.idsDeNaves());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (IVistaCreacion.CREAR_NAVE.equals(e.getActionCommand())) {
            crearNave();
        } else if (IVistaCreacion.CREAR_TRIPULANTE.equals(e.getActionCommand())) {
            crearTripulante();
        }
    }

    private void crearNave() {
        String id = vista.getIdNave();
        if (id.isEmpty()) {
            vista.mostrarError("Ingrese el id de la nave");
            return;
        }
        if (universo.estaRegistrada(id)) {
            vista.mostrarError("Ya hay una nave con id " + id);
            return;
        }
        try {
            AsistenteDeComando asistente = new AsistenteDeComandoBasico(
                NaveFactory.createNave(id, vista.getTipoNave()), new Bitacora());
            universo.registrar(asistente);
            vista.mostrarNaves(universo.idsDeNaves());
            vista.mostrarAviso("Nave " + id + " (" + vista.getTipoNave() + ") creada");
            alCambiarNaves.run();
        } catch (NaveException error) {
            vista.mostrarError(error.getMessage());
        }
    }

    private void crearTripulante() {
        String idNave = vista.getNaveSeleccionada();
        if (idNave == null) {
            vista.mostrarError("Primero cree una nave");
            return;
        }
        if (vista.getIdTripulante().isEmpty() || vista.getNombre().isEmpty() || vista.getApellido().isEmpty()) {
            vista.mostrarError("Complete id, nombre y apellido del tripulante");
            return;
        }
        Integer antiguedad = vista.getAntiguedad();   // la vista ya aviso si el formato esta mal
        if (antiguedad == null) {
            return;
        }
        try {
            Tripulante tripulante = new Tripulante(vista.getIdTripulante(), vista.getNombre(), vista.getApellido(),
                cargoSegun(vista.getCargo()), Origen.valueOf(vista.getOrigen()), antiguedad);
            universo.obtener(idNave).asignarTripulante(tripulante);
            vista.limpiarTripulante();
            vista.mostrarAviso("Tripulante " + tripulante.getId() + " asignado a " + idNave);
            alCambiarNaves.run();
        } catch (NaveException error) {
            vista.mostrarError(error.getMessage());
        }
    }

    private static Cargo cargoSegun(String nombre) {
        switch (nombre) {
            case "capitan":   return new Capitan();
            case "consejero": return new Consejero();
            case "teniente":  return new Teniente();
            default:          return new Alferez();
        }
    }
}
