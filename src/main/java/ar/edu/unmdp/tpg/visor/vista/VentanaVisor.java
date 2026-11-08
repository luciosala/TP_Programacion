package ar.edu.unmdp.tpg.visor.vista;

import ar.edu.unmdp.tpg.visor.modelo.ObservadorVisor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Vista del visor del universo paralelo: los dos botones del panel de control y la
 * consola de eventos en vivo.
 *
 * Cumple dos roles:
 * - Vista del MVC (IVistaVisor): los botones avisan al controlador.
 * - Observador concreto del patron Observer (ObservadorVisor): recibe los eventos
 *   que publican la plataforma, las generadoras y las naves.
 */
public class VentanaVisor extends JFrame implements IVistaVisor, ObservadorVisor {

    private static final int MAX_LINEAS = 500;

    private final JButton btnCrearNave = new JButton("Crear Nave Consumidora");
    private final JButton btnCrearGeneradora = new JButton("Crear Generadora de Energía");
    private final JLabel lblCantidades = new JLabel();
    private final JTextArea consola = new JTextArea(22, 70);

    public VentanaVisor(int capacidadPlataforma) {
        super("Visor del universo paralelo");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.setBorder(BorderFactory.createTitledBorder("Panel de control"));
        botones.add(btnCrearNave);
        botones.add(btnCrearGeneradora);
        botones.add(lblCantidades);

        btnCrearNave.setActionCommand(CREAR_NAVE);
        btnCrearGeneradora.setActionCommand(CREAR_GENERADORA);

        consola.setEditable(false);
        consola.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(consola);
        scroll.setBorder(BorderFactory.createTitledBorder(
                "Eventos en vivo (plataforma de carga con capacidad " + capacidadPlataforma + ")"));

        setLayout(new BorderLayout());
        add(botones, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        mostrarCantidades(0, 0);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void setActionListener(ActionListener listener) {
        btnCrearNave.addActionListener(listener);
        btnCrearGeneradora.addActionListener(listener);
    }

    @Override
    public void alCerrar(Runnable accion) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                accion.run();
            }
        });
    }

    @Override
    public void mostrarCantidades(int naves, int generadoras) {
        lblCantidades.setText("   Naves: " + naves + "   Generadoras: " + generadoras);
    }

    /**
     * Lo llaman los hilos de las generadoras y naves, no el hilo de Swing.
     * Swing no es seguro para hilos: el append se encola con invokeLater para que lo
     * haga el hilo de Swing (Event Dispatch Thread).
     */
    @Override
    public void actualizar(String evento) {
        SwingUtilities.invokeLater(() -> {
            consola.append(evento + "\n");
            recortar();
            consola.setCaretPosition(consola.getDocument().getLength());   // autoscroll
        });
    }

    /** Evita que la consola crezca sin limite si la simulacion corre mucho tiempo. */
    private void recortar() {
        int sobrantes = consola.getLineCount() - MAX_LINEAS;
        if (sobrantes > 0) {
            try {
                consola.replaceRange("", 0, consola.getLineEndOffset(sobrantes - 1));
            } catch (javax.swing.text.BadLocationException ignorada) {
                // no puede pasar: el offset sale de la propia consola
            }
        }
    }

    @Override
    public void mostrar() {
        setVisible(true);
    }
}
