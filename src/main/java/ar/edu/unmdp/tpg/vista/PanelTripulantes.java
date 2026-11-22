package ar.edu.unmdp.tpg.vista;

import ar.edu.unmdp.tpg.haberes.Haber;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Sección de Tripulantes: listado de la tripulación de la nave activa con su
 * información detallada y el salario de cada uno (JTable con su modelo de tabla),
 * y abajo el detalle de los conceptos de cada haber (patrón Decorator).
 */
public class PanelTripulantes extends JPanel implements IVistaTripulantes {

    private final TablaTripulantesModel modeloTabla = new TablaTripulantesModel();
    private final JTable tabla = new JTable(modeloTabla);
    private final JTextArea txtDetalle = new JTextArea(8, 60);
    private final JButton btnActualizar = new JButton("Actualizar liquidación");
    private final JButton btnConsejo = new JButton("Registrar consejo del consejero seleccionado");

    public PanelTripulantes() {
        setLayout(new BorderLayout(5, 5));
        tabla.setPreferredScrollableViewportSize(new Dimension(600, 160));
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Tripulación de la nave activa"));

        txtDetalle.setEditable(false);
        txtDetalle.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollDetalle = new JScrollPane(txtDetalle);
        scrollDetalle.setBorder(BorderFactory.createTitledBorder("Detalle de haberes (concepto por concepto)"));

        JPanel centro = new JPanel(new GridLayout(2, 1, 5, 5));
        centro.add(scrollTabla);
        centro.add(scrollDetalle);
        add(centro, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        botones.add(btnActualizar);
        botones.add(btnConsejo);
        add(botones, BorderLayout.SOUTH);

        btnActualizar.setActionCommand(ACTUALIZAR);
        btnConsejo.setActionCommand(REGISTRAR_CONSEJO);
    }

    @Override
    public void setActionListener(ActionListener controlador) {
        btnActualizar.addActionListener(controlador);
        btnConsejo.addActionListener(controlador);
    }

    @Override
    public void mostrarTripulantes(List<Tripulante> tripulantes, List<Haber> haberes, String periodo) {
        modeloTabla.setDatos(tripulantes, haberes);
        StringBuilder detalle = new StringBuilder("Período liquidado: " + periodo + "\n\n");
        for (int i = 0; i < tripulantes.size(); i++) {
            Tripulante t = tripulantes.get(i);
            Haber haber = haberes.get(i);
            detalle.append(t.getId()).append(" - ").append(t.getNombre()).append(' ').append(t.getApellido())
                   .append(" (").append(t.getCargo().getNombre()).append(")\n");
            for (int c = 0; c < haber.cantidadConceptos(); c++) {
                detalle.append(String.format("    %-36s %10.2f PG%n",
                    haber.consultarNombreConcepto(c), haber.consultarImporteConcepto(c)));
            }
            detalle.append(String.format("    %-36s %10.2f PG%n%n", "TOTAL", haber.calcularTotal()));
        }
        txtDetalle.setText(detalle.toString());
        txtDetalle.setCaretPosition(0);
    }

    @Override
    public String getTripulanteSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        return modeloTabla.getId(fila);
    }

    @Override
    public String pedirDescripcionConsejo() {
        return JOptionPane.showInputDialog(this, "¿Qué aconsejó?", "Registrar consejo", JOptionPane.QUESTION_MESSAGE);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Tripulantes", JOptionPane.ERROR_MESSAGE);
    }
}
