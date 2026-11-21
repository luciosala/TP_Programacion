package ar.edu.unmdp.tpg.gui.controlador;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.bitacora.EventoBitacora;
import ar.edu.unmdp.tpg.destino.Observador;
import ar.edu.unmdp.tpg.destino.Posicion;
import ar.edu.unmdp.tpg.destino.Tablero;
import ar.edu.unmdp.tpg.elementos.ElementoDelUniverso;
import ar.edu.unmdp.tpg.excepciones.NaveException;
import ar.edu.unmdp.tpg.excepciones.NaveNoRegistradaException;
import ar.edu.unmdp.tpg.gui.vista.IVistaControl;
import ar.edu.unmdp.tpg.mision.InformeMision;
import ar.edu.unmdp.tpg.mision.Mision;
import ar.edu.unmdp.tpg.mision.MisionIntercepcion;
import ar.edu.unmdp.tpg.mision.MisionRecoleccion;
import ar.edu.unmdp.tpg.mision.MisionRetornoSeguro;
import ar.edu.unmdp.tpg.naves.Nave;
import ar.edu.unmdp.tpg.universo.Universo;

import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del control de la nave. Junta los dos patrones de la consigna:
 *
 * - MVC: implementa ActionListener y atiende lo que hace el usuario (elegir nave,
 *   iniciar mision, mantenimiento), llamando al modelo a traves del asistente.
 * - Observer: implementa Observador del Tablero. Cada vez que el tablero cambia
 *   (la nave se mueve o Destino termina su turno) le llega actualizar(), y ahi
 *   refresca en la vista el tablero, los recursos y la bitacora.
 *
 * La mision corre en un hilo aparte: tiene Thread.sleep entre turnos, y si corriera
 * en el hilo de Swing la ventana quedaria congelada hasta que termine. Como las
 * notificaciones del tablero llegan desde ese hilo, los cambios en la vista se
 * encolan con SwingUtilities.invokeLater (igual que en el visor).
 */
public class ControladorControl implements ActionListener, Observador {

    private final IVistaControl vista;
    private final Universo universo;
    private final Tablero tablero;
    private AsistenteDeComando asistente;    // nave con la que se esta trabajando
    private Mision misionEnCurso;            // la ultima mision iniciada, para dibujar su objetivo y su ruta
    private boolean hayMisionEnCurso = false;

    public ControladorControl(IVistaControl vista, Universo universo, Tablero tablero) {
        this.vista = vista;
        this.universo = universo;
        this.tablero = tablero;
        vista.setActionListener(this);
        tablero.agregarObservador(this);        // Observer: el controlador escucha al tablero
        vista.mostrarNaves(universo.idsDeNaves());
        refrescar();
    }

    /** Lo llama el main cuando se crean naves en otra seccion. */
    public void actualizarNaves() {
        vista.mostrarNaves(universo.idsDeNaves());
    }

    // ---------- MVC: acciones del usuario ----------

    @Override
    public void actionPerformed(ActionEvent e) {
        if (IVistaControl.SELECCIONAR_NAVE.equals(e.getActionCommand())) {
            seleccionarNave();
        } else if (IVistaControl.INICIAR_MISION.equals(e.getActionCommand())) {
            iniciarMision();
        } else if (IVistaControl.MANTENIMIENTO.equals(e.getActionCommand())) {
            realizarMantenimiento();
        } else if (IVistaControl.CARGAR_COMBUSTIBLE.equals(e.getActionCommand())) {
            cargar(true);
        } else if (IVistaControl.CARGAR_ENERGIA.equals(e.getActionCommand())) {
            cargar(false);
        }
    }

    private void seleccionarNave() {
        String id = vista.getNaveSeleccionada();
        if (id == null) {
            asistente = null;
            return;
        }
        try {
            universo.seleccionarNave(id);              // el sistema opera una nave a la vez
            asistente = universo.getNaveActiva();
            refrescar();
        } catch (NaveNoRegistradaException error) {
            vista.mostrarError(error.getMessage());
        }
    }

    /** La base es el centro del tablero: donde la catedra pone la nave al crearlo y adonde vuelve M-03. */
    private Posicion base() {
        return new Posicion(tablero.getX() / 2, tablero.getY() / 2);
    }

    private boolean naveEnLaBase() {
        return tablero.getPosicionNave().equals(base());
    }

    private void realizarMantenimiento() {
        if (asistente == null) {
            vista.mostrarError("Seleccione una nave");
            return;
        }
        if (!naveEnLaBase()) {
            vista.mostrarError("La nave solo puede repararse en la base");
            return;
        }
        asistente.realizarMantenimiento();
        refrescar();
    }

    /** Carga combustible o energia. El modelo rechaza la carga si supera la capacidad (escenario D). */
    private void cargar(boolean combustible) {
        if (asistente == null) {
            vista.mostrarError("Seleccione una nave");
            return;
        }
        if (!naveEnLaBase()) {
            vista.mostrarError("La nave solo puede cargar en la base (vuelva con M-03)");
            return;
        }
        Integer cantidad = vista.getCantidadCarga();   // la vista ya aviso si el formato esta mal
        if (cantidad == null) {
            return;
        }
        try {
            if (combustible) {
                asistente.cargarCombustible(cantidad);
            } else {
                asistente.cargarEnergia(cantidad);
            }
        } catch (NaveException error) {
            vista.mostrarError(error.getMessage());     // por ejemplo, la carga supera la capacidad maxima
        }
        refrescar();
    }

    private void iniciarMision() {
        if (asistente == null) {
            vista.mostrarError("Seleccione una nave");
            return;
        }
        Mision mision = crearMision(vista.getMisionSeleccionada());
        misionEnCurso = mision;
        hayMisionEnCurso = true;
        vista.habilitarMision(false);

        // La mision corre en su propio hilo para no congelar la ventana (ver comentario de la clase).
        // Igual que en la teoria: una tarea Runnable, new Thread(tarea) y start().
        Thread hiloMision = new Thread(new EjecucionDeMision(mision), "Mision " + asistente.idNave());
        hiloMision.start();
    }

    /**
     * Tarea que ejecuta la mision en un hilo aparte. Cuando termina, le pasa el
     * informe (o el error) a la vista a traves del hilo de Swing.
     */
    private class EjecucionDeMision implements Runnable {

        private final Mision mision;

        EjecucionDeMision(Mision mision) {
            this.mision = mision;
        }

        @Override
        public void run() {
            String resultado;
            boolean exito;
            try {
                InformeMision informe = asistente.ejecutarMision(mision);
                resultado = informe.toString();
                exito = true;
            } catch (NaveException error) {
                resultado = error.getMessage();
                exito = false;
            }
            SwingUtilities.invokeLater(new MostrarResultado(resultado, exito));
        }
    }

    /** Lo que hay que hacer en el hilo de Swing cuando la mision termino. */
    private class MostrarResultado implements Runnable {

        private final String resultado;
        private final boolean exito;

        MostrarResultado(String resultado, boolean exito) {
            this.resultado = resultado;
            this.exito = exito;
        }

        @Override
        public void run() {
            if (exito) {
                vista.agregarInforme(resultado);
            } else {
                vista.mostrarError(resultado);
            }
            hayMisionEnCurso = false;
            vista.habilitarMision(true);
            refrescar();
        }
    }

    private Mision crearMision(int indice) {
        switch (indice) {
            case 0:  return new MisionIntercepcion(asistente, tablero, "Carguero Orion", "sin propulsión");
            case 1:  return new MisionRecoleccion(asistente, tablero, "Nebulosa K-7", "muestra de gas");
            default: return new MisionRetornoSeguro(asistente, tablero, "Base Andrómeda");
        }
    }

    // ---------- Observer: el tablero cambio ----------

    @Override
    public void actualizar() {
        // Llega desde el hilo de la mision: el refresco de la vista va al hilo de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                refrescar();
            }
        });
    }

    /** Vuelve a leer el modelo y se lo pasa a la vista. */
    private void refrescar() {
        if (asistente != null) {
            vista.mostrarRecursos(asistente.combustibleDisponible(), asistente.energiaDisponible(),
                asistente.desgasteActual(), asistente.gemasDisponibles(), asistente.estadoDelMotor(),
                asistente.naveDestruida());
            List<String> lineas = new ArrayList<>();
            for (EventoBitacora evento : asistente.consultarBitacora()) {
                lineas.add(evento.getCategoria() + " | " + evento.getDescripcion());
            }
            vista.mostrarBitacora(lineas);
        }
        Posicion nave = tablero.getPosicionNave();
        Posicion base = base();
        vista.mostrarTablero(etiquetasDelTablero(), nave.getX(), nave.getY(),
            new int[] {base.getX(), base.getY()}, objetivoDeLaMision(), rutaDeLaMision());
        vista.habilitarCarga(asistente != null && naveEnLaBase() && !hayMisionEnCurso);
    }

    /** {fila, columna} del objetivo de la mision en curso, o null si no hay mision preparada. */
    private int[] objetivoDeLaMision() {
        if (misionEnCurso == null || misionEnCurso.getObjetivo() == null) {
            return null;
        }
        Posicion objetivo = misionEnCurso.getObjetivo();
        return new int[] {objetivo.getX(), objetivo.getY()};
    }

    /** Casillas {fila, columna} que le faltan recorrer a la nave en la mision en curso. */
    private List<int[]> rutaDeLaMision() {
        List<int[]> ruta = new ArrayList<>();
        if (misionEnCurso != null) {
            for (Posicion p : misionEnCurso.getRutaPendiente()) {
                ruta.add(new int[] {p.getX(), p.getY()});
            }
        }
        return ruta;
    }

    /** Traduce los elementos del tablero a etiquetas cortas, que es lo unico que la vista entiende. */
    private String[][] etiquetasDelTablero() {
        ElementoDelUniverso[][] celdas = tablero.verTablero();
        String[][] etiquetas = new String[celdas.length][];
        for (int fila = 0; fila < celdas.length; fila++) {
            etiquetas[fila] = new String[celdas[fila].length];
            for (int columna = 0; columna < celdas[fila].length; columna++) {
                ElementoDelUniverso elemento = celdas[fila][columna];
                if (elemento instanceof Nave) {
                    etiquetas[fila][columna] = "N";
                } else if (elemento != null) {
                    etiquetas[fila][columna] = elemento.toString().substring(0, 1);   // "A" o "E"
                }
            }
        }
        return etiquetas;
    }
}
