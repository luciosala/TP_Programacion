package ar.edu.unmdp.tpg.visor;

import ar.edu.unmdp.tpg.visor.controlador.ControladorVisor;
import ar.edu.unmdp.tpg.visor.modelo.UniversoParalelo;
import ar.edu.unmdp.tpg.visor.vista.VentanaVisor;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Arranca el visor del universo paralelo por separado del resto del juego.
 * Cuando exista el panel de control principal, alcanza con que su boton haga lo
 * mismo que abrir() (sin cerrar la aplicacion al cerrar el visor).
 */
public class MainVisor {

    public static final int CAPACIDAD_PLATAFORMA = 3;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaVisor vista = abrir(CAPACIDAD_PLATAFORMA);
            vista.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }

    /**
     * Arma el MVC + Observer del visor y muestra la ventana.
     *
     * @param capacidad capacidad de la plataforma de carga
     * @return la ventana creada
     */
    public static VentanaVisor abrir(int capacidad) {
        UniversoParalelo universo = new UniversoParalelo(capacidad);
        VentanaVisor vista = new VentanaVisor(capacidad);
        universo.getCanal().agregarObservador(vista);   // Observer: la vista escucha al modelo
        new ControladorVisor(vista, universo);          // MVC: el controlador escucha a la vista
        vista.mostrar();
        return vista;
    }
}
