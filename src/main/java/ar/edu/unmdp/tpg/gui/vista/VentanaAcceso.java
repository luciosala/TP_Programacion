package ar.edu.unmdp.tpg.gui.vista;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

/**
 * Pantalla de acceso: usuario, contrasena y dos botones (ingresar / registrarse).
 * No sabe nada del modelo: el controlador lee los campos y decide que hacer.
 */
public class VentanaAcceso extends JFrame implements IVistaAcceso {

    private final JTextField txtUsuario = new JTextField(15);
    private final JPasswordField txtContrasena = new JPasswordField(15);
    private final JButton btnIngresar = new JButton("Ingresar");
    private final JButton btnRegistrar = new JButton("Registrarse");

    public VentanaAcceso() {
        super("Acceso al sistema");

        JPanel campos = new JPanel(new GridLayout(2, 2, 5, 5));
        campos.add(new JLabel("Usuario:"));
        campos.add(txtUsuario);
        campos.add(new JLabel("Contraseña:"));
        campos.add(txtContrasena);

        JPanel botones = new JPanel(new FlowLayout());
        botones.add(btnIngresar);
        botones.add(btnRegistrar);

        btnIngresar.setActionCommand(INGRESAR);
        btnRegistrar.setActionCommand(REGISTRAR);

        setLayout(new BorderLayout(10, 10));
        add(campos, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void setActionListener(ActionListener listener) {
        btnIngresar.addActionListener(listener);
        btnRegistrar.addActionListener(listener);
    }

    @Override
    public String getUsuario() {
        return txtUsuario.getText().trim();
    }

    @Override
    public String getContrasena() {
        return new String(txtContrasena.getPassword());
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
    public void mostrar() {
        setVisible(true);
    }

    @Override
    public void cerrar() {
        dispose();
    }
}
