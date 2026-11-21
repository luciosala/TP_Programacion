package ar.edu.unmdp.tpg.gui.vista;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Pestana "Tripulantes": combo de nave, boton para mostrar y una tabla con el salario.
 */
public class PanelTripulantes extends JPanel implements IVistaTripulantes {

    private static final String[] COLUMNAS = {"Id", "Nombre", "Cargo", "Origen", "Antigüedad", "Salario (PG)"};

    private final JComboBox<String> cmbNave = new JComboBox<>();
    private final JButton btnMostrar = new JButton("Mostrar tripulación");
    private final JLabel lblPeriodo = new JLabel();
    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;    // la tabla solo muestra
        }
    };
    private final JTable tabla = new JTable(modeloTabla);

    public PanelTripulantes() {
        setLayout(new BorderLayout(10, 10));

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(new JLabel("Nave:"));
        arriba.add(cmbNave);
        arriba.add(btnMostrar);
        arriba.add(lblPeriodo);
        btnMostrar.setActionCommand(MOSTRAR);

        add(arriba, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    @Override
    public void setActionListener(ActionListener listener) {
        btnMostrar.addActionListener(listener);
    }

    @Override
    public String getNaveSeleccionada() {
        return (String) cmbNave.getSelectedItem();
    }

    @Override
    public void mostrarNaves(List<String> ids) {
        String seleccionada = getNaveSeleccionada();
        cmbNave.removeAllItems();
        for (String id : ids) {
            cmbNave.addItem(id);
        }
        if (seleccionada != null && ids.contains(seleccionada)) {
            cmbNave.setSelectedItem(seleccionada);
        }
    }

    @Override
    public void mostrarTripulantes(List<String[]> filas, String periodo) {
        modeloTabla.setRowCount(0);
        for (String[] fila : filas) {
            modeloTabla.addRow(fila);
        }
        lblPeriodo.setText("Haberes del período " + periodo);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
