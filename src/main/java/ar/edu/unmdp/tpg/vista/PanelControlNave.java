package ar.edu.unmdp.tpg.vista;

import ar.edu.unmdp.tpg.aplicacion.ModeloAplicacion;
import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.bitacora.EventoBitacora;
import ar.edu.unmdp.tpg.destino.Observador;
import ar.edu.unmdp.tpg.mision.InformeMision;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Sección de Control de la Nave: estado actual de los recursos, bitácora de
 * eventos, historial de informes finales, controles para iniciar una misión y el
 * tablero en tiempo real.
 *
 * Patrón Observer (Clase 11): la vista se registra en el modelo de la
 * aplicación y, cada vez que este avisa un cambio (un evento de la bitácora o un
 * movimiento en el tablero), vuelve a consultar al modelo y se redibuja sola
 * (actualización automática). El modelo no sabe que esta vista existe.
 *
 * Los avisos llegan desde el hilo de la misión; Swing solo debe modificarse desde
 * su propio hilo de eventos, por eso la actualización se le encarga con
 * SwingUtilities.invokeLater y un Runnable (Clase 10).
 */
public class PanelControlNave extends JPanel implements IVistaControl, Observador {

    private final ModeloAplicacion modelo;

    private final JLabel lblNave = new JLabel();
    private final JLabel lblCombustible = new JLabel();
    private final JLabel lblEnergia = new JLabel();
    private final JLabel lblDesgaste = new JLabel();
    private final JLabel lblMotor = new JLabel();
    private final JLabel lblGemas = new JLabel();
    private final JLabel lblEstado = new JLabel();
    private final PanelTablero panelTablero =
        new PanelTablero(ModeloAplicacion.FILAS, ModeloAplicacion.COLUMNAS);
    private final JTextArea txtBitacora = new JTextArea();
    private final JTextArea txtInformes = new JTextArea();
    private final JLabel lblUltimoAviso = new JLabel(" ");

    private final JButton btnM01 = new JButton("M-01 Intercepción");
    private final JButton btnM02 = new JButton("M-02 Recolección");
    private final JButton btnM03 = new JButton("M-03 Retorno seguro");
    private final JTextField txtCantidad = new JTextField(4);
    private final JButton btnCombustible = new JButton("Cargar combustible");
    private final JButton btnEnergia = new JButton("Cargar energía");
    private final JButton btnMantenimiento = new JButton("Mantenimiento");

    // Lo que ya se mostró, para agregar solo lo nuevo
    private String naveMostrada = null;
    private int eventosMostrados = 0;
    private int informesMostrados = 0;

    public PanelControlNave(ModeloAplicacion modelo) {
        this.modelo = modelo;
        setLayout(new BorderLayout(5, 5));

        JPanel estado = new JPanel(new GridLayout(1, 7, 5, 5));
        estado.setBorder(BorderFactory.createTitledBorder("Estado de la nave"));
        estado.add(lblNave);
        estado.add(lblCombustible);
        estado.add(lblEnergia);
        estado.add(lblDesgaste);
        estado.add(lblMotor);
        estado.add(lblGemas);
        estado.add(lblEstado);
        add(estado, BorderLayout.NORTH);

        add(panelTablero, BorderLayout.CENTER);

        Font monoespaciada = new Font(Font.MONOSPACED, Font.PLAIN, 11);
        txtBitacora.setEditable(false);
        txtBitacora.setFont(monoespaciada);
        txtInformes.setEditable(false);
        txtInformes.setFont(monoespaciada);
        JScrollPane scrollBitacora = new JScrollPane(txtBitacora);
        scrollBitacora.setBorder(BorderFactory.createTitledBorder("Bitácora de eventos"));
        JScrollPane scrollInformes = new JScrollPane(txtInformes);
        scrollInformes.setBorder(BorderFactory.createTitledBorder("Historial de informes finales"));
        JPanel registros = new JPanel(new GridLayout(2, 1, 5, 5));
        registros.add(scrollBitacora);
        registros.add(scrollInformes);
        registros.setPreferredSize(new Dimension(520, 500));
        add(registros, BorderLayout.EAST);

        JPanel misiones = new JPanel();
        misiones.setBorder(BorderFactory.createTitledBorder("Iniciar misión"));
        misiones.add(btnM01);
        misiones.add(btnM02);
        misiones.add(btnM03);
        JPanel recursos = new JPanel();
        recursos.setBorder(BorderFactory.createTitledBorder("Recursos y mantenimiento"));
        recursos.add(new JLabel("Cantidad:"));
        recursos.add(txtCantidad);
        recursos.add(btnCombustible);
        recursos.add(btnEnergia);
        recursos.add(btnMantenimiento);
        JPanel controles = new JPanel(new GridLayout(1, 2, 5, 5));
        controles.add(misiones);
        controles.add(recursos);
        JPanel sur = new JPanel(new BorderLayout());
        sur.add(controles, BorderLayout.CENTER);
        sur.add(lblUltimoAviso, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);

        btnM01.setActionCommand(INICIAR_M01);
        btnM02.setActionCommand(INICIAR_M02);
        btnM03.setActionCommand(INICIAR_M03);
        btnCombustible.setActionCommand(CARGAR_COMBUSTIBLE);
        btnEnergia.setActionCommand(CARGAR_ENERGIA);
        btnMantenimiento.setActionCommand(MANTENIMIENTO);

        refrescar();
    }

    @Override
    public void setActionListener(ActionListener controlador) {
        btnM01.addActionListener(controlador);
        btnM02.addActionListener(controlador);
        btnM03.addActionListener(controlador);
        btnCombustible.addActionListener(controlador);
        btnEnergia.addActionListener(controlador);
        btnMantenimiento.addActionListener(controlador);
    }

    @Override
    public String validarFormatoCantidad() {
        try {
            if (getCantidad() <= 0) {
                return "La cantidad a cargar debe ser mayor que 0.";
            }
        } catch (NumberFormatException noEsNumero) {
            return "Ingrese la cantidad a cargar como número entero.";
        }
        return null;
    }

    @Override
    public int getCantidad() {
        return Integer.parseInt(txtCantidad.getText().trim());
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Control de la nave", JOptionPane.ERROR_MESSAGE);
    }

    /** Observer: el modelo avisó un cambio; se redibuja en el hilo de eventos de Swing. */
    @Override
    public void actualizar(final String mensaje) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblUltimoAviso.setText(mensaje);
                refrescar();
            }
        });
    }

    /** Vuelve a consultar al modelo y muestra su estado actual. */
    private void refrescar() {
        boolean hayNave = modelo.hayNaveActiva();
        boolean enMision = modelo.hayMisionEnCurso();
        if (!hayNave) {
            lblNave.setText("Sin nave activa");
            habilitar(false);
            return;
        }
        AsistenteDeComando nave = modelo.getNaveActiva();
        lblNave.setText("Nave " + nave.idNave());
        lblCombustible.setText("Combustible: " + nave.combustibleDisponible());
        lblEnergia.setText("Energía: " + nave.energiaDisponible());
        lblDesgaste.setText("Desgaste: " + nave.desgasteActual()
            + (nave.requiereMantenimiento() ? " (mant.)" : ""));
        lblMotor.setText("Motor: " + nave.estadoDelMotor());
        lblGemas.setText("Gemas: " + nave.gemasDisponibles());
        if (nave.naveDestruida()) {
            lblEstado.setText("DESTRUIDA");
        } else if (enMision) {
            lblEstado.setText("En misión");
        } else if (!nave.tripulacionLista()) {
            lblEstado.setText("Falta tripulación");
        } else {
            lblEstado.setText("Operativa");
        }

        panelTablero.dibujar(modelo.getFotoTablero(), modelo.getFotoNave(), modelo.getFotoObjetivo(), modelo.getBase());
        mostrarBitacora(nave);
        mostrarInformes();
        habilitar(!enMision && !nave.naveDestruida());
    }

    private void mostrarBitacora(AsistenteDeComando nave) {
        if (!nave.idNave().equals(naveMostrada)) {   // cambió la nave activa: se muestra su bitácora
            naveMostrada = nave.idNave();
            eventosMostrados = 0;
            txtBitacora.setText("");
        }
        List<EventoBitacora> eventos = nave.consultarBitacora();
        for (int i = eventosMostrados; i < eventos.size(); i++) {
            txtBitacora.append(eventos.get(i) + "\n");
        }
        eventosMostrados = eventos.size();
        txtBitacora.setCaretPosition(txtBitacora.getDocument().getLength());
    }

    private void mostrarInformes() {
        List<InformeMision> informes = modelo.getInformes();
        if (informes.size() != informesMostrados) {
            for (int i = informesMostrados; i < informes.size(); i++) {
                txtInformes.append(informes.get(i) + "\n\n");
            }
            informesMostrados = informes.size();
            txtInformes.setCaretPosition(txtInformes.getDocument().getLength());
        }
    }

    private void habilitar(boolean habilitado) {
        btnM01.setEnabled(habilitado);
        btnM02.setEnabled(habilitado);
        btnM03.setEnabled(habilitado);
        btnCombustible.setEnabled(habilitado);
        btnEnergia.setEnabled(habilitado);
        btnMantenimiento.setEnabled(habilitado);
    }
}
