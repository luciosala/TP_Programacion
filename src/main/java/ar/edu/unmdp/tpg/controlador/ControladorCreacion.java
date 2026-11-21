package ar.edu.unmdp.tpg.controlador;

import ar.edu.unmdp.tpg.aplicacion.ModeloAplicacion;
import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.excepciones.NaveNoRegistradaException;
import ar.edu.unmdp.tpg.excepciones.NaveYaRegistradaException;
import ar.edu.unmdp.tpg.excepciones.TripulacionInvalidaException;
import ar.edu.unmdp.tpg.naves.Nave;
import ar.edu.unmdp.tpg.vista.IVistaCreacion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador de la sección de Creación (MVC): crea naves, elige la nave activa
 * y asigna tripulantes. Después de cada cambio le pide a la sección de
 * Tripulantes que se actualice.
 */
public class ControladorCreacion implements ActionListener {

    private final IVistaCreacion vista;
    private final ModeloAplicacion modelo;
    private final ControladorTripulantes tripulantes;

    public ControladorCreacion(IVistaCreacion vista, ModeloAplicacion modelo, ControladorTripulantes tripulantes) {
        this.vista = vista;
        this.modelo = modelo;
        this.tripulantes = tripulantes;
        vista.setActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        String comando = evento.getActionCommand();
        if (comando.equals(IVistaCreacion.CREAR_NAVE)) {
            crearNave();
        } else if (comando.equals(IVistaCreacion.SELECCIONAR_NAVE)) {
            seleccionarNave();
        } else if (comando.equals(IVistaCreacion.AGREGAR_TRIPULANTE)) {
            agregarTripulante();
        }
        actualizarVista();
    }

    private void crearNave() {
        String errorDeFormato = vista.validarFormatoNave();
        if (errorDeFormato != null) {
            vista.mostrarError(errorDeFormato);
            return;
        }
        try {
            modelo.crearNave(vista.getIdNave(), vista.getTipoNave());
        } catch (NaveYaRegistradaException error) {
            vista.mostrarError(error.getMessage());
        }
    }

    private void seleccionarNave() {
        String id = vista.getNaveElegida();
        if (id == null) {
            vista.mostrarError("Todavía no hay naves registradas.");
            return;
        }
        try {
            modelo.seleccionarNave(id);
        } catch (NaveNoRegistradaException error) {
            vista.mostrarError(error.getMessage());
        }
    }

    private void agregarTripulante() {
        if (!modelo.hayNaveActiva()) {
            vista.mostrarError("Primero cree una nave.");
            return;
        }
        String errorDeFormato = vista.validarFormatoTripulante();
        if (errorDeFormato != null) {
            vista.mostrarError(errorDeFormato);
            return;
        }
        try {
            modelo.asignarTripulante(vista.getIdTripulante(), vista.getNombreTripulante(),
                vista.getApellidoTripulante(), vista.getCargo(), vista.getOrigen(), vista.getAntiguedad());
            vista.limpiarTripulante();
        } catch (TripulacionInvalidaException error) {
            vista.mostrarError(error.getMessage());
        }
    }

    /** Muestra las naves registradas y el resumen de la activa. */
    private void actualizarVista() {
        if (!modelo.hayNaveActiva()) {
            return;
        }
        AsistenteDeComando nave = modelo.getNaveActiva();
        vista.mostrarNaves(modelo.idsDeNaves(), nave.idNave());
        StringBuilder resumen = new StringBuilder();
        resumen.append("Nave ").append(nave.idNave()).append('\n');
        resumen.append("Tripulantes: ").append(nave.cantidadTripulantes())
               .append(" (mínimo ").append(Nave.DOTACION_MINIMA).append(", con un capitán)\n");
        resumen.append(nave.tripulacionLista() ? "Tripulación lista para operar\n"
                                               : "Tripulación incompleta: no puede iniciar misiones\n");
        resumen.append("Combustible ").append(nave.combustibleDisponible())
               .append(" | Energía ").append(nave.energiaDisponible())
               .append(" | Desgaste ").append(nave.desgasteActual())
               .append(" | Motor ").append(nave.estadoDelMotor());
        vista.mostrarResumen(resumen.toString());
        tripulantes.actualizarVista();
    }
}
