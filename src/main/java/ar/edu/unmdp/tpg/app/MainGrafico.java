package ar.edu.unmdp.tpg.app;

import ar.edu.unmdp.tpg.acceso.ServicioDeAcceso;
import ar.edu.unmdp.tpg.aplicacion.ModeloAplicacion;
import ar.edu.unmdp.tpg.controlador.ControladorAcceso;
import ar.edu.unmdp.tpg.controlador.ControladorControl;
import ar.edu.unmdp.tpg.controlador.ControladorCreacion;
import ar.edu.unmdp.tpg.controlador.ControladorTripulantes;
import ar.edu.unmdp.tpg.excepciones.NaveException;
import ar.edu.unmdp.tpg.persistencia.ConexionBD;
import ar.edu.unmdp.tpg.persistencia.PersistenciaException;
import ar.edu.unmdp.tpg.persistencia.UsuarioDAO;
import ar.edu.unmdp.tpg.persistencia.UsuarioDAOEnMemoria;
import ar.edu.unmdp.tpg.persistencia.UsuarioDAOJDBC;
import ar.edu.unmdp.tpg.tripulacion.Origen;
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
 *
 * Si se lo ejecuta con el argumento "demo" arranca con una nave ya creada y
 * tripulada, para no cargarla a mano en la defensa (ejecutar_demo.bat).
 */
public class MainGrafico {

    /** Capacidad de la Plataforma de Carga del visor. */
    public static final int CAPACIDAD_PLATAFORMA = 3;

    public static void main(String[] args) {
        // Persistencia: solo las credenciales de los usuarios, en H2 (DAO/DTO + JDBC)
        ServicioDeAcceso servicio = new ServicioDeAcceso(crearUsuarioDAO());

        // Modelos
        ModeloAplicacion modelo = new ModeloAplicacion();
        if (args.length > 0 && args[0].equalsIgnoreCase("demo")) {
            cargarNaveDeDemo(modelo);
        }
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

    /**
     * Si la base H2 está disponible los usuarios quedan guardados entre ejecuciones.
     * Si no (falta el jar de H2 en lib, ver lib/LEEME.txt), se avisa y se usa el DAO
     * en memoria: el programa funciona igual, pero los usuarios duran lo que dura la ejecución.
     */
    private static UsuarioDAO crearUsuarioDAO() {
        try {
            ConexionBD conexion = ConexionBD.porDefecto();
            conexion.inicializarEsquema();
            return new UsuarioDAOJDBC(conexion);
        } catch (PersistenciaException error) {
            JOptionPane.showMessageDialog(null,
                "No se pudo abrir la base de datos H2 (" + error.getMessage() + ").\n"
                + "Los usuarios se guardarán solo en memoria durante esta ejecución.",
                "Base de datos", JOptionPane.WARNING_MESSAGE);
            return new UsuarioDAOEnMemoria();
        }
    }

    /** Nave de combate COM-1 con capitán y cuatro tripulantes más, lista para iniciar misiones. */
    private static void cargarNaveDeDemo(ModeloAplicacion modelo) {
        try {
            modelo.crearNave("COM-1", "combate");
            modelo.asignarTripulante("T1", "Ana", "Sol", "Capitán", Origen.TERRICOLA, 5);
            modelo.asignarTripulante("T2", "Bruno", "Luz", "Consejero", Origen.VULCANO, 2);
            modelo.asignarTripulante("T3", "Carla", "Mora", "Teniente", Origen.MARCIANO, 1);
            modelo.asignarTripulante("T4", "Dante", "Paz", "Alférez", Origen.TERRICOLA, 0);
            modelo.asignarTripulante("T5", "Eva", "Quiroga", "Alférez", Origen.VULCANO, 3);
        } catch (NaveException error) {
            System.err.println("No se pudo cargar la nave de demo: " + error.getMessage());
        }
    }
}
