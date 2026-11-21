package ar.edu.unmdp.tpg.gui.vista;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Pestana "Creacion": arriba el formulario de la nave, abajo el del tripulante.
 */
public class PanelCreacion extends JPanel implements IVistaCreacion {

    // Nave
    private final JTextField txtIdNave = new JTextField(10);
    private final JComboBox<String> cmbTipoNave = new JComboBox<>(new String[] {"exploradora", "carguero", "combate"});
    private final JButton btnCrearNave = new JButton("Crear nave");

    // Tripulante
    private final JComboBox<String> cmbNave = new JComboBox<>();
    private final JTextField txtIdTripulante = new JTextField(10);
    private final JTextField txtNombre = new JTextField(10);
    private final JTextField txtApellido = new JTextField(10);
    private final JComboBox<String> cmbCargo = new JComboBox<>(new String[] {"capitan", "consejero", "teniente", "alferez"});
    private final JComboBox<String> cmbOrigen = new JComboBox<>(new String[] {"TERRICOLA", "VULCANO", "MARCIANO"});
    private final JTextField txtAntiguedad = new JTextField(10);
    private final JButton btnCrearTripulante = new JButton("Crear y asignar tripulante");

    public PanelCreacion() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelNave = new JPanel(new GridLayout(3, 2, 5, 5));
        panelNave.setBorder(BorderFactory.createTitledBorder("Nueva nave"));
        panelNave.add(new JLabel("Id:"));
        panelNave.add(txtIdNave);
        panelNave.add(new JLabel("Tipo:"));
        panelNave.add(cmbTipoNave);
        panelNave.add(new JLabel(""));
        panelNave.add(btnCrearNave);

        JPanel panelTripulante = new JPanel(new GridLayout(8, 2, 5, 5));
        panelTripulante.setBorder(BorderFactory.createTitledBorder("Nuevo tripulante"));
        panelTripulante.add(new JLabel("Nave:"));
        panelTripulante.add(cmbNave);
        panelTripulante.add(new JLabel("Id:"));
        panelTripulante.add(txtIdTripulante);
        panelTripulante.add(new JLabel("Nombre:"));
        panelTripulante.add(txtNombre);
        panelTripulante.add(new JLabel("Apellido:"));
        panelTripulante.add(txtApellido);
        panelTripulante.add(new JLabel("Cargo:"));
        panelTripulante.add(cmbCargo);
        panelTripulante.add(new JLabel("Origen:"));
        panelTripulante.add(cmbOrigen);
        panelTripulante.add(new JLabel("Antigüedad (años):"));
        panelTripulante.add(txtAntiguedad);
        panelTripulante.add(new JLabel(""));
        panelTripulante.add(btnCrearTripulante);

        btnCrearNave.setActionCommand(CREAR_NAVE);
        btnCrearTripulante.setActionCommand(CREAR_TRIPULANTE);

        add(panelNave, BorderLayout.NORTH);
        add(panelTripulante, BorderLayout.CENTER);
    }

    @Override
    public void setActionListener(ActionListener listener) {
        btnCrearNave.addActionListener(listener);
        btnCrearTripulante.addActionListener(listener);
    }

    @Override public String getIdNave() { return txtIdNave.getText().trim(); }
    @Override public String getTipoNave() { return (String) cmbTipoNave.getSelectedItem(); }
    @Override public String getIdTripulante() { return txtIdTripulante.getText().trim(); }
    @Override public String getNombre() { return txtNombre.getText().trim(); }
    @Override public String getApellido() { return txtApellido.getText().trim(); }
    @Override public String getCargo() { return (String) cmbCargo.getSelectedItem(); }
    @Override public String getOrigen() { return (String) cmbOrigen.getSelectedItem(); }
    @Override public String getNaveSeleccionada() { return (String) cmbNave.getSelectedItem(); }

    /** La validacion de FORMATO va en la vista: aca se controla que sea un entero no negativo. */
    @Override
    public Integer getAntiguedad() {
        try {
            int valor = Integer.parseInt(txtAntiguedad.getText().trim());
            if (valor < 0) {
                mostrarError("La antigüedad no puede ser negativa");
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            mostrarError("La antigüedad debe ser un número entero");
            return null;
        }
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
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void limpiarTripulante() {
        txtIdTripulante.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtAntiguedad.setText("");
    }
}
