package ar.edu.unmdp.tpg.visor.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Punto de entrada del modelo del visor: crea la plataforma, lanza un hilo por cada
 * nave o generadora nueva (aun con la simulacion en marcha) y los detiene al cerrar.
 *
 * Es independiente del resto del juego: no usa Universo, Nave, misiones ni el tablero.
 */
public class UniversoParalelo {

    private final CanalDeEventos canal = new CanalDeEventos();
    private final PlataformaDeCarga plataforma;
    private final List<Thread> hilos = new ArrayList<>();
    private int cantidadNaves = 0;
    private int cantidadGeneradoras = 0;

    /**
     * @param capacidadPlataforma capacidad de la unica plataforma de carga (al menos 1)
     */
    public UniversoParalelo(int capacidadPlataforma) {
        this.plataforma = new PlataformaDeCarga(capacidadPlataforma, canal);
    }

    /**
     * Crea una nave consumidora y arranca su hilo.
     *
     * Postcondiciones:
     * - Hay un hilo nuevo corriendo el ciclo de la nave.
     * - Se publico el evento de ingreso.
     *
     * @return nombre de la nave creada
     */
    public synchronized String crearNave() {
        cantidadNaves++;
        String nombre = "Nave " + cantidadNaves;
        canal.publicar("¡Ingresó una nueva " + nombre + " al universo paralelo!");
        lanzar(new NaveConsumidora(nombre, plataforma, canal));
        return nombre;
    }

    /**
     * Crea una generadora de energia y arranca su hilo.
     *
     * Postcondiciones:
     * - Hay un hilo nuevo corriendo el ciclo de la generadora.
     * - Se publico el evento de ingreso.
     *
     * @return nombre de la generadora creada
     */
    public synchronized String crearGeneradora() {
        cantidadGeneradoras++;
        String nombre = "Generadora " + cantidadGeneradoras;
        canal.publicar("¡Ingresó una nueva " + nombre + " al universo paralelo!");
        lanzar(new GeneradoraDeEnergia(nombre, plataforma, canal));
        return nombre;
    }

    /**
     * Arranca el hilo con start(), como en el main de la teoria. start() crea el hilo
     * nuevo, que ejecuta run(); llamar a run() directamente lo ejecutaria en el hilo
     * que llama (el de Swing) y congelaria la ventana.
     * Son daemon: si se cierra la aplicacion, no la mantienen viva.
     */
    private void lanzar(Thread hilo) {
        hilo.setDaemon(true);
        hilos.add(hilo);
        hilo.start();
    }

    /**
     * Detiene la simulacion: interrumpe todos los hilos (los que estaban en wait o
     * sleep reciben InterruptedException y terminan).
     */
    public synchronized void detenerTodo() {
        for (Thread hilo : hilos) {
            hilo.interrupt();
        }
        hilos.clear();
    }

    public CanalDeEventos getCanal() {
        return canal;
    }

    public PlataformaDeCarga getPlataforma() {
        return plataforma;
    }

    public synchronized int getCantidadNaves() {
        return cantidadNaves;
    }

    public synchronized int getCantidadGeneradoras() {
        return cantidadGeneradoras;
    }
}
