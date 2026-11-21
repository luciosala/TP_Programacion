package ar.edu.unmdp.tpg.gui.vista;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

/**
 * Ventana principal: una pestana por seccion de la consigna, mas el boton del visor.
 * Cada pestana es su propia vista (IVistaCreacion, IVistaTripulantes, IVistaControl)
 * con su propio controlador; esta ventana solo las agrupa.
 */
public class VentanaPrincipal extends JFrame {

    public static final String ABRIR_VISOR = "ABRIR_VISOR";

    private final PanelCreacion panelCreacion = new PanelCreacion();
    private final PanelTripulantes panelTripulantes = new PanelTripulantes();
    private final PanelControl panelControl = new PanelControl();
    private final JButton btnVisor = new JButton("Abrir visor del universo paralelo");

    public VentanaPrincipal(String usuario) {
        super("Software embarcado - TPG 2026");

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Creación", panelCreacion);
        pestanas.addTab("Tripulantes", panelTripulantes);
        pestanas.addTab("Control de la nave", panelControl);

        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        abajo.add(new JLabel("Operador: " + usuario + "   "));
        abajo.add(btnVisor);
        btnVisor.setActionCommand(ABRIR_VISOR);

        setLayout(new BorderLayout());
        add(pestanas, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    public IVistaCreacion getVistaCreacion() {
        return panelCreacion;
    }

    public IVistaTripulantes getVistaTripulantes() {
        return panelTripulantes;
    }

    public IVistaControl getVistaControl() {
        return panelControl;
    }

    public void setVisorListener(ActionListener listener) {
        btnVisor.addActionListener(listener);
    }

    public void mostrar() {
        setVisible(true);
    }
}
