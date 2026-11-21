package ar.edu.unmdp.tpg.visor.modelo;

/**
 * Consumidor: cada nave desconocida corre en su propio hilo. Implementa Runnable (define
 * la tarea) y el hilo lo crea UniversoParalelo con new Thread(tarea, nombre).start(), como
 * en los ejemplos de la catedra. Repite el ciclo de la consigna una cantidad fija de veces:
 * 1) solicita una bateria, 2) intenta retirarla, 3) si no hay espera,
 * 4) la retira y la consume, 5) notifica (los pasos 2, 3 y 5 los resuelve
 * PlataformaDeCarga.retirar).
 *
 * No tiene nada que ver con las naves del juego (paquete naves): es una entidad
 * del universo paralelo, sin recursos, tripulacion ni motor.
 *
 * Cuando termina sus CANTIDAD_CONSUMOS vueltas, run() termina y el hilo muere.
 * Si se cierra el visor antes, se la interrumpe y tambien termina.
 */
public class NaveConsumidora implements Runnable {

    /** Cuantas baterias retira cada nave antes de terminar. */
    public static final int CANTIDAD_CONSUMOS = 10;

    /** Tiempo que tarda en usar cada bateria, en milisegundos. */
    public static final int TIEMPO_CONSUMO = 1500;

    private final String nombre;
    private final PlataformaDeCarga plataforma;
    private final CanalDeEventos canal;

    /**
     * @param nombre nombre para los eventos (por ejemplo, "Nave 1")
     * @param plataforma plataforma compartida de donde retira
     * @param canal canal donde publica sus eventos
     */
    public NaveConsumidora(String nombre, PlataformaDeCarga plataforma, CanalDeEventos canal) {
        this.nombre = nombre;
        this.plataforma = plataforma;
        this.canal = canal;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < CANTIDAD_CONSUMOS; i++) {
                canal.publicar(nombre + " solicitando batería.");
                Bateria bateria = plataforma.retirar(nombre);   // espera si no hay
                canal.publicar("Batería retirada y utilizada por " + nombre + ". (" + bateria + ")");
                Thread.sleep(TIEMPO_CONSUMO);                              // la consume
            }
            canal.publicar(nombre + " terminó: usó sus " + CANTIDAD_CONSUMOS + " baterías.");
        } catch (InterruptedException e) {
            // La interrumpieron (se cerro el visor): run() termina y el hilo muere.
        }
    }
}
