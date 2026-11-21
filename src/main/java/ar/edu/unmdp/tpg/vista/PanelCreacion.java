package ar.edu.unmdp.tpg.vista;

import ar.edu.unmdp.tpg.aplicacion.ModeloAplicacion;
import ar.edu.unmdp.tpg.destino.Observador;
import ar.edu.unmdp.tpg.tripulacion.FabricaDeCargos;
import ar.edu.unmdp.tpg.tripulacion.Origen;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Sección de Creación: elegir el tipo de nave e instanciarla (con NaveFactory, a
 * través del modelo) y crear tripulantes para asignarlos a la nave activa.
 *
 * Observa al modelo de la aplicación (Observer) solo para deshabilitarse mientras
 * hay una misión en curso.
 */
public class PanelCreacion extends JPanel implements IVistaCreacion, Observador {

    private static final String[] TIPOS = { "exploradora", "carguero", "combate" };

    private final ModeloAplicacion modelo;

    private final JTextField txtIdNave = new JTextField(10);
    private final JComboBox<String> cmbTipo = new JComboBox<>(TIPOS);
    private final JButton btnCrearNave = new JButton("Crear nave");
    private final JComboBox<String> cmbNaves = new JComboBox<>();
    private final JButton btnSeleccionar = new JButton("Operar esta nave");
    private final JTextArea txtResumen = new JTextArea(6, 40);

    private final JTextField txtIdTripulante = new JTextField(10);
    private final JTextField txtNombre = new JTextField(10);
    private final JTextField txtApellido = new JTextField(10);
    private final JComboBox<String> cmbCargo = new JComboBox<>(FabricaDeCargos.NOMBRES);
    private final JComboBox<Origen> cmbOrigen = new JComboBox<>(Origen.values());
    private final JTextField txtAntiguedad = new JTextField(4);
    private final JButton btnAgregar = new JButton("Asignar a la nave activa");

    public PanelCreacion(ModeloAplicacion modelo) {
        this.modelo = modelo;
        setLayout(new BorderLayout(10, 10));

        // Nave
        JPanel nave = new JPanel(new GridLayout(3, 3, 5, 5));
        nave.setBorder(BorderFactory.createTitledBorder("Nave"));
        nave.add(new JLabel("Id de la nave:"));
        nave.add(txtIdNave);
        nave.add(new JLabel(""));
        nave.add(new JLabel("Tipo:"));
        nave.add(cmbTipo);
        nave.add(btnCrearNave);
        nave.add(new JLabel("Naves registradas:"));
        nave.add(cmbNaves);
        nave.add(btnSeleccionar);

        // Tripulante
        JPanel tripulante = new JPanel(new GridLayout(7, 2, 5, 5));
        tripulante.setBorder(BorderFactory.createTitledBorder("Tripulante"));
        tripulante.add(new JLabel("Id:"));
        tripulante.add(txtIdTripulante);
        tripulante.add(new JLabel("Nombre:"));
        tripulante.add(txtNombre);
        tripulante.add(new JLabel("Apellido:"));
        tripulante.add(txtApellido);
        tripulante.add(new JLabel("Cargo:"));
        tripulante.add(cmbCargo);
        tripulante.add(new JLabel("Planeta de origen:"));
        tripulante.add(cmbOrigen);
        tripulante.add(new JLabel("Antigüedad (años):"));
        tripulante.add(txtAntiguedad);
        tripulante.add(new JLabel(""));
        tripulante.add(btnAgregar);

        txtResumen.setEditable(false);
        JScrollPane resumen = new JScrollPane(txtResumen);
        resumen.setBorder(BorderFactory.createTitledBorder("Nave activa"));

        JPanel arriba = new JPanel(new GridLayout(1, 2, 10, 10));
        arriba.add(nave);
        arriba.add(tripulante);
        add(arriba, BorderLayout.NORTH);
        add(resumen, BorderLayout.CENTER);

        btnCrearNave.setActionCommand(CREAR_NAVE);
        btnSeleccionar.setActionCommand(SELECCIONAR_NAVE);
        btnAgregar.setActionCommand(AGREGAR_TRIPULANTE);
        mostrarResumen("Todavía no hay naves. Cree una nave y asígnele un capitán y cuatro tripulantes más.");
    }

    @Override
    public void setActionListener(ActionListener controlador) {
        btnCrearNave.addActionListener(controlador);
        btnSeleccionar.addActionListener(controlador);
        btnAgregar.addActionListener(controlador);
    }

    @Override
    public String getIdNave() {
        return txtIdNave.getText().trim();
    }

    @Override
    public String getTipoNave() {
        return (String) cmbTipo.getSelectedItem();
    }

    @Override
    public String getNaveElegida() {
        return (String) cmbNaves.getSelectedItem();
    }

    @Override
    public String getIdTripulante() {
        return txtIdTripulante.getText().trim();
    }

    @Override
    public String getNombreTripulante() {
        return txtNombre.getText().trim();
    }

    @Override
    public String getApellidoTripulante() {
        return txtApellido.getText().trim();
    }

    @Override
    public String getCargo() {
        return (String) cmbCargo.getSelectedItem();
    }

    @Override
    public Origen getOrigen() {
        return (Origen) cmbOrigen.getSelectedItem();
    }

    @Override
    public int getAntiguedad() {
        return Integer.parseInt(txtAntiguedad.getText().trim());
    }

    @Override
    public String validarFormatoNave() {
        if (getIdNave().isEmpty()) {
            return "Ingrese el id de la nave.";
        }
        return null;
    }

    @Override
    public String validarFormatoTripulante() {
        if (getIdTripulante().isEmpty() || getNombreTripulante().isEmpty() || getApellidoTripulante().isEmpty()) {
            return "Complete id, nombre y apellido del tripulante.";
        }
        try {
            if (getAntiguedad() < 0) {
                return "La antigüedad no puede ser negativa.";
            }
        } catch (NumberFormatException noEsNumero) {
            return "La antigüedad debe ser un número entero de años.";
        }
        return null;
    }

    @Override
    public void mostrarNaves(List<String> ids, String activa) {
        cmbNaves.removeAllItems();
        for (String id : ids) {
            cmbNaves.addItem(id);
        }
        cmbNaves.setSelectedItem(activa);
    }

    @Override
    public void mostrarResumen(String resumen) {
        txtResumen.setText(resumen);
    }

    @Override
    public void limpiarTripulante() {
        txtIdTripulante.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtAntiguedad.setText("");
    }

    @Override
    public void habilitar(boolean habilitado) {
        btnCrearNave.setEnabled(habilitado);
        btnSeleccionar.setEnabled(habilitado);
        btnAgregar.setEnabled(habilitado);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Creación", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Observer: el modelo avisó un cambio. Puede llegar desde el hilo de la misión,
     * así que la actualización de Swing se le encarga al hilo de eventos de Swing.
     */
    @Override
    public void actualizar(String mensaje) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                habilitar(!modelo.hayMisionEnCurso());
            }
        });
    }
}
