package ar.edu.unmdp.tpg.visor.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Punto de entrada del modelo del visor: crea la plataforma, arranca un hilo por cada
 * nave o generadora nueva (aun con la simulacion en marcha) y los detiene al cerrar.
 * Guarda los hilos en una lista solo para poder interrumpirlos cuando se cierra el visor.
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
        // Igual que en el main de la teoria: se crea el hilo con la tarea y se arranca con start()
        Thread hilo = new Thread(new NaveConsumidora(nombre, plataforma, canal), nombre);
        hilos.add(hilo);
        hilo.start();
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
        Thread hilo = new Thread(new GeneradoraDeEnergia(nombre, plataforma, canal), nombre);
        hilos.add(hilo);
        hilo.start();
        return nombre;
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
