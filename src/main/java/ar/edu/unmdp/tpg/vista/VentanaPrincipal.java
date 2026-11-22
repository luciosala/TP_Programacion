package ar.edu.unmdp.tpg.vista;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

/**
 * Ventana principal: una ficha (JTabbedPane) por cada sección del Enunciado:
 * Creación, Tripulantes, Control de la Nave y Visor Recóndito.
 */
public class VentanaPrincipal extends JFrame implements IVistaPrincipal {

    public VentanaPrincipal(JPanel creacion, JPanel tripulantes, JPanel control, JPanel visor) {
        super("Nave interestelar - Asistente de navegación y comando");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JTabbedPane fichas = new JTabbedPane();
        fichas.addTab("Creación", creacion);
        fichas.addTab("Tripulantes", tripulantes);
        fichas.addTab("Control de la Nave", control);
        fichas.addTab("Visor Recóndito", visor);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(fichas, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);   // la pestaña de control necesita toda la pantalla
    }

    @Override
    public void mostrar(String usuario) {
        setTitle(getTitle() + " - Operador: " + usuario);
        setVisible(true);
    }
}
