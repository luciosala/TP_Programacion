package ar.edu.unmdp.tpg.vista;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Ventana de acceso: una ficha con la Vista de Login y otra con la Vista de Registro.
 *
 * Validaciones de formato en la vista (Guía del TPG): los botones se habilitan
 * solo cuando los campos están completos, igual que el ejemplo VentanaLogin de la
 * teoría (KeyAdapter como clase anónima). El registro además controla longitudes
 * y que las dos contraseñas coincidan. Las reglas del negocio (usuario repetido,
 * credenciales incorrectas) las decide el servicio, no la vista.
 */
public class VentanaAcceso extends JFrame implements IVistaAcceso {

    public static final int MINIMO_USUARIO = 3;
    public static final int MAXIMO_USUARIO = 30;   // VARCHAR(30) en database/schema.sql
    public static final int MINIMO_CONTRASENA = 4;

    private final JTextField txtUsuarioLogin = new JTextField(15);
    private final JPasswordField txtContrasenaLogin = new JPasswordField(15);
    private final JButton btnIngresar = new JButton("Ingresar");

    private final JTextField txtUsuarioRegistro = new JTextField(15);
    private final JPasswordField txtContrasenaRegistro = new JPasswordField(15);
    private final JPasswordField txtRepetirContrasena = new JPasswordField(15);
    private final JButton btnRegistrar = new JButton("Registrarse");

    public VentanaAcceso() {
        super("Nave interestelar - Acceso al sistema");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane fichas = new JTabbedPane();
        fichas.addTab("Iniciar sesión", armarLogin());
        fichas.addTab("Registrarse", armarRegistro());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(fichas, BorderLayout.CENTER);

        btnIngresar.setActionCommand(INGRESAR);
        btnRegistrar.setActionCommand(REGISTRAR);
        btnIngresar.setEnabled(false);
        btnRegistrar.setEnabled(false);

        // Validación de formato: el botón queda deshabilitado mientras falte un dato
        KeyAdapter controlLogin = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                btnIngresar.setEnabled(!getUsuarioLogin().isBlank() && !getContrasenaLogin().isEmpty());
            }
        };
        txtUsuarioLogin.addKeyListener(controlLogin);
        txtContrasenaLogin.addKeyListener(controlLogin);

        KeyAdapter controlRegistro = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                btnRegistrar.setEnabled(!getUsuarioRegistro().isBlank() && !getContrasenaRegistro().isEmpty()
                    && txtRepetirContrasena.getPassword().length > 0);
            }
        };
        txtUsuarioRegistro.addKeyListener(controlRegistro);
        txtContrasenaRegistro.addKeyListener(controlRegistro);
        txtRepetirContrasena.addKeyListener(controlRegistro);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel armarLogin() {
        JPanel campos = new JPanel(new GridLayout(2, 2, 5, 5));
        campos.add(new JLabel("Usuario:"));
        campos.add(txtUsuarioLogin);
        campos.add(new JLabel("Contraseña:"));
        campos.add(txtContrasenaLogin);
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(campos, BorderLayout.CENTER);
        JPanel botones = new JPanel();
        botones.add(btnIngresar);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel armarRegistro() {
        JPanel campos = new JPanel(new GridLayout(3, 2, 5, 5));
        campos.add(new JLabel("Usuario (" + MINIMO_USUARIO + " a " + MAXIMO_USUARIO + " caracteres):"));
        campos.add(txtUsuarioRegistro);
        campos.add(new JLabel("Contraseña (mínimo " + MINIMO_CONTRASENA + "):"));
        campos.add(txtContrasenaRegistro);
        campos.add(new JLabel("Repetir contraseña:"));
        campos.add(txtRepetirContrasena);
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(campos, BorderLayout.CENTER);
        JPanel botones = new JPanel();
        botones.add(btnRegistrar);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    @Override
    public void setActionListener(ActionListener controlador) {
        btnIngresar.addActionListener(controlador);
        btnRegistrar.addActionListener(controlador);
    }

    @Override
    public String getUsuarioLogin() {
        return txtUsuarioLogin.getText().trim();
    }

    @Override
    public String getContrasenaLogin() {
        return new String(txtContrasenaLogin.getPassword());
    }

    @Override
    public String getUsuarioRegistro() {
        return txtUsuarioRegistro.getText().trim();
    }

    @Override
    public String getContrasenaRegistro() {
        return new String(txtContrasenaRegistro.getPassword());
    }

    @Override
    public String validarFormatoRegistro() {
        String usuario = getUsuarioRegistro();
        if (usuario.length() < MINIMO_USUARIO || usuario.length() > MAXIMO_USUARIO) {
            return "El usuario debe tener entre " + MINIMO_USUARIO + " y " + MAXIMO_USUARIO + " caracteres.";
        }
        if (usuario.contains(" ")) {
            return "El usuario no puede tener espacios.";
        }
        if (getContrasenaRegistro().length() < MINIMO_CONTRASENA) {
            return "La contraseña debe tener al menos " + MINIMO_CONTRASENA + " caracteres.";
        }
        if (!getContrasenaRegistro().equals(new String(txtRepetirContrasena.getPassword()))) {
            return "Las contraseñas no coinciden.";
        }
        return null;
    }

    @Override
    public void limpiarRegistro() {
        txtContrasenaRegistro.setText("");
        txtRepetirContrasena.setText("");
        btnRegistrar.setEnabled(false);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Acceso", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Acceso", JOptionPane.ERROR_MESSAGE);
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
