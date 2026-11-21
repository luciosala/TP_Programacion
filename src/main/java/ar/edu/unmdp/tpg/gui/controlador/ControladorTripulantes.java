package ar.edu.unmdp.tpg.gui.controlador;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.excepciones.NaveNoRegistradaException;
import ar.edu.unmdp.tpg.gui.vista.IVistaTripulantes;
import ar.edu.unmdp.tpg.haberes.Haber;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;
import ar.edu.unmdp.tpg.universo.Universo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la seccion de tripulantes (MVC): le pide al asistente la tripulacion
 * y la liquidacion de haberes del mes actual (Decorator), y se las da a la vista
 * como filas de texto. La vista no conoce Tripulante ni Haber.
 */
public class ControladorTripulantes implements ActionListener {

    private final IVistaTripulantes vista;
    private final Universo universo;

    public ControladorTripulantes(IVistaTripulantes vista, Universo universo) {
        this.vista = vista;
        this.universo = universo;
        vista.setActionListener(this);
        vista.mostrarNaves(universo.idsDeNaves());
    }

    /** Lo llama el main cuando se crean naves o tripulantes en otra seccion. */
    public void actualizarNaves() {
        vista.mostrarNaves(universo.idsDeNaves());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String idNave = vista.getNaveSeleccionada();
        if (idNave == null) {
            vista.mostrarError("No hay naves creadas");
            return;
        }
        try {
            AsistenteDeComando asistente = universo.obtener(idNave);
            YearMonth periodo = YearMonth.now();
            List<String[]> filas = new ArrayList<>();
            for (Tripulante t : asistente.consultarTripulacion()) {
                Haber haber = asistente.liquidarHaberes(periodo, t);
                filas.add(new String[] {
                    t.getId(),
                    t.getNombre() + " " + t.getApellido(),
                    t.getCargo().getNombre(),
                    t.getOrigen().toString(),
                    String.valueOf(t.getAntiguedadAnios()),
                    String.format("%.2f", haber.calcularTotal())
                });
            }
            vista.mostrarTripulantes(filas, periodo.toString());
        } catch (NaveNoRegistradaException error) {
            vista.mostrarError(error.getMessage());
        }
    }
}
