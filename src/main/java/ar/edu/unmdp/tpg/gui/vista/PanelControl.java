package ar.edu.unmdp.tpg.gui.vista;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Pestana "Control de la nave".
 * Arriba: combo de nave, combo de mision y botones. Izquierda: recursos y bitacora.
 * Centro: el tablero. Derecha: historial de informes.
 */
public class PanelControl extends JPanel implements IVistaControl {

    private final JComboBox<String> cmbNave = new JComboBox<>();
    private final JComboBox<String> cmbMision = new JComboBox<>(MISIONES);
    private final JButton btnIniciar = new JButton("Iniciar misión");
    private final JButton btnMantenimiento = new JButton("Mantenimiento");
    private final JTextField txtCantidad = new JTextField("20", 4);
    private final JButton btnCombustible = new JButton("Cargar combustible");
    private final JButton btnEnergia = new JButton("Cargar energía");

    private final JLabel lblRecursos = new JLabel("Sin nave seleccionada");
    private final JLabel lblBase = new JLabel();
    private final JTextArea txtBitacora = new JTextArea(14, 34);
    private final JTextArea txtInformes = new JTextArea(14, 30);
    private final PanelTablero panelTablero = new PanelTablero();

    public PanelControl() {
        setLayout(new BorderLayout(10, 10));

        // Dos filas: arriba la mision, abajo lo que solo se puede hacer en la base
        JPanel filaMision = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaMision.add(new JLabel("Nave:"));
        filaMision.add(cmbNave);
        filaMision.add(new JLabel("Misión:"));
        filaMision.add(cmbMision);
        filaMision.add(btnIniciar);
        JPanel filaBase = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaBase.add(new JLabel("Cantidad:"));
        filaBase.add(txtCantidad);
        filaBase.add(btnCombustible);
        filaBase.add(btnEnergia);
        filaBase.add(btnMantenimiento);
        filaBase.add(lblBase);
        JPanel arriba = new JPanel(new GridLayout(2, 1));
        arriba.add(filaMision);
        arriba.add(filaBase);
        cmbNave.setActionCommand(SELECCIONAR_NAVE);
        btnIniciar.setActionCommand(INICIAR_MISION);
        btnMantenimiento.setActionCommand(MANTENIMIENTO);
        btnCombustible.setActionCommand(CARGAR_COMBUSTIBLE);
        btnEnergia.setActionCommand(CARGAR_ENERGIA);

        txtBitacora.setEditable(false);
        txtBitacora.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        txtInformes.setEditable(false);
        txtInformes.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));

        JPanel izquierda = new JPanel(new BorderLayout(5, 5));
        lblRecursos.setBorder(BorderFactory.createTitledBorder("Recursos"));
        izquierda.add(lblRecursos, BorderLayout.NORTH);
        JScrollPane scrollBitacora = new JScrollPane(txtBitacora);
        scrollBitacora.setBorder(BorderFactory.createTitledBorder("Bitácora"));
        izquierda.add(scrollBitacora, BorderLayout.CENTER);

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centro.setBorder(BorderFactory.createTitledBorder("Tablero"));
        centro.add(panelTablero);

        JScrollPane scrollInformes = new JScrollPane(txtInformes);
        scrollInformes.setBorder(BorderFactory.createTitledBorder("Informes de misión"));

        add(arriba, BorderLayout.NORTH);
        add(izquierda, BorderLayout.WEST);
        add(centro, BorderLayout.CENTER);
        add(scrollInformes, BorderLayout.EAST);
    }

    @Override
    public void setActionListener(ActionListener listener) {
        cmbNave.addActionListener(listener);
        btnIniciar.addActionListener(listener);
        btnMantenimiento.addActionListener(listener);
        btnCombustible.addActionListener(listener);
        btnEnergia.addActionListener(listener);
    }

    @Override
    public String getNaveSeleccionada() {
        return (String) cmbNave.getSelectedItem();
    }

    @Override
    public int getMisionSeleccionada() {
        return cmbMision.getSelectedIndex();
    }

    /** La validacion de FORMATO va en la vista: entero positivo. */
    @Override
    public Integer getCantidadCarga() {
        try {
            int valor = Integer.parseInt(txtCantidad.getText().trim());
            if (valor <= 0) {
                mostrarError("La cantidad a cargar debe ser mayor que 0");
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            mostrarError("La cantidad a cargar debe ser un número entero");
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
    public void mostrarRecursos(int combustible, int energia, int desgaste, int gemas, String estadoMotor, boolean destruida) {
        lblRecursos.setText("<html>Combustible: " + combustible + " &nbsp; Energía: " + energia
            + " &nbsp; Desgaste: " + desgaste + "<br>Gemas: " + gemas + " &nbsp; Motor: " + estadoMotor
            + (destruida ? " &nbsp; <b>NAVE DESTRUIDA</b>" : "") + "</html>");
    }

    @Override
    public void mostrarBitacora(List<String> eventos) {
        StringBuilder texto = new StringBuilder();
        for (String evento : eventos) {
            texto.append(evento).append('\n');
        }
        txtBitacora.setText(texto.toString());
        txtBitacora.setCaretPosition(txtBitacora.getDocument().getLength());   // autoscroll
    }

    @Override
    public void agregarInforme(String informe) {
        txtInformes.append(informe + "\n" + "-".repeat(40) + "\n");
        txtInformes.setCaretPosition(txtInformes.getDocument().getLength());
    }

    @Override
    public void mostrarTablero(String[][] etiquetas, int filaNave, int columnaNave, int[] base, int[] objetivo, List<int[]> ruta) {
        panelTablero.mostrar(etiquetas, filaNave, columnaNave, base, objetivo, ruta);
    }

    @Override
    public void habilitarCarga(boolean enLaBase) {
        btnMantenimiento.setEnabled(enLaBase);
        btnCombustible.setEnabled(enLaBase);
        btnEnergia.setEnabled(enLaBase);
        String texto = enLaBase ? "   En la base: se puede cargar y reparar" : "   Fuera de la base: no se puede cargar";
        lblBase.setText(texto);
    }

    @Override
    public void habilitarMision(boolean habilitada) {
        btnIniciar.setEnabled(habilitada);
        cmbNave.setEnabled(habilitada);
        if (!habilitada) {               // en mision no se carga; al terminar, el controlador vuelve a decidir
            btnMantenimiento.setEnabled(false);
            btnCombustible.setEnabled(false);
            btnEnergia.setEnabled(false);
        }
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
