package ar.edu.unmdp.tpg.visor.vista;

import ar.edu.unmdp.tpg.destino.Observador;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

/**
 * Sección del Visor Recóndito: botones para agregar Naves Consumidoras y
 * Generadoras de Energía en vivo, y una consola con el flujo de eventos de la
 * Plataforma de Carga, que se actualiza sola.
 *
 * Es vista (MVC) y observadora del canal de eventos (Observer). Los eventos los
 * publican los hilos de las naves y generadoras; como Swing solo debe modificarse
 * desde su hilo de eventos, el texto se agrega con SwingUtilities.invokeLater y
 * un Runnable (Clase 10).
 */
public class PanelVisor extends JPanel implements IVistaVisor, Observador {

    private static final int MAX_LINEAS = 500;

    private final JButton btnCrearNave = new JButton("Crear Nave Consumidora");
    private final JButton btnCrearGeneradora = new JButton("Crear Generadora de Energía");
    private final JLabel lblCantidades = new JLabel();
    private final JTextArea consola = new JTextArea(22, 70);

    public PanelVisor(int capacidadPlataforma) {
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
    }

    @Override
    public void setActionListener(ActionListener listener) {
        btnCrearNave.addActionListener(listener);
        btnCrearGeneradora.addActionListener(listener);
    }

    @Override
    public void mostrarCantidades(int naves, int generadoras) {
        lblCantidades.setText("   Naves: " + naves + "   Generadoras: " + generadoras);
    }

    /** Observer: llega un evento del universo paralelo (desde el hilo que lo publicó). */
    @Override
    public void actualizar(final String evento) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                consola.append(evento + "\n");
                recortar();
                consola.setCaretPosition(consola.getDocument().getLength());   // autoscroll
            }
        });
    }

    // Deja solo las últimas MAX_LINEAS líneas, para que la consola no crezca sin límite
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
}
