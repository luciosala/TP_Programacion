package ar.edu.unmdp.tpg.gui;

import ar.edu.unmdp.tpg.acceso.ServicioDeAcceso;
import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.asistente.AsistenteDeComandoBasico;
import ar.edu.unmdp.tpg.bitacora.Bitacora;
import ar.edu.unmdp.tpg.destino.Tablero;
import ar.edu.unmdp.tpg.excepciones.NaveException;
import ar.edu.unmdp.tpg.gui.controlador.ControladorAcceso;
import ar.edu.unmdp.tpg.gui.controlador.ControladorControl;
import ar.edu.unmdp.tpg.gui.controlador.ControladorCreacion;
import ar.edu.unmdp.tpg.gui.controlador.ControladorPrincipal;
import ar.edu.unmdp.tpg.gui.controlador.ControladorTripulantes;
import ar.edu.unmdp.tpg.gui.vista.VentanaAcceso;
import ar.edu.unmdp.tpg.gui.vista.VentanaPrincipal;
import ar.edu.unmdp.tpg.naves.NaveFactory;
import ar.edu.unmdp.tpg.persistencia.ConexionBD;
import ar.edu.unmdp.tpg.persistencia.PersistenciaException;
import ar.edu.unmdp.tpg.persistencia.UsuarioDAO;
import ar.edu.unmdp.tpg.persistencia.UsuarioDAOEnMemoria;
import ar.edu.unmdp.tpg.persistencia.UsuarioDAOJDBC;
import ar.edu.unmdp.tpg.tripulacion.Alferez;
import ar.edu.unmdp.tpg.tripulacion.Capitan;
import ar.edu.unmdp.tpg.tripulacion.Consejero;
import ar.edu.unmdp.tpg.tripulacion.Origen;
import ar.edu.unmdp.tpg.tripulacion.Teniente;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;
import ar.edu.unmdp.tpg.universo.TableroInicial;
import ar.edu.unmdp.tpg.universo.Universo;

/**
 * Programa principal con interfaz grafica. Como en la teoria: el main crea el
 * modelo, la vista y el controlador, y los conecta. Ninguna clase del modelo
 * conoce a las vistas ni a los controladores.
 *
 * Primero se abre la pantalla de acceso. Cuando el login sale bien, el
 * ControladorAcceso llama a abrirPrincipal(), que arma la ventana principal.
 *
 * Ejecutar con: mvn compile exec:exec -Dprograma=ar.edu.unmdp.tpg.gui.MainGUI
 * Con el argumento "demo" arranca con una nave ya creada y tripulada (solo en
 * memoria: la consigna dice que las naves no se persisten).
 */
public class MainGUI {

    private static boolean modoDemo = false;

    public static void main(String[] args) {
        modoDemo = args.length > 0 && args[0].equalsIgnoreCase("demo");

        // Modelo
        ServicioDeAcceso servicio = new ServicioDeAcceso(crearUsuarioDAO());
        // Vista
        VentanaAcceso ventanaAcceso = new VentanaAcceso();
        // Controlador
        ControladorAcceso controladorAcceso = new ControladorAcceso(ventanaAcceso, servicio);

        ventanaAcceso.mostrar();
    }

    /** Usa la base H2 (DAO JDBC). Si la base no esta disponible, sigue con el DAO en memoria. */
    private static UsuarioDAO crearUsuarioDAO() {
        try {
            ConexionBD conexion = ConexionBD.porDefecto();
            conexion.inicializarEsquema();
            return new UsuarioDAOJDBC(conexion);
        } catch (PersistenciaException | RuntimeException e) {
            System.err.println("No se pudo usar la base H2 (" + e.getMessage() + "). Se usan usuarios en memoria.");
            return new UsuarioDAOEnMemoria();
        }
    }

    /**
     * Arma la ventana principal. Lo llama el ControladorAcceso cuando el usuario ingreso.
     * Hay un controlador por cada seccion (pestana) de la ventana.
     */
    public static void abrirPrincipal(String usuario) {
        // Modelo
        Universo universo = new Universo();
        Tablero tablero = TableroInicial.crear();
        if (modoDemo) {
            cargarNaveDeDemo(universo);
        }

        // Vista
        VentanaPrincipal ventana = new VentanaPrincipal(usuario);

        // Controladores
        ControladorTripulantes controladorTripulantes =
            new ControladorTripulantes(ventana.getVistaTripulantes(), universo);
        ControladorControl controladorControl =
            new ControladorControl(ventana.getVistaControl(), universo, tablero);
        ControladorCreacion controladorCreacion =
            new ControladorCreacion(ventana.getVistaCreacion(), universo, controladorTripulantes, controladorControl);
        ControladorPrincipal controladorPrincipal = new ControladorPrincipal(ventana);

        ventana.mostrar();
    }

    /**
     * Una nave de combate (80 de combustible, 100 de energia) con capitan y cuatro
     * tripulantes, lista para salir en mision. Es de combate porque es la que mas
     * recursos tiene: el auxilio de M-01 puede pedir hasta 50 de cada uno.
     */
    private static void cargarNaveDeDemo(Universo universo) {
        try {
            AsistenteDeComando asistente = new AsistenteDeComandoBasico(
                NaveFactory.createNave("COM-1", "combate"), new Bitacora());
            asistente.asignarTripulante(new Tripulante("T1", "Ana", "Sol", new Capitan(), Origen.TERRICOLA, 5));
            asistente.asignarTripulante(new Tripulante("T2", "Bruno", "Luz", new Consejero(), Origen.VULCANO, 2));
            asistente.asignarTripulante(new Tripulante("T3", "Carla", "Mora", new Teniente(), Origen.MARCIANO, 1));
            asistente.asignarTripulante(new Tripulante("T4", "Dante", "Paz", new Alferez(), Origen.TERRICOLA, 0));
            asistente.asignarTripulante(new Tripulante("T5", "Eva", "Quiroga", new Alferez(), Origen.VULCANO, 3));
            universo.registrar(asistente);
        } catch (NaveException e) {
            System.err.println("No se pudo cargar la nave de demo: " + e.getMessage());
        }
    }
}
