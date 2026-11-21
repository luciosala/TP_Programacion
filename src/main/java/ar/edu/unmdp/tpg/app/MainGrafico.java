package ar.edu.unmdp.tpg.app;

import ar.edu.unmdp.tpg.acceso.ServicioDeAcceso;
import ar.edu.unmdp.tpg.aplicacion.ModeloAplicacion;
import ar.edu.unmdp.tpg.controlador.ControladorAcceso;
import ar.edu.unmdp.tpg.controlador.ControladorControl;
import ar.edu.unmdp.tpg.controlador.ControladorCreacion;
import ar.edu.unmdp.tpg.controlador.ControladorTripulantes;
import ar.edu.unmdp.tpg.persistencia.ConexionBD;
import ar.edu.unmdp.tpg.persistencia.PersistenciaException;
import ar.edu.unmdp.tpg.persistencia.UsuarioDAOJDBC;
import ar.edu.unmdp.tpg.visor.controlador.ControladorVisor;
import ar.edu.unmdp.tpg.visor.modelo.UniversoParalelo;
import ar.edu.unmdp.tpg.visor.vista.PanelVisor;
import ar.edu.unmdp.tpg.vista.PanelControlNave;
import ar.edu.unmdp.tpg.vista.PanelCreacion;
import ar.edu.unmdp.tpg.vista.PanelTripulantes;
import ar.edu.unmdp.tpg.vista.VentanaAcceso;
import ar.edu.unmdp.tpg.vista.VentanaPrincipal;

import javax.swing.JOptionPane;

/**
 * Programa principal con interfaz gráfica (Entrega 2). Arma el MVC: crea los
 * modelos, las vistas y los controladores, registra las vistas como observadoras
 * de los modelos y muestra la ventana de acceso.
 */
public class MainGrafico {

    /** Capacidad de la Plataforma de Carga del visor. */
    public static final int CAPACIDAD_PLATAFORMA = 3;

    public static void main(String[] args) {
        // Persistencia: solo las credenciales de los usuarios, en H2 (DAO/DTO + JDBC)
        ConexionBD conexion = ConexionBD.porDefecto();
        try {
            conexion.inicializarEsquema();
        } catch (PersistenciaException error) {
            JOptionPane.showMessageDialog(null, error.getMessage(), "Base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ServicioDeAcceso servicio = new ServicioDeAcceso(new UsuarioDAOJDBC(conexion));

        // Modelos
        ModeloAplicacion modelo = new ModeloAplicacion();
        UniversoParalelo universoParalelo = new UniversoParalelo(CAPACIDAD_PLATAFORMA);

        // Vistas
        PanelCreacion creacion = new PanelCreacion(modelo);
        PanelTripulantes tripulantes = new PanelTripulantes();
        PanelControlNave control = new PanelControlNave(modelo);
        PanelVisor visor = new PanelVisor(CAPACIDAD_PLATAFORMA);
        VentanaPrincipal principal = new VentanaPrincipal(creacion, tripulantes, control, visor);
        VentanaAcceso acceso = new VentanaAcceso();

        // Observer: las vistas se registran en los modelos (el modelo no las conoce)
        modelo.agregarObservador(creacion);
        modelo.agregarObservador(control);
        universoParalelo.getCanal().agregarObservador(visor);

        // Controladores (MVC): cada uno se vincula con los botones de su vista
        ControladorTripulantes controladorTripulantes = new ControladorTripulantes(tripulantes, modelo);
        new ControladorCreacion(creacion, modelo, controladorTripulantes);
        new ControladorControl(control, modelo);
        new ControladorVisor(visor, universoParalelo);
        new ControladorAcceso(acceso, principal, servicio);

        acceso.mostrar();
    }
}
